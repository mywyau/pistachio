package repositories.office

import cats.Monad
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.database.*
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.errors.OfficeContactDetailsErrors

import java.sql.Timestamp
import java.time.LocalDateTime

trait OfficeContactDetailsRepositoryAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[Option[OfficeContactDetails]]

  def create(officeContactDetails: OfficeContactDetails): F[ValidatedNel[SqlErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]
}

class OfficeContactDetailsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeContactDetailsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByOfficeId(officeId: String): F[Option[OfficeContactDetails]] = {
    val findQuery: F[Option[OfficeContactDetails]] =
      sql"SELECT * FROM office_contact_details WHERE office_id = $officeId"
        .query[OfficeContactDetails]
        .option
        .transact(transactor)
    findQuery
  }

  override def create(officeContactDetails: OfficeContactDetails): F[ValidatedNel[SqlErrors, Int]] = {
    sql"""
      INSERT INTO office_contact_details (
        business_id,
        office_id,
        primary_contact_first_name,
        primary_contact_last_name,
        contact_email,
        contact_number,
        created_at,
        updated_at
      ) VALUES (
        ${officeContactDetails.businessId},
        ${officeContactDetails.officeId},
        ${officeContactDetails.primaryContactFirstName},
        ${officeContactDetails.primaryContactLastName},
        ${officeContactDetails.contactEmail},
        ${officeContactDetails.contactNumber},
        ${officeContactDetails.createdAt},
        ${officeContactDetails.updatedAt}
      )
    """.update
      .run
      .transact(transactor)
      .attempt
      .map {
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
          UnknownError.invalidNel
      }
  }

  override def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]] = {
    val deleteQuery: Update0 =
      sql"""
        DELETE FROM office_contact_details
        WHERE office_id = $officeId
      """.update
    deleteQuery
      .run
      .transact(transactor)
      .attempt
      .map {
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


object OfficeContactDetailsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): OfficeContactDetailsRepositoryAlgebra[F] =
    new OfficeContactDetailsRepositoryImpl[F](transactor)
}
