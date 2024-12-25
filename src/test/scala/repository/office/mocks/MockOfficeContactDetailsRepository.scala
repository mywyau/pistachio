package repository.office.mocks

import cats.data.Validated.validNel
import cats.data.ValidatedNel
import cats.effect.IO
import cats.effect.kernel.Ref
import models.database.SqlErrors
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import repositories.office.OfficeContactDetailsRepositoryAlgebra

import java.time.LocalDateTime

case class MockOfficeContactDetailsRepository(ref: Ref[IO, List[OfficeContactDetails]]) extends OfficeContactDetailsRepositoryAlgebra[IO] {

  override def findByOfficeId(officeId: String): IO[Option[OfficeContactDetails]] =
    ref.get.map(_.find(_.officeId == officeId))

  override def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): IO[ValidatedNel[SqlErrors, Int]] =
    ref.modify { contactDetails =>
      val updatedList: List[OfficeContactDetails] =
        OfficeContactDetails(
          id = Some(1),
          businessId = createOfficeContactDetailsRequest.businessId,
          officeId = createOfficeContactDetailsRequest.officeId,
          primaryContactFirstName = createOfficeContactDetailsRequest.primaryContactFirstName,
          primaryContactLastName = createOfficeContactDetailsRequest.primaryContactLastName,
          contactEmail = createOfficeContactDetailsRequest.contactEmail,
          contactNumber = createOfficeContactDetailsRequest.contactNumber,
          createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        ) :: contactDetails

      (updatedList, validNel(1))
    }


  override def delete(officeId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
}
