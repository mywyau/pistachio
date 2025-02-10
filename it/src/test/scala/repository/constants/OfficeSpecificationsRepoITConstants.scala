package repository.constants

import models.office.address_details.OfficeAddress
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.adts.OpenPlanOffice
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}

import java.time.{LocalDateTime, LocalTime}

object OfficeSpecificationsRepoITConstants {

  def testCreateOfficeSpecificationsRequest(businessId: String, officeId: String): CreateOfficeSpecificationsRequest = {
    CreateOfficeSpecificationsRequest(
      businessId = businessId,
      officeId = officeId,
      officeName = "Maginificanent Office",
      description = "some office description",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      openingHours =
        OfficeAvailability(
          days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
          openingTime = LocalTime.of(10, 0, 0),
          closingTime = LocalTime.of(10, 30, 0)
        ),
      rules = Some("Please keep the office clean and tidy.")
    )
  }


  def testOfficeSpecs(id: Option[Int], businessId: String, officeId: String): OfficeSpecifications = {
    OfficeSpecifications(
      id = Some(1),
      businessId = businessId,
      officeId = officeId,
      officeName = Some("Maginificanent Office"),
      description = Some("some office description"),
      officeType = Some(OpenPlanOffice),
      numberOfFloors = Some(3),
      totalDesks = Some(3),
      capacity = Some(50),
      amenities = Some(List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking")),
      openingHours =
        Some(OfficeAvailability(
          days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
          openingTime = LocalTime.of(10, 0, 0),
          closingTime = LocalTime.of(10, 30, 0)
        )),
      rules = Some("Please keep the office clean and tidy."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

}
