package repository.business.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.business_address.service.BusinessAddress
import repositories.business.BusinessAddressRepositoryAlgebra

import java.time.LocalDateTime

case class MockBusinessAddressRepository(ref: Ref[IO, List[BusinessAddress]]) extends BusinessAddressRepositoryAlgebra[IO] {

  override def findByUserId(userId: String): IO[Option[BusinessAddress]] =
    ref.get.map(_.find(_.userId == userId))

  override def createUserAddress(BusinessAddress: BusinessAddress): IO[Int] =
    ref.modify(addresses => (BusinessAddress :: addresses, 1))

}
