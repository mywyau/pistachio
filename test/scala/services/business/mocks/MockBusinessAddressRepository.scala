package services.business.mocks

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import java.time.LocalDateTime

import models.business.address.CreateBusinessAddressRequest
import models.business.address.UpdateBusinessAddressRequest
import models.business.address.BusinessAddressPartial
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import repositories.business.BusinessAddressRepositoryAlgebra
import services.business.BusinessAddressService
import services.business.BusinessAddressServiceImpl
import weaver.SimpleIOSuite
import models.business.address.BusinessAddressPartial

class MockBusinessAddressRepository(
  existingBusinessAddress: Map[String, BusinessAddressPartial] = Map.empty
) extends BusinessAddressRepositoryAlgebra[IO] {

  def showAllUsers: IO[Map[String, BusinessAddressPartial]] = IO.pure(existingBusinessAddress)

  override def findByBusinessId(businessId: String): IO[Option[BusinessAddressPartial]] = IO.pure(existingBusinessAddress.get(businessId))

  override def create(request: CreateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO.pure(Valid(CreateSuccess))

  override def update(businessId: String, request: UpdateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def deleteAllByUserId(userId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???
}
