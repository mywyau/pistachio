package repositories.office

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.Monad
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.postgres.implicits.*
import doobie.util.meta.Meta
import io.circe.syntax.*
import java.sql.Timestamp
import java.time.LocalDateTime
import models.database.*
import models.office.adts.OfficeType
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.requests.UpdateOfficeSpecificationsRequest
import models.office.specifications.OfficeSpecifications

trait OfficeSpecificationsRepositoryAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[Option[OfficeSpecifications]]

  def create(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def update(officeId: String, request: UpdateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, Int]]

}

class OfficeSpecificationsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeSpecificationsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val officeTypeMeta: Meta[OfficeType] = Meta[String].imap(OfficeType.fromString)(_.toString)

  override def findByOfficeId(officeId: String): F[Option[OfficeSpecifications]] = {
    val findQuery: F[Option[OfficeSpecifications]] =
      sql"SELECT * FROM office_specifications WHERE office_id = $officeId".query[OfficeSpecifications].option.transact(transactor)
    findQuery
  }

  override def create(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    sql"""
      INSERT INTO office_specifications (
        business_id,
        office_id,
        office_name,
        description,
        office_type,
        number_of_floors,
        total_desks,
        capacity,
        amenities,
        availability,
        rules
      ) VALUES (
        ${createOfficeSpecificationsRequest.businessId},
        ${createOfficeSpecificationsRequest.officeId},
        ${createOfficeSpecificationsRequest.officeName},
        ${createOfficeSpecificationsRequest.description},
        ${createOfficeSpecificationsRequest.officeType},
        ${createOfficeSpecificationsRequest.numberOfFloors},
        ${createOfficeSpecificationsRequest.totalDesks},
        ${createOfficeSpecificationsRequest.capacity},
        ${createOfficeSpecificationsRequest.amenities},
        ${createOfficeSpecificationsRequest.availability.asJson.noSpaces}::jsonb,
        ${createOfficeSpecificationsRequest.rules}
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
      case Left(ex) =>
        UnknownError(ex.getMessage).invalidNel
    }

  override def update(officeId: String, request: UpdateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    sql"""
      UPDATE office_specifications
      SET
        office_name = ${request.officeName},
        description = ${request.description},
        office_type = ${request.officeType},
        number_of_floors = ${request.numberOfFloors},
        total_desks = ${request.totalDesks},
        capacity = ${request.capacity},
        amenities = ${request.amenities},
        availability = ${request.availability}::jsonb,
        rules = ${request.rules},
        updated_at = ${request.updatedAt}
      WHERE office_id = $officeId
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

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, Int]] = {
    val deleteQuery: Update0 =
      sql"""
          DELETE FROM office_specifications
          WHERE office_id = $officeId
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

object OfficeSpecificationsRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): OfficeSpecificationsRepositoryAlgebra[F] =
    new OfficeSpecificationsRepositoryImpl[F](transactor)
}
