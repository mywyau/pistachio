package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.OpeningHours
import models.desk.deskSpecifications.PrivateDesk
import models.Monday
import weaver.SimpleIOSuite
import models.Tuesday
import testData.DeskTestConstants.availability

object DeskSpecificationsPartialSpec extends SimpleIOSuite {

  val sampleDeskSpecificationsPartial: DeskSpecificationsPartial =
    DeskSpecificationsPartial(
      deskId = "desk-001",
      deskName = "Private Office Desk",
      description = Some("A comfortable desk in a private office space with all amenities included."),
      deskType = Some(PrivateDesk),
      quantity = Some(5),
      features = Some(List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair")),
      availability = Some(availability),
      rules = Some("Please keep the desk clean and quiet.")
    )

  test("DeskSpecificationsPartial model encodes correctly to JSON") {

    val jsonResult = sampleDeskSpecificationsPartial.asJson

    val expectedJson =
      """
        |{
        |  "deskId": "desk-001",
        |  "deskName": "Private Office Desk",
        |  "description": "A comfortable desk in a private office space with all amenities included.",
        |  "deskType": "PrivateDesk",
        |  "quantity": 5,
        |  "rules": "Please keep the desk clean and quiet.",
        |  "features": ["Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"],
        |  "availability": {
        |    [
        |       "day": Monday"
        |       "openingTime": "10:00:00",
        |       "closingTime": "10:30:00"
        |    ],
        |    [
        |       "day": Tuesday"
        |       "openingTime": "10:00:00",
        |       "closingTime": "10:30:00"
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
