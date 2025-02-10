package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.office.adts.*
import weaver.SimpleIOSuite
import testData.OfficeTestConstants.*


object OfficeSpecificationsSpec extends SimpleIOSuite {

  test("OfficeSpecifications model encodes correctly to JSON") {

    val jsonResult = officeSpecifications.asJson

    val expectedJson =
      """
        |{
        |   "id": 1,
        |   "businessId": "businessId1",
        |   "officeId": "officeId1",
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
        |   "rules": "Please keep the office clean and tidy.",
        |   "createdAt": "2025-01-01T00:00:00",
        |   "updatedAt": "2025-01-01T00:00:00"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
