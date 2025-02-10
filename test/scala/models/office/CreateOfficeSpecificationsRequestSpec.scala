package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.office.adts.*
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import models.Monday
import models.Tuesday
import weaver.SimpleIOSuite
import testData.OfficeTestConstants.*

object CreateOfficeSpecificationsRequestSpec extends SimpleIOSuite {

  test("CreateOfficeSpecificationsRequest model encodes correctly to JSON") {

    val jsonResult = createOfficeSpecificationsRequest.asJson

    val expectedJson =
      """
        |{
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
        |   "rules": "Please keep the office clean and tidy."
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
      _ <- IO(println(jsonResult))
    } yield expect(jsonResult == expectedResult)
  }

}
