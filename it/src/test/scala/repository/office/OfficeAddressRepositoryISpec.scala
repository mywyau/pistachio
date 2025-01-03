package repository.office

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.business.adts.PrivateDesk
import models.office.address_details.OfficeAddress
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.requests.UpdateOfficeAddressRequest
import models.office.office_listing.requests.OfficeListingRequest
import models.office.specifications.OfficeAvailability
import repositories.office.OfficeAddressRepositoryImpl
import repository.constants.OfficeAddressRepoITConstants.*
import repository.fragments.OfficeAddressRepoFragments.{createOfficeAddressTable, insertOfficeAddressTable, resetOfficeAddressTable}
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite}

import java.time.LocalDateTime
import models.database.UpdateSuccess
import cats.data.Validated.Valid

class OfficeAddressRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = OfficeAddressRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createOfficeAddressTable.update.run.transact(transactor.xa).void *>
        resetOfficeAddressTable.update.run.transact(transactor.xa).void *>
        insertOfficeAddressTable.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, OfficeAddressRepositoryImpl[IO]] = {
    val setup =
      for {
        transactor <- global.getOrFailR[TransactorResource]()
        officeAddressRepo = new OfficeAddressRepositoryImpl[IO](transactor.xa)
        createSchemaIfNotPresent <- initializeSchema(transactor)
      } yield officeAddressRepo

    setup
  }

  test(".findByBusinessId() - should return the office address if business_id exists for a previously created office address") { officeAddressRepo =>

    val expectedResult = testOfficeAddressPartial("business_id_1", "office_id_1")

    for {
      officeAddressOpt <- officeAddressRepo.findByOfficeId("office_id_1")
    } yield expect(officeAddressOpt == Some(expectedResult))
  }

  test(".update() - should return the office address if business_id exists for a previously created office address") { officeAddressRepo =>

    val businessId = "business_id_6"
    val officeId = "office_id_6"

    val createRequest = createInitialOfficeAddress(businessId, officeId)

    val request = {
      UpdateOfficeAddressRequest(
        buildingName = Some("Empire State Building"),
        floorNumber = Some("5th Floor"),
        street = Some("123 Main Street"),
        city = Some("New York"),
        country = Some("USA"),
        county = Some("Manhattan"),
        postcode = Some("10001"),
        latitude = Some(40.748817),
        longitude = Some(-73.985428),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    }

    for {
      officeAddressOpt <- officeAddressRepo.create(createRequest)
      officeAddressOpt <- officeAddressRepo.update(officeId, request)
    } yield expect(officeAddressOpt == Valid(UpdateSuccess))
  }
}
