package repository.business

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.business.adts.PrivateDesk
import models.business.specifications.BusinessAvailability
import models.business.specifications.BusinessSpecifications
import repositories.business.BusinessSpecificationsRepositoryImpl
import repository.fragments.business.BusinessSpecificationsRepoFragments.createBusinessSpecsTable
import repository.fragments.business.BusinessSpecificationsRepoFragments.insertBusinessSpecificationsData
import repository.fragments.business.BusinessSpecificationsRepoFragments.resetBusinessSpecsTable
import shared.TransactorResource
import weaver.GlobalRead
import weaver.IOSuite
import weaver.ResourceTag

class BusinessSpecificationsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = BusinessSpecificationsRepositoryImpl[IO]

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      createBusinessSpecsTable.update.run.transact(transactor.xa).void *>
        resetBusinessSpecsTable.update.run.transact(transactor.xa).void *>
        insertBusinessSpecificationsData.update.run.transact(transactor.xa).void
    )

  def sharedResource: Resource[IO, BusinessSpecificationsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      businessSpecsRepo = new BusinessSpecificationsRepositoryImpl[IO](transactor.xa)
      createSchemaIfNotPresent <- initializeSchema(transactor)
    } yield businessSpecsRepo

    setup
  }

  test(".findByBusinessId() - should return the business specifications if business_id exists for a previously created business specifications") { businessSpecsRepo =>

    val userId = "USER001"
    val businessId = "BUS001"

    val expectedResult =
      BusinessSpecifications(
        id = Some(1),
        userId = userId,
        businessId = businessId,
        businessName = Some("business_name_1"),
        description = Some("some desc1"),
        availability = Some(
          BusinessAvailability(
            days = List("Monday", "Friday"),
            startTime = LocalTime.of(9, 0, 0),
            endTime = LocalTime.of(17, 0, 0)
          )
        ),
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )

    for {
      businessSpecsOpt <- businessSpecsRepo.findByBusinessId(businessId)
    } yield expect(businessSpecsOpt == Some(expectedResult))
  }
}
