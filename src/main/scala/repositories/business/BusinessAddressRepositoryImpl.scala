package repositories.business

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.business.business_address.service.BusinessAddress

import java.sql.Timestamp
import java.time.LocalDateTime


trait BusinessAddressRepositoryAlgebra[F[_]] {

  def createUserAddress(user: BusinessAddress): F[Int]

  def findByUserId(userId: String): F[Option[BusinessAddress]]
}

class BusinessAddressRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessAddressRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)
  
  override def findByUserId(userId: String): F[Option[BusinessAddress]] = {
    val findQuery: F[Option[BusinessAddress]] =
      sql"SELECT * FROM business_address WHERE user_id = $userId"
        .query[BusinessAddress]
        .option
        .transact(transactor)
    findQuery
  }

  override def createUserAddress(businessAddress: BusinessAddress): F[Int] = {
    sql"""
      INSERT INTO business_address (
        user_id,
        address1,
        address2,
        city,
        country,
        county,
        postcode,
        created_at,
        updated_at
      )
      VALUES (
        ${businessAddress.userId},
        ${businessAddress.address1},
        ${businessAddress.address2},
        ${businessAddress.city},
        ${businessAddress.country},
        ${businessAddress.county},
        ${businessAddress.postcode},
        ${businessAddress.createdAt},
        ${businessAddress.updatedAt}
        )
    """.update
      .run
      .transact(transactor)
  }
}


object BusinessAddressRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): BusinessAddressRepositoryImpl[F] =
    new BusinessAddressRepositoryImpl[F](transactor)
}
