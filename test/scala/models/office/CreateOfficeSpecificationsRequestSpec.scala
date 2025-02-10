package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskSpecifications.OpeningHours
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
        |   "businessId": "business_id_1",
        |   "officeId": "office_id_1",
        |   "officeName": "Modern Workspace",
        |   "description": "A vibrant office space in the heart of the city, ideal for teams or individuals.",
        |   "officeType": "OpenPlanOffice",
        |   "numberOfFloors": 3,
        |   "totalDesks": 3,
        |   "capacity": 50,
        |   "availability": {
        |     "days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
        |     "openingTime": "10:00:00",
        |     "closingTime": "10:30:00"
        |   },
        |   "amenities": ["Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"],
        |   "rules": "No smoking. Maintain cleanliness."
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
