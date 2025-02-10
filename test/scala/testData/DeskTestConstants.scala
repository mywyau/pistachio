package testData

import models.Monday
import models.Tuesday
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.OpeningHours
import models.office.address_details.OfficeAddressPartial

import java.time.LocalDateTime
import java.time.LocalTime

object DeskTestConstants {

  val deskId1 = "deskId1"
  val deskId2 = "deskId2"
  val deskId3 = "deskId3"
  val deskId4 = "deskId4"
  val deskId5 = "deskId5"

  val deskName = "Luxury supreme desk"
  val description1 = "Some description"
  val description2 = "A comfortable desk in a private office space with all amenities included."

  val rules = "Please keep the desk clean and quiet."

  val sampleDeskListingCard: DeskListingCard =
    DeskListingCard(
      deskId = deskId1,
      deskName = deskName,
      description = description1
    )

  val availability: Availability =
    Availability(
      availability = List(
        OpeningHours(
          day = Monday,
          startTime = LocalTime.of(10, 0, 0),
          endTime = LocalTime.of(10, 30, 0)
        ),
        OpeningHours(
          day = Tuesday,
          startTime = LocalTime.of(10, 0, 0),
          endTime = LocalTime.of(10, 30, 0)
        )
      )
    )

  val sampleRetrievedDeskPricing: RetrievedDeskPricing =
    RetrievedDeskPricing(
      pricePerHour = Some(30.00),
      pricePerDay = Some(180.00),
      pricePerWeek = Some(450.00),
      pricePerMonth = Some(1000.00),
      pricePerYear = Some(9000.00)
    )



}
