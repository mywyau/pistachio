package repositories.office

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import io.circe.syntax.*
import models.office.office_specs.OfficeSpecs
import models.office.adts.OfficeType

import java.sql.Timestamp
import java.time.LocalDateTime

trait OfficeSpecsRepositoryAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Option[OfficeSpecs]]

  def createSpecs(user: OfficeSpecs): F[Int]
}

class OfficeSpecsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeSpecsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val officeTypeMeta: Meta[OfficeType] = Meta[String].imap(OfficeType.fromString)(_.toString)

  override def findByBusinessId(businessId: String): F[Option[OfficeSpecs]] = {
    val findQuery: F[Option[OfficeSpecs]] =
      sql"SELECT * FROM office_specs WHERE business_id = $businessId"
        .query[OfficeSpecs]
        .option
        .transact(transactor)
    findQuery
  }

  override def createSpecs(officeSpecs: OfficeSpecs): F[Int] = {
    sql"""
      INSERT INTO office_specs (
        business_id,
        office_id,
        office_name,
        description,
        office_type,
        number_of_floors,
        capacity,
        amenities,
        availability,
        rules,
        created_at,
        updated_at
      ) VALUES (
        ${officeSpecs.businessId},
        ${officeSpecs.officeId},
        ${officeSpecs.officeName},
        ${officeSpecs.description},
        ${officeSpecs.officeType},
        ${officeSpecs.numberOfFloors},
        ${officeSpecs.capacity},
        ${officeSpecs.amenities},
        ${officeSpecs.availability.asJson.noSpaces}::jsonb,
        ${officeSpecs.rules},
        ${officeSpecs.createdAt},
        ${officeSpecs.updatedAt}
      )
    """
      .update
      .run
      .transact(transactor)
  }

}


object OfficeSpecsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): OfficeSpecsRepositoryImpl[F] =
    new OfficeSpecsRepositoryImpl[F](transactor)
}
