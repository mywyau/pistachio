package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskSpecifications.OpeningHours
import models.office.adts.*
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecifications
import models.Monday
import models.Tuesday
import weaver.SimpleIOSuite

object OfficeSpecificationsSpec extends SimpleIOSuite {

  val officeSpecifications: OfficeSpecifications =
    OfficeSpecifications(
      id = Some(1),
      businessId = "business_id_1",
      officeId = "office_id_1",
      officeName = Some("Modern Workspace"),
      description = Some("A vibrant office space in the heart of the city, ideal for teams or individuals."),
      officeType = Some(OpenPlanOffice),
      numberOfFloors = Some(3),
      totalDesks = Some(3),
      capacity = Some(50),
      amenities = Some(List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking")),
      availability = Some(
        OfficeAvailability(
          List(
            OpeningHours(
              day = Monday,
              openingTime = LocalTime.of(10, 0, 0),
              closingTime = LocalTime.of(10, 30, 0)
            ),
            OpeningHours(
              day = Tuesday,
              openingTime = LocalTime.of(10, 0, 0),
              closingTime = LocalTime.of(10, 30, 0)
            )
          )
        )
      ),
      rules = Some("No smoking. Maintain cleanliness."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test("OfficeSpecifications model encodes correctly to JSON") {

    val jsonResult = officeSpecifications.asJson

    val expectedJson =
      """
        |{
        |   "id": 1,
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
        |   "rules": "No smoking. Maintain cleanliness.",
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
