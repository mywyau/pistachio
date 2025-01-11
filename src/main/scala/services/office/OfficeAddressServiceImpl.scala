package services.office

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.*
import models.office.address_details.errors.*
import models.office.address_details.UpdateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.office.address_details.OfficeAddressPartial
import org.typelevel.log4cats.Logger
import repositories.office.OfficeAddressRepositoryAlgebra

trait OfficeAddressServiceAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[Either[OfficeAddressErrors, OfficeAddressPartial]]

  def update(officeId: String, officeAddress: UpdateOfficeAddressRequest): F[ValidatedNel[OfficeAddressErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class OfficeAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad : Logger](
  officeAddressRepository: OfficeAddressRepositoryAlgebra[F]
) extends OfficeAddressServiceAlgebra[F] {

  override def findByOfficeId(officeId: String): F[Either[OfficeAddressErrors, OfficeAddressPartial]] =
    officeAddressRepository
      .findByOfficeId(officeId)
      .flatMap {
        case Some(office) =>
          Concurrent[F].pure(Right(office))
        case None =>
          Concurrent[F].pure(Left(OfficeAddressNotFound))
      }

  override def update(officeId: String, request: UpdateOfficeAddressRequest): F[ValidatedNel[OfficeAddressErrors, DatabaseSuccess]] =
    officeAddressRepository
      .update(officeId, request)
      .flatMap {
        case Valid(response) =>
          Concurrent[F].pure(response.validNel)
        case Invalid(errors) =>
          Concurrent[F].pure(OfficeAddressNotUpdated.invalidNel)
      }
      .handleErrorWith { e =>
        Logger[F].error(e)(s"[OfficeAddressServiceImpl][update] Error updating office address for officeId: $officeId - ${e.getMessage}") *>
          Concurrent[F].pure(UnexpectedError.invalidNel)
      }

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeAddressRepository.delete(officeId)

}

object OfficeAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel : Monad : Logger](officeAddressRepository: OfficeAddressRepositoryAlgebra[F]): OfficeAddressServiceImpl[F] =
    new OfficeAddressServiceImpl[F](officeAddressRepository)
}
