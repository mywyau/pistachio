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
import models.business.availability.requests.CreateBusinessAvailabilityRequest
import models.business.availability.requests.UpdateBusinessAvailabilityRequest
import models.business.availability.BusinessAvailabilityPartial
import models.database.*

trait BusinessAvailabilityRepositoryAlgebra[F[_]] {

  def findAll(businessId: String): F[Option[BusinessAvailabilityPartial]]

  def createDaysOpen(request: CreateBusinessAvailabilityRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def updateDaysOpen(request: CreateBusinessAvailabilityRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def updateOpeningHours(day: String, request: UpdateBusinessAvailabilityRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(day: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAll(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class BusinessAvailabilityRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessAvailabilityRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByBusinessId(businessId: String): F[Option[BusinessAvailabilityPartial]] = {
    val findQuery: F[Option[BusinessAvailabilityPartial]] =
      sql"""
         SELECT 
             user_id,
             business_id,
             building_name,
             floor_number,
             street,
             city,
             country,
             county,
             postcode,
             latitude,
             longitude
         FROM business_availability
         WHERE business_id = $businessId
       """.query[BusinessAvailabilityPartial].option.transact(transactor)

    findQuery
  }

  override def create(request: CreateBusinessAvailabilityRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      INSERT INTO business_availability (
        user_id,
        business_id,
        building_name,
        floor_number,
        street,
        city,
        country,
        county,
        postcode,
        latitude,
        longitude
      )
      VALUES (
        ${request.userId},
        ${request.businessId},
        ${request.buildingName},
        ${request.floorNumber},
        ${request.street},
        ${request.city},
        ${request.country},
        ${request.county},
        ${request.postcode},
        ${request.latitude},
        ${request.longitude}
        )
    """.update.run
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows == 1 =>
          CreateSuccess.validNel
        case Left(e: java.sql.SQLIntegrityConstraintViolationException) =>
          ConstraintViolation.invalidNel
        case Left(e: java.sql.SQLException) =>
          DatabaseError.invalidNel
        case Left(ex) =>
          UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
        case _ =>
          UnexpectedResultError.invalidNel
      }

  override def update(businessId: String, request: UpdateBusinessAvailabilityRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      UPDATE business_availability
      SET
          building_name = ${request.buildingName},
          floor_number = ${request.floorNumber},
          street = ${request.street},
          city = ${request.city},
          country = ${request.country},
          county = ${request.county},
          postcode = ${request.postcode},
          latitude = ${request.latitude},
          longitude = ${request.longitude},
          updated_at = ${LocalDateTime.now()}
      WHERE business_id = ${businessId}
    """.update.run
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows == 1 =>
          UpdateSuccess.validNel
        case Right(affectedRows) if affectedRows == 0 =>
          NotFoundError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "23503" =>
          ForeignKeyViolationError.invalidNel // Foreign key constraint violation
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "08001" =>
          DatabaseConnectionError.invalidNel // Database connection issue
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "22001" =>
          DataTooLongError.invalidNel // Data length exceeds column limit
        case Left(ex: java.sql.SQLException) =>
          SqlExecutionError(ex.getMessage).invalidNel // General SQL execution error
        case Left(ex) =>
          UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
        case _ =>
          UnexpectedResultError.invalidNel
      }

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
        DELETE FROM business_availability
        WHERE business_id = $businessId
      """.update

    deleteQuery.run.transact(transactor).attempt.map {
      case Right(affectedRows) if affectedRows == 1 =>
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

  override def deleteAllByUserId(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
         DELETE FROM business_availability
         WHERE user_id = $userId
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
