package models.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.adts.*
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecifications
import models.office_listing.requests.OfficeListingRequest
import models.OpeningHours
import models.Monday
import models.Tuesday

object OfficeListingConstants {

  val testCreateOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = "businessId1",
      officeId = "officeId1",
      officeName = "Maginificanent Office",
      description = "some office description",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      openingHours = OfficeAvailability(
        List(
          OpeningHours(
            day = Monday,
            openingTime = LocalTime.of(10, 0, 0),
            closingTime = LocalTime.of(10, 30, 0)
          ),
          OpeningHours(
            day = Tuesday,
            openingTime = LocalTime.of(10, 0, 0),
            closingTime = LocalTime.of(10, 30, 0)
          )
        )
      ),
      rules = Some("Please keep the office clean and tidy.")
    )

  val testCreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = "businessId1",
      officeId = "officeId1",
      buildingName = Some("butter building"),
      floorNumber = Some("floor 1"),
      street = Some("Main street 123"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("County 123"),
      postcode = Some("123456"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  val testCreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = "businessId1",
      officeId = "officeId1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  val officeListingRequest =
    OfficeListingRequest(
      officeId = "officeId1",
      createOfficeAddressRequest = testCreateOfficeAddressRequest,
      createOfficeSpecificationsRequest = testCreateOfficeSpecificationsRequest,
      createOfficeContactDetailsRequest = testCreateOfficeContactDetailsRequest,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
}
