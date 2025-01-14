package services.office

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.office.contact_details.errors.*
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetailsPartial
import repositories.office.OfficeContactDetailsRepositoryAlgebra

trait OfficeContactDetailsServiceAlgebra[F[_]] {

  def getByOfficeId(officeId: String): F[Either[OfficeContactDetailsErrors, OfficeContactDetailsPartial]]

  def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): F[ValidatedNel[OfficeContactDetailsErrors, DatabaseSuccess]]

  def update(officeId: String, officeAddress: UpdateOfficeContactDetailsRequest): F[ValidatedNel[OfficeContactDetailsErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class OfficeContactDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F]
) extends OfficeContactDetailsServiceAlgebra[F] {

  override def getByOfficeId(officeId: String): F[Either[OfficeContactDetailsErrors, OfficeContactDetailsPartial]] =
    officeContactDetailsRepo.findByOfficeId(officeId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(OfficeContactDetailsNotFound))
    }

  override def create(request: CreateOfficeContactDetailsRequest): F[ValidatedNel[OfficeContactDetailsErrors, DatabaseSuccess]] = {

    val contactDetailsCreation =
      officeContactDetailsRepo.create(request)

    contactDetailsCreation
      .map {
        case Validated.Valid(i) =>
          Valid(i)
        case contactDetailsResult =>
          val errors =
            List(contactDetailsResult.toEither.left.getOrElse(Nil))
          OfficeContactDetailsNotCreated.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(OfficeContactDetailsDatabaseError.invalidNel)
      }
  }

  override def update(officeId: String, request: UpdateOfficeContactDetailsRequest): F[ValidatedNel[OfficeContactDetailsErrors, DatabaseSuccess]] = {

    val updateContactDetails =
      officeContactDetailsRepo.update(officeId, request)

    updateContactDetails
      .map {
        case Validated.Valid(addressId) =>
          Valid(CreateSuccess)
        case contactDetailsResult =>
          val errors =
            List(contactDetailsResult.toEither.left.getOrElse(Nil))
          OfficeContactDetailsNotCreated.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(OfficeContactDetailsDatabaseError.invalidNel)
      }
  }

  override def delete(officeId: String) =
    officeContactDetailsRepo.delete(officeId)
}

object OfficeContactDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F]
  ): OfficeContactDetailsServiceImpl[F] =
    new OfficeContactDetailsServiceImpl[F](officeContactDetailsRepo)
}
