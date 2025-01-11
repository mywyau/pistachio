package services.desk_listing

import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.desk_listing.requests.DeskListingRequest
import models.desk_listing.service.DeskListing
import repositories.desk.DeskListingRepositoryAlgebra
import models.desk_listing.errors.{DeskListingNotFound, DeskListingErrors, DatabaseError}

class DeskListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](deskListingRepo: DeskListingRepositoryAlgebra[F]) extends DeskListingServiceAlgebra[F] {

  override def findByUserId(userId: String): F[Either[DeskListingErrors, DeskListing]] =
    deskListingRepo.findByUserId(userId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(DeskListingNotFound))
    }

  override def createDesk(deskListing: DeskListingRequest): F[Either[DeskListingErrors, Int]] =
    deskListingRepo.createDeskToRent(deskListing).attempt.flatMap {
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

object DeskListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel : Monad](deskListingRepo: DeskListingRepositoryAlgebra[F]): DeskListingServiceImpl[F] =
    new DeskListingServiceImpl[F](deskListingRepo)
}
