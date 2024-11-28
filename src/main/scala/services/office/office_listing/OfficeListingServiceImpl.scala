package services.office.office_listing

import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import repositories.business.OfficeListingRepositoryAlgebra
import repositories.office.{OfficeAddressRepositoryAlgebra, OfficeContactDetailsRepositoryAlgebra, OfficeSpecsRepositoryAlgebra}
import services.office.office_listing.OfficeListingServiceAlgebra


class OfficeListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                              officeAddressRepo: OfficeAddressRepositoryAlgebra[F],
                                                                              officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F],
                                                                              officeSpecsRepo: OfficeSpecsRepositoryAlgebra[F]
                                                                            ) extends OfficeListingServiceAlgebra[F] {

  override def findByUserId(userId: String): F[Either[OfficeListingErrors, OfficeListing]] = {
    officeListingRepo.findByUserId(userId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(OfficeListingNotFound))
    }
  }

  override def createOffice(officeListing: OfficeListingRequest): F[Either[OfficeListingErrors, Int]] = {
    officeListingRepo.createOfficeToRent(officeListing).attempt.flatMap {
      case Right(id) =>
        if (id > 0) {
          Concurrent[F].pure(Right(id))
        } else {
          Concurrent[F].pure(Left(DatabaseError))
        }
      case Left(ex) =>
        Concurrent[F].pure(Left(DatabaseError))
    }
  }

}

object OfficeListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeListingRepo: OfficeListingRepositoryAlgebra[F]
                                                 ): OfficeListingServiceImpl[F] =
    new OfficeListingServiceImpl[F](officeListingRepo)
}

