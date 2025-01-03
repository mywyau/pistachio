package services.constants

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.database.DatabaseErrors
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import repositories.office.OfficeContactDetailsRepositoryAlgebra
import repository.business.mocks.MockBusinessContactDetailsRepository
import repository.office.mocks.MockOfficeContactDetailsRepository

import java.time.LocalDateTime
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest

object OfficeContactDetailsServiceConstants {

  def testOfficeContactDetails(id: Option[Int], businessId: String, office_id: String): OfficeContactDetails =
    OfficeContactDetails(
      id = Some(1),
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = Some("Michael"),
      primaryContactLastName = Some("Yau"),
      contactEmail = Some("mike@gmail.com"),
      contactNumber = Some("07402205071"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  def testCreateOfficeContactDetailsRequest(businessId: String, office_id: String): CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  class MockOfficeContactDetailsRepository(
                                            existingOfficeContactDetails: Map[String, OfficeContactDetails] = Map.empty
                                          ) extends OfficeContactDetailsRepositoryAlgebra[IO] {


    override def update(officeId: String, request: UpdateOfficeContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, Int]] = ???

    def showAllUsers: IO[Map[String, OfficeContactDetails]] = IO.pure(existingOfficeContactDetails)

    override def findByOfficeId(officeId: String): IO[Option[OfficeContactDetails]] = IO.pure(existingOfficeContactDetails.get(officeId))

    override def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, Int]] = IO(Valid(1))

    override def delete(officeId: String): IO[ValidatedNel[DatabaseErrors, Int]] = ???
  }

}
