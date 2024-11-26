package models.business.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.office.office_address.OfficeAddress
import models.office.office_details.OfficeDetails
import models.office.office_listing.OfficeAvailability
import models.office.office_listing.requests.OfficeListingRequest
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object OfficeListingRequestSpec extends SimpleIOSuite {

  val testOfficeDetails =
    OfficeDetails(
      id = Some(1),
      businessId = "BIZ123",
      office_name = "Modern Workspace",
      description = "A vibrant office space in the heart of the city, ideal for teams or individuals.",
      office_type = "Coworking",
      capacity = 50,
      numberOfFloors = 3,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      rules = Some("No smoking. Maintain cleanliness."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testOfficeAddress =
    OfficeAddress(
      id = Some(10),
      businessId = "BIZ123",
      office_id = "office_123",
      building_name = Some("build_123"),
      floor_number = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testOfficeAvailability =
    OfficeAvailability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      endTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val officeListingRequest =
    OfficeListingRequest(
      office_id = "OFF123",
      officeDetails = testOfficeDetails,
      addressDetails = testOfficeAddress,
      availability = testOfficeAvailability,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test("DeskListingRequest model encodes correctly to JSON") {

    val jsonResult = officeListingRequest.asJson

    val expectedJson =
      """
        |{
        |  "office_id": "OFF123",
        |  "officeDetails": {
        |    "id": 1,
        |    "businessId": "BIZ123",
        |    "office_name": "Modern Workspace",
        |    "description": "A vibrant office space in the heart of the city, ideal for teams or individuals.",
        |    "office_type": "Coworking",
        |    "capacity": 50,
        |    "numberOfFloors": 3,
        |    "amenities": ["Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"],
        |    "rules": "No smoking. Maintain cleanliness.",
        |    "createdAt": "2025-01-01T00:00:00",
        |    "updatedAt": "2025-01-01T00:00:00"
        |  },
        |  "addressDetails": {
        |    "id": 10,
        |    "businessId": "BIZ123",
        |    "office_id": "office_123",
        |    "building_name": "build_123",
        |    "floor_number": "floor 1",
        |    "street": "123 Main Street",
        |    "city": "New York",
        |    "country": "USA",
        |    "county": "New York County",
        |    "postcode": "10001",
        |    "latitude": 100.1,
        |    "longitude": -100.1,
        |    "createdAt": "2025-01-01T00:00:00",
        |    "updatedAt": "2025-01-01T00:00:00"
        |  },
        |  "availability": {
        |    "days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
        |    "startTime": "2025-01-01T00:00:00",
        |    "endTime": "2025-01-01T00:00:00"
        |  },
        |  "createdAt": "2025-01-01T00:00:00",
        |  "updatedAt": "2025-01-01T00:00:00"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      //      _ <- IO(println(jsonResult.noSpaces)) // For debugging, prints the actual JSON result.
      _ <- IO("")
    } yield {
      expect(jsonResult == expectedResult)
    }
  }

}
