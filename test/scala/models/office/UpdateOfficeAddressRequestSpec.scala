package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.office.address_details.requests.UpdateOfficeAddressRequest
import testData.OfficeTestConstants.*
import weaver.SimpleIOSuite

object UpdateOfficeAddressRequestSpec extends SimpleIOSuite {

  test("UpdateOfficeAddressRequest model encodes correctly to JSON") {

    val jsonResult = updateOfficeAddressRequest.asJson

    val expectedJson =
      """
        |{
        |   "buildingName": "butter building",
        |   "floorNumber": "floor 1",
        |   "street": "Main street 123",
        |   "city": "New York",
        |   "country": "USA",
        |   "county": "County 123",
        |   "postcode": "123456",
        |   "latitude": 100.1,
        |   "longitude": -100.1
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
