package services.business.contact_details

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.business.contact_details.errors.*
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.contact_details.requests.UpdateBusinessContactDetailsRequest
import models.business.contact_details.BusinessContactDetails
import models.database.DatabaseErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra

trait BusinessContactDetailsServiceAlgebra[F[_]] {

  def getContactDetailsByBusinessId(businessId: String): F[Either[BusinessContactDetailsErrors, BusinessContactDetails]]

  def create(businessContactDetails: CreateBusinessContactDetailsRequest): F[ValidatedNel[BusinessContactDetailsErrors, Int]]

  def update(businessId: String, request: UpdateBusinessContactDetailsRequest): F[ValidatedNel[BusinessContactDetailsErrors, Int]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, Int]]

}

class BusinessContactDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F]
) extends BusinessContactDetailsServiceAlgebra[F] {

  override def getContactDetailsByBusinessId(businessId: String): F[Either[BusinessContactDetailsErrors, BusinessContactDetails]] =
    businessContactDetailsRepo.findByBusinessId(businessId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(BusinessContactDetailsNotFound))
    }

  override def create(businessContactDetails: CreateBusinessContactDetailsRequest): F[ValidatedNel[BusinessContactDetailsErrors, Int]] = {

    val contactDetailsCreation: F[ValidatedNel[DatabaseErrors, Int]] =
      businessContactDetailsRepo.create(businessContactDetails)

    contactDetailsCreation
      .map {
        case Validated.Valid(i) =>
          Valid(i)
        case contactDetailsResult =>
          val errors =
            List(contactDetailsResult.toEither.left.getOrElse(Nil))
          BusinessContactDetailsNotCreated.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(BusinessContactDetailsDatabaseError.invalidNel)
      }
  }

  override def update(businessId: String, request: UpdateBusinessContactDetailsRequest): F[ValidatedNel[BusinessContactDetailsErrors, Int]] = {

    val updateContactDetails: F[ValidatedNel[DatabaseErrors, Int]] =
      businessContactDetailsRepo.update(businessId, request)

    updateContactDetails
      .map {
        case Valid(contactDetailsId) =>
          Valid(1)
        case contactDetailsResult =>
          val errors =
            List(contactDetailsResult.toEither.left.getOrElse(Nil))
          BusinessContactDetailsNotFound.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(BusinessContactDetailsNotFound.invalidNel)
      }
  }

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, Int]] =
    businessContactDetailsRepo.delete(businessId)
}

object BusinessContactDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F]
  ): BusinessContactDetailsServiceImpl[F] =
    new BusinessContactDetailsServiceImpl[F](businessContactDetailsRepo)
}
