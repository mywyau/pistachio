package services.business.specifications

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.business.specifications.errors.*
import models.business.specifications.requests.CreateBusinessSpecificationsRequest
import models.business.specifications.requests.UpdateBusinessSpecificationsRequest
import models.business.specifications.BusinessSpecificationsPartial
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import repositories.business.BusinessSpecificationsRepositoryAlgebra

trait BusinessSpecificationsServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessSpecificationsErrors, BusinessSpecificationsPartial]]

  def update(businessId: String, request: UpdateBusinessSpecificationsRequest): F[ValidatedNel[BusinessSpecificationsErrors, DatabaseSuccess]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class BusinessSpecificationsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessSpecificationsRepo: BusinessSpecificationsRepositoryAlgebra[F]
) extends BusinessSpecificationsServiceAlgebra[F] {

  override def getByBusinessId(businessId: String): F[Either[BusinessSpecificationsErrors, BusinessSpecificationsPartial]] =
    businessSpecificationsRepo
      .findByBusinessId(businessId)
      .flatMap {
        case Some(user) =>
          Concurrent[F].pure(Right(user))
        case None =>
          Concurrent[F].pure(Left(BusinessSpecificationsNotFound))
      }

  override def update(businessId: String, request: UpdateBusinessSpecificationsRequest): F[ValidatedNel[BusinessSpecificationsErrors, DatabaseSuccess]] = {

    val updateSpecifications =
      businessSpecificationsRepo.update(businessId, request)

    updateSpecifications
      .map {
        case Valid(specificationsId) =>
          Valid(CreateSuccess)
        case result =>
          val errors =
            List(result.toEither.left.getOrElse(Nil))
          BusinessSpecificationsNotCreated.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(BusinessSpecificationsDatabaseError.invalidNel)
      }
  }

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessSpecificationsRepo.delete(businessId)
}

object BusinessSpecificationsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessSpecificationsRepo: BusinessSpecificationsRepositoryAlgebra[F]
  ): BusinessSpecificationsServiceImpl[F] =
    new BusinessSpecificationsServiceImpl[F](businessSpecificationsRepo)
}
