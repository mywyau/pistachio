package controllers.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest

import models.desk.deskSpecifications.PrivateDesk
import models.responses.CreatedResponse
import org.http4s.circe.jsonEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

object DeskSpecificationsControllerConstants {

  val openingHours =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      openingTime = LocalTime.of(10, 0, 0),
      closingTime = LocalTime.of(10, 30, 0)
    )

  val testDeskSpecificationsRequest =
    UpdateDeskSpecificationsRequest(
      deskName = "Luxury supreme desk",
      description = Some("Some description"),
      deskType = PrivateDesk,
      quantity = 5,
      rules = Some("Please keep the desk clean and quiet."),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      openingHours = availability
    )
}
