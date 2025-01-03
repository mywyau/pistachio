package repositories.business

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.Monad
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import io.circe.syntax.*
import java.sql.Timestamp
import java.time.LocalDateTime
import models.business.specifications.requests.CreateBusinessSpecificationsRequest
import models.business.specifications.requests.UpdateBusinessSpecificationsRequest
import models.business.specifications.BusinessSpecifications
import models.database.*
import models.office.specifications.requests.UpdateOfficeSpecificationsRequest

trait BusinessSpecificationsRepositoryAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Option[BusinessSpecifications]]

  def create(createBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def update(businessId: String, request: UpdateBusinessSpecificationsRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, Int]]
}

class BusinessSpecificationsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessSpecificationsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByBusinessId(businessId: String): F[Option[BusinessSpecifications]] = {
    val findQuery: F[Option[BusinessSpecifications]] =
      sql"SELECT * FROM business_specifications WHERE business_id = $businessId"
        .query[BusinessSpecifications]
        .option
        .transact(transactor)
    findQuery
  }

  override def create(createBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    sql"""
      INSERT INTO business_specifications (
        user_id,
        business_id,
        business_name,
        description,
        availability
      ) VALUES (
        ${createBusinessSpecificationsRequest.userId},
        ${createBusinessSpecificationsRequest.businessId},
        ${createBusinessSpecificationsRequest.businessName},
        ${createBusinessSpecificationsRequest.description},
        ${createBusinessSpecificationsRequest.availability.asJson.noSpaces}::jsonb
      )
    """.update.run.transact(transactor).attempt.map {
      case Right(rowsAffected) =>
        if (rowsAffected == 1) {
          rowsAffected.validNel
        } else {
          InsertionFailed.invalidNel
        }
      case Left(e: java.sql.SQLIntegrityConstraintViolationException) =>
        ConstraintViolation.invalidNel
      case Left(e: java.sql.SQLException) =>
        DatabaseError.invalidNel
      case Left(e) =>
        UnknownError(e.getMessage).invalidNel
    }

  override def update(businessId: String, request: UpdateBusinessSpecificationsRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    sql"""
        UPDATE business_specifications
        SET
        business_name = ${request.businessName},
        description = ${request.description},
        availability = ${request.availability.asJson.noSpaces}::jsonb,
        updated_at = ${request.updatedAt}
        WHERE business_id = $businessId
      """.update.run.transact(transactor).attempt.map {
      case Right(affectedRows) =>
        if (affectedRows > 0)
          affectedRows.validNel
        else
          NotFoundError.invalidNel
      case Left(ex: java.sql.SQLException) =>
        DatabaseError.invalidNel
      case Left(ex) =>
        UnknownError(ex.getMessage).invalidNel
    }

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, Int]] = {
    val deleteQuery: Update0 =
      sql"""
          DELETE FROM business_specifications
          WHERE business_id = $businessId
        """.update

    deleteQuery.run.transact(transactor).attempt.map {
      case Right(affectedRows) =>
        if (affectedRows > 0)
          affectedRows.validNel
        else
          NotFoundError.invalidNel
      case Left(ex) =>
        DeleteError.invalidNel
    }
  }
}

object BusinessSpecificationsRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): BusinessSpecificationsRepositoryAlgebra[F] =
    new BusinessSpecificationsRepositoryImpl[F](transactor)
}
