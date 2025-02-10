package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.OpeningHours
import models.desk.deskSpecifications.PrivateDesk
import models.Monday
import weaver.SimpleIOSuite
import models.Tuesday
import testData.DeskTestConstants.*

object UpdateDeskSpecificationsRequestSpec extends SimpleIOSuite {

  val sampleUpdateRequest: UpdateDeskSpecificationsRequest =
    UpdateDeskSpecificationsRequest(
      deskName = deskName,
      description = Some(description2),
      deskType = PrivateDesk,
      quantity = 5,
      rules = Some(rules),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      availability = availability
    )

  test("UpdateDeskSpecificationsRequest model encodes correctly to JSON") {

    val jsonResult = sampleUpdateRequest.asJson

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
        |    [
        |       "day": Monday"
        |       "startTime": "10:00:00",
        |       "endTime": "10:30:00"
        |    ],
        |    [
        |       "day": Tuesday"
        |       "startTime": "10:00:00",
        |       "endTime": "10:30:00"
        |    ]
        |  }
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }
}
