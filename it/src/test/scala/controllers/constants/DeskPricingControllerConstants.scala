package controllers.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskPricing.UpdateDeskPricingRequest
import models.responses.CreatedResponse
import org.http4s.circe.jsonEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

object DeskPricingControllerConstants {

  val testUpdateRequest =
    UpdateDeskPricingRequest(
      pricePerHour = 15.0,
      pricePerDay = 100.0,
      pricePerWeek = 600.0,
      pricePerMonth = 2000.0,
      pricePerYear = 24000.0
    )

}
