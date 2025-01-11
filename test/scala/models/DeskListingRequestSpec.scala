package models

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.desk_listing.requests.DeskListingRequest
import models.desk_listing.Availability
import models.desk_listing.PrivateDesk
import weaver.SimpleIOSuite

object DeskListingRequestSpec extends SimpleIOSuite {

  val availability: Availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalTime.of(10, 0, 0),
      endTime = LocalTime.of(10, 30, 0)
    )

  val sampleDeskListingRequest: DeskListingRequest =
    DeskListingRequest(
      deskName = "Private Office Desk",
      description = Some("A comfortable desk in a private office space with all amenities included."),
      deskType = PrivateDesk,
      quantity = 5,
      pricePerHour = BigDecimal(15.50),
      pricePerDay = BigDecimal(120.00),
      rules = Some("Please keep the desk clean and quiet."),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      availability = availability
    )

  test("DeskListingRequest model encodes correctly to JSON") {

    val jsonResult = sampleDeskListingRequest.asJson

    val expectedJson =
      """
        |{
        |  "business_id": "business_123",
        |  "workspace_id": "workspace_456",
        |  "title": "Private Office Desk",
        |  "description": "A comfortable desk in a private office space with all amenities included.",
        |  "desk_type": "PrivateDesk",
        |  "quantity": 5,
        |  "price_per_hour": 15.50,
        |  "price_per_day": 120.00,
        |  "rules": "Please keep the desk clean and quiet.",
        |  "features": ["Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"],
        |  "availability": {
        |    "days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
        |    "startTime": "10:00:00",
        |    "endTime": "10:30:00"
        |  },
        |  "created_at": "2024-11-21T10:00:00",
        |  "updated_at": "2024-11-21T10:30:00"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }
}
