package repositories.business

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.Monad
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import java.sql.Timestamp
import java.time.LocalDateTime
import models.business.availability.*
import models.database.*
import models.Day

trait BusinessAvailabilityRepositoryAlgebra[F[_]] {

  // def findAll(businessId: String): F[Option[BusinessAvailabilityPartial]]

  def createDaysOpen(businessId: String, request: CreateBusinessDaysRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def updateDaysOpen(businessId: String, request: UpdateBusinessDaysRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def createOpeningHours(businessId: String, request: CreateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def updateOpeningHours(businessId: String, day: String, request: UpdateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(businessId: String, day: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAll(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class BusinessAvailabilityRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessAvailabilityRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  // override def findByBusinessId(businessId: String): F[Option[BusinessAvailabilityPartial]] = {
  //   val findQuery: F[Option[BusinessAvailabilityPartial]] =
  //     sql"""
  //        SELECT
  //            user_id,
  //            business_id,
  //            building_name,
  //            floor_number,
  //            street,
  //            city,
  //            country,
  //            county,
  //            postcode,
  //            latitude,
  //            longitude
  //        FROM business_opening_hours
  //        WHERE business_id = $businessId
  //      """.query[BusinessAvailabilityPartial].option.transact(transactor)

  //   findQuery
  // }

  override def createDaysOpen(businessId: String, request: CreateBusinessDaysRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {

    val insertNew = Update[(String, Day)](
      "INSERT INTO business_opening_hours (business_id, weekday) VALUES (?, ?)"
    ).updateMany(request.days.map(day => (businessId, day))) // Use request.businessId

    insertNew
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows > 0 => UpdateSuccess.validNel
        case Right(_) => NotFoundError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "23505" =>
          ConstraintViolation.invalidNel // Handle unique constraint violations
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "23503" =>
          ForeignKeyViolationError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "08001" =>
          DatabaseConnectionError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "22001" =>
          DataTooLongError.invalidNel
        case Left(ex: java.sql.SQLException) =>
          SqlExecutionError(ex.getMessage).invalidNel
        case Left(ex) =>
          UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
      }
  }

  override def updateDaysOpen(
    businessId: String,
    request: UpdateBusinessDaysRequest
  ): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {

    val deleteExisting =
      sql"""
        DELETE FROM business_opening_hours WHERE business_id = $businessId
      """.update.run

    val insertNew = Update[(String, Day)](
      "INSERT INTO business_opening_hours (business_id, weekday) VALUES (?, ?)"
    ).updateMany(request.days.map(day => (businessId, day)))

    (for {
      _ <- deleteExisting
      affectedRows <- insertNew
    } yield affectedRows)
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows > 0 => UpdateSuccess.validNel
        case Right(_) => NotFoundError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "23503" =>
          ForeignKeyViolationError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "08001" =>
          DatabaseConnectionError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "22001" =>
          DataTooLongError.invalidNel
        case Left(ex: java.sql.SQLException) =>
          SqlExecutionError(ex.getMessage).invalidNel
        case Left(ex) =>
          UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
      }
  }

  override def createOpeningHours(businessId: String, request: CreateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def updateOpeningHours(businessId: String, day: String, request: UpdateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def delete(businessId: String, day: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
      DELETE FROM business_opening_hours
      WHERE business_id = $businessId
      AND weekday = $day
    """.update

    deleteQuery.run.transact(transactor).attempt.map {
      case Right(affectedRows) if affectedRows > 0 =>
        DeleteSuccess.validNel
      case Right(affectedRows) if affectedRows == 0 =>
        NotFoundError.invalidNel
      case Left(ex: java.sql.SQLException) if ex.getSQLState == "23503" =>
        ForeignKeyViolationError.invalidNel
      case Left(ex: java.sql.SQLException) if ex.getSQLState == "08001" =>
        DatabaseConnectionError.invalidNel
      case Left(ex: java.sql.SQLException) =>
        SqlExecutionError(ex.getMessage).invalidNel
      case Left(ex) =>
        UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
      case _ =>
        UnexpectedResultError.invalidNel
    }
  }

  override def deleteAll(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
         DELETE FROM business_opening_hours
         WHERE businessId = $businessId
       """.update

    deleteQuery.run.transact(transactor).attempt.map {
      case Right(affectedRows) if affectedRows > 0 =>
        DeleteSuccess.validNel
      case Right(affectedRows) if affectedRows == 0 =>
        NotFoundError.invalidNel
      case Left(ex: java.sql.SQLException) if ex.getSQLState == "23503" =>
        ForeignKeyViolationError.invalidNel
      case Left(ex: java.sql.SQLException) if ex.getSQLState == "08001" =>
        DatabaseConnectionError.invalidNel
      case Left(ex: java.sql.SQLException) =>
        SqlExecutionError(ex.getMessage).invalidNel
      case Left(ex) =>
        UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
      case _ =>
        UnexpectedResultError.invalidNel
    }
  }
}

object BusinessAvailabilityRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): BusinessAvailabilityRepositoryImpl[F] =
    new BusinessAvailabilityRepositoryImpl[F](transactor)
}
