package testData

import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.OpeningHours
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.OfficeAddressPartial
import models.office.adts.OpenPlanOffice
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import models.Monday
import models.Tuesday
import testData.DeskTestConstants.description1
import testData.TestConstants.*

object OfficeTestConstants {

  val officeRules = "Please keep the office clean and tidy."

  val createOfficeAddressRequest: CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = businessId1,
      officeId = officeId1,
      buildingName = Some(buildingName1),
      floorNumber = Some(floorNumber1),
      street = Some(street1),
      city = Some(city1),
      country = Some(country1),
      county = Some(county1),
      postcode = Some(postcode1),
      latitude = Some(latitude1),
      longitude = Some(longitude1)
    )

  val officeAvailability1 =
    OfficeAvailability(
      List(
        OpeningHours(
          day = Monday,
          openingTime = openingTime0900,
          closingTime = closingTime1700
        ),
        OpeningHours(
          day = Tuesday,
          openingTime = openingTime0900,
          closingTime = closingTime1700
        )
      )
    )

  val createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = businessId1,
      officeId = officeId1,
      officeName = officeName1,
      description = officeDescription1,
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      availability = officeAvailability1,
      rules = Some(officeRules)
    )

  val createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId1,
      officeId = officeId1,
      primaryContactFirstName = primaryContactFirstName1,
      primaryContactLastName = primaryContactLastName1,
      contactEmail = contactEmail1,
      contactNumber = contactNumber1
    )
}
