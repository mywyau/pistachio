package repository.business.mocks

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
import cats.effect.IO
import cats.effect.kernel.Ref
import cats.implicits.*
import models.business.address.BusinessAddress
import models.business.address.requests.CreateBusinessAddressRequest
import models.database.*
import repositories.business.BusinessAddressRepositoryAlgebra

import java.time.LocalDateTime
import models.business.address.requests.UpdateBusinessAddressRequest

case class MockBusinessAddressRepository(ref: Ref[IO, List[BusinessAddress]]) extends BusinessAddressRepositoryAlgebra[IO] {

  override def update(businessId: String, request: UpdateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, Int]] = ???


  override def findByBusinessId(businessId: String): IO[Option[BusinessAddress]] =
    ref.get.map(_.find(_.businessId.contains(businessId)))

  override def createBusinessAddress(request: CreateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, Int]] =
    ref.modify(addresses => (
      BusinessAddress(
        Some(1),
        request.userId,
        request.businessId,
        request.businessName,
        request.buildingName,
        request.floorNumber,
        request.street,
        request.city,
        request.country,
        request.county,
        request.postcode,
        request.latitude,
        request.longitude,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ) :: addresses,
      Valid(1)
    )
    )


  override def deleteBusinessAddress(businessId: String): IO[ValidatedNel[DatabaseErrors, Int]] =
    ref.modify { addresses =>
      val (remainingAddresses, deletedCount) = addresses.partition(!_.businessId.contains(businessId)) match {
        case (remaining, deleted) if deleted.nonEmpty => (remaining, Valid(deleted.size))
        case (remaining, _) => (remaining, Validated.invalidNel(NotFoundError))
      }
      (remainingAddresses, deletedCount)
    }
}
