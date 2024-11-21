package services.business_address

import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.business.business_address.errors.*
import models.business.business_address.service.BusinessAddress
import repositories.business.BusinessAddressRepositoryAlgebra


class BusinessAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
                                                                              ) extends BusinessAddressServiceAlgebra[F] {

  override def getAddressDetailsByUserId(userId: String): F[Either[BusinessAddressErrors, BusinessAddress]] = {
    businessAddressRepo.findByUserId(userId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(BusinessAddressNotFound))
    }
  }

  override def createAddress(businessAddress: BusinessAddress): F[Int] = {
    businessAddressRepo.createUserAddress(businessAddress)
  }
}

object BusinessAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
                                                 ): BusinessAddressServiceImpl[F] =
    new BusinessAddressServiceImpl[F](businessAddressRepo)
}

