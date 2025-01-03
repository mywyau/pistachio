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
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.address.requests.UpdateBusinessAddressRequest
import models.business.address.BusinessAddress
import models.database.*

trait BusinessAddressRepositoryAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Option[BusinessAddress]]

  def createBusinessAddress(request: CreateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def update(businessId: String, request: UpdateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def deleteBusinessAddress(businessId: String): F[ValidatedNel[DatabaseErrors, Int]]
}

class BusinessAddressRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessAddressRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByBusinessId(businessId: String): F[Option[BusinessAddress]] = {
    val findQuery: F[Option[BusinessAddress]] =
      sql"SELECT * FROM business_address WHERE business_id = $businessId".query[BusinessAddress].option.transact(transactor)
    findQuery
  }

  override def createBusinessAddress(request: CreateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    sql"""
      INSERT INTO business_address (
        user_id,
        business_id,
        business_name,
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
        ${request.businessName},
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

  override def update(businessId: String, request: UpdateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    sql"""
      UPDATE business_address
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
          updated_at = ${request.updatedAt}
      WHERE business_id = ${businessId}
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

  override def deleteBusinessAddress(businessId: String): F[ValidatedNel[DatabaseErrors, Int]] = {
    val deleteQuery: Update0 =
      sql"""
        DELETE FROM business_address
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

object BusinessAddressRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): BusinessAddressRepositoryImpl[F] =
    new BusinessAddressRepositoryImpl[F](transactor)
}
