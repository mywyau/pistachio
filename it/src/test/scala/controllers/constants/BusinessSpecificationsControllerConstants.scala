package controllers.constants

import cats.effect.*

import models.business.specifications.BusinessSpecifications
import models.business.specifications.BusinessSpecificationsPartial
import models.business.specifications.requests.CreateBusinessSpecificationsRequest

import java.time.LocalDateTime
import java.time.LocalTime

object BusinessSpecificationsControllerConstants {

  val testBusinessSpecs: BusinessSpecificationsPartial =
    BusinessSpecificationsPartial(
      userId = "userId1",
      businessId = "businessId1",
      businessName = Some("Example Business Name"),
      description = Some("some description"),
      openingHours = Some(
        BusinessAvailability(
          days = List("Monday", "Friday"),
          openingTime = LocalTime.of(9, 0, 0),
          closingTime = LocalTime.of(17, 0, 0)
        )
      )
    )

  def testCreateBusinessSpecificationsRequest(
    userId: String,
    businessId: String
  ): CreateBusinessSpecificationsRequest =
    CreateBusinessSpecificationsRequest(
      userId = userId,
      businessId = businessId,
      businessName = "Example Business Name",
      description = "some description",
      BusinessAvailability(
        days = List("Monday", "Tuesday"),
        openingTime = LocalTime.of(10, 0, 0),
        closingTime = LocalTime.of(10, 30, 0)
      )
    )
}
