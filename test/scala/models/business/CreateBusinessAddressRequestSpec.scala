package models.business

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import models.business.address.requests.CreateBusinessAddressRequest
import testData.BusinessTestConstants.testCreateBusinessAddressRequest
import weaver.SimpleIOSuite

object CreateBusinessAddressRequestSpec extends SimpleIOSuite {

  test("CreateBusinessAddressRequest model encodes correctly to JSON") {

    val jsonResult = testCreateBusinessAddressRequest.asJson

    val expectedJson =
      """
        |{
        |  "userId": "user_id_1",
        |  "businessId": "businessId1",
        |  "businessName": "MikeyCorp",
        |  "buildingName": "Nameless Building",
        |  "floorNumber": "floor 1",
        |  "street": "Main street 123",
        |  "city": "New York",
        |  "country": "USA",
        |  "county": "County 123",
        |  "postcode": "CF3 3NJ",
        |  "latitude": 100.1,
        |  "longitude": -100.1
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
