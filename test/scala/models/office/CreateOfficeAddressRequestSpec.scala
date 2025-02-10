package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.adts.*
import weaver.SimpleIOSuite
import testData.OfficeTestConstants.*

object CreateOfficeAddressRequestSpec extends SimpleIOSuite {
  
  test("CreateOfficeAddressRequest model encodes correctly to JSON") {

    val jsonResult = createOfficeAddressRequest.asJson

    val expectedJson =
      """
        |{
        |   "businessId": "business_id_1",
        |   "officeId": "office_id_1",
        |   "buildingName": "build_123",
        |   "floorNumber": "floor 1",
        |   "street": "123 Main Street",
        |   "city": "New York",
        |   "country": "USA",
        |   "county": "New York County",
        |   "postcode": "10001",
        |   "latitude": 100.1,
        |   "longitude": -100.1
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

