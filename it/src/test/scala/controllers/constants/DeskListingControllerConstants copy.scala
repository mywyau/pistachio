package controllers.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskListing.requests.UpdateDeskListingRequest
import models.desk.deskListing.Availability
import models.desk.deskListing.PrivateDesk
import models.responses.CreatedResponse
import org.http4s.circe.jsonEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

object DeskListingControllerConstants {

  val availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalTime.of(10, 0, 0),
      endTime = LocalTime.of(10, 30, 0)
    )

  val testDeskListingRequest =
    UpdateDeskListingRequest(
      deskName = "Private Office Desk",
      description = Some("A comfortable desk in a private office space with all amenities included."),
      deskType = PrivateDesk,
      quantity = 5,
      rules = Some("Please keep the desk clean and quiet."),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      availability = availability
    )
}
