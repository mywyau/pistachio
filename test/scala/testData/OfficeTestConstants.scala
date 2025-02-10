package testData

import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.OpeningHours
import models.office.address_details.OfficeAddressPartial
import models.office.adts.OpenPlanOffice
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import models.Monday
import models.Tuesday
import models.office.address_details.requests.CreateOfficeAddressRequest

object OfficeTestConstants {

  val createOfficeAddressRequest: CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      buildingName = Some("build_123"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  val createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = "business_id_1",
      officeId = "office_id_1",
      officeName = "Modern Workspace",
      description = "A vibrant office space in the heart of the city, ideal for teams or individuals.",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      availability = OfficeAvailability(
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
      rules = Some("No smoking. Maintain cleanliness.")
    )

}
