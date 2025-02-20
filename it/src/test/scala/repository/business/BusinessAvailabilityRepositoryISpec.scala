package repository.business

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.*
import models.business.availability.*
import models.database.*
import models.desk.deskSpecifications.PrivateDesk
import repositories.business.BusinessAvailabilityRepositoryImpl
import repository.fragments.business.BusinessAvailabilityRepoFragments.*
import shared.TransactorResource
import testData.BusinessTestConstants.*
import testData.TestConstants.*
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

import java.time.LocalDateTime
import java.time.LocalTime

class BusinessAvailabilityRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = BusinessAvailabilityRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createBusinessAvailabilityTable.update.run.transact(transactor.xa).void *>
        resetBusinessAvailabilityTable.update.run.transact(transactor.xa).void *>
        insertBusinessAvailabilityData.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, BusinessAvailabilityRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessAvailabilityRepo = new BusinessAvailabilityRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield businessAvailabilityRepo

    setup
  }

  test(".createDaysOpen() - should insert/create entries for the data in postgres for each day in request list") { businessAvailabilityRepo =>

    val request =
      CreateBusinessDaysRequest(
        userId = userId1,
        businessId = businessId4,
        days = List(Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)
      )

    val expectedResult = CreateSuccess

    for {
      businessAvailabilityOpt <- businessAvailabilityRepo.createDaysOpen(request)
    } yield expect(businessAvailabilityOpt == Valid(expectedResult))
  }

  test(".updateOpeningHours() - should update the opening/closing time entries in postgres for the desired day and business") { businessAvailabilityRepo =>

    val createRequest =
      CreateBusinessDaysRequest(
        userId = userId1,
        businessId = businessId5,
        days = List(Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)
      )

    val expectedCreatedResult = CreateSuccess

    val updateRequest =
      UpdateBusinessOpeningHoursRequest(
        userId = userId1,
        businessId = businessId5,
        day = Monday,
        openingTime = LocalTime.of(9, 0, 0),
        closingTime = LocalTime.of(17, 0, 0)
      )

    val expectedUpdateResult = UpdateSuccess

    for {
      createResult <- businessAvailabilityRepo.createDaysOpen(createRequest)
      updateResult <- businessAvailabilityRepo.updateOpeningHours(updateRequest)
    } yield expect.all(
      createResult == Valid(expectedCreatedResult),
      updateResult == Valid(expectedUpdateResult)
    )
  }
}
