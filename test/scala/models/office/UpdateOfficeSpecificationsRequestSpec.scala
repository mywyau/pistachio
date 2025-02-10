package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import testData.OfficeTestConstants.*
import weaver.SimpleIOSuite

object UpdateOfficeSpecificationsRequestSpec extends SimpleIOSuite {

  test("UpdateOfficeSpecificationsRequest model encodes correctly to JSON") {

    val jsonResult = updateOfficeSpecificationsRequest.asJson

    val expectedJson =
      """
        |{
        |   "officeName": "Maginificanent Office",
        |   "description": "some office description",
        |   "officeType": "OpenPlanOffice",
        |   "numberOfFloors": 3,
        |   "totalDesks": 3,
        |   "capacity": 50,
        |   "availability": {
        |     [
        |       {
        |          "day": Monday"
        |          "openingTime": "09:00:00",
        |          "closingTime": "17:00:00"
        |       },
        |       {
        |          "day": Tuesday"
        |          "openingTime": "09:00:00",
        |          "closingTime": "17:00:00"
        |       }
        |     ]
        |   },
        |   "amenities": ["Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"],
        |   "rules": "Please keep the office clean and tidy."
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
