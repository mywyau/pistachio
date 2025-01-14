package models

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.deskListing.PrivateDesk
import models.deskListing.requests.UpdateDeskListingRequest
import models.deskListing.Availability
import weaver.SimpleIOSuite

object DeskListingRequestSpec extends SimpleIOSuite {

  val availability: Availability =
    Availability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalTime.of(10, 0, 0),
      endTime = LocalTime.of(10, 30, 0)
    )

  val sampleDeskListingRequest: UpdateDeskListingRequest =
    UpdateDeskListingRequest(
      deskName = "Private Office Desk",
      description = Some("A comfortable desk in a private office space with all amenities included."),
      deskType = PrivateDesk,
      quantity = 5,
      rules = Some("Please keep the desk clean and quiet."),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      availability = availability
    )

  test("DeskListingRequest model encodes correctly to JSON") {

    val jsonResult = sampleDeskListingRequest.asJson

    val expectedJson =
      """
        |{
        |  "deskName": "Private Office Desk",
        |  "description": "A comfortable desk in a private office space with all amenities included.",
        |  "deskType": "PrivateDesk",
        |  "quantity": 5,
        |  "rules": "Please keep the desk clean and quiet.",
        |  "features": ["Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"],
        |  "availability": {
        |    "days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
        |    "startTime": "10:00:00",
        |    "endTime": "10:30:00"
        |  }
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }
}
