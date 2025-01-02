package models.business

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.business.address.requests.CreateBusinessAddressRequest
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object CreateBusinessAddressRequestSpec extends SimpleIOSuite {

  val testCreateBusinessAddressRequest: CreateBusinessAddressRequest =
    CreateBusinessAddressRequest(
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = Some("MikeyCorp"),
      buildingName = Some("Nameless Building"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  test("CreateBusinessAddressRequest model encodes correctly to JSON") {

    val jsonResult = testCreateBusinessAddressRequest.asJson

    val expectedJson =
      """
        |{
        |  "userId": "user_id_1",
        |  "businessId": "business_id_1",
        |  "businessName": "MikeyCorp",
        |  "buildingName": "Nameless Building",
        |  "floorNumber": "floor 1",
        |  "street": "123 Main Street",
        |  "city": "New York",
        |  "country": "USA",
        |  "county": "New York County",
        |  "postcode": "CF3 3NJ",
        |  "latitude": 100.1,
        |  "longitude": -100.1
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield {
      expect(jsonResult == expectedResult)
    }
  }

}

