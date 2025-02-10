package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest
import testData.OfficeTestConstants.*
import weaver.SimpleIOSuite

object UpdateOfficeContactDetailsRequestSpec extends SimpleIOSuite {

  test("UpdateOfficeContactDetailsRequest model encodes correctly to JSON") {

    val jsonResult = updateOfficeContactDetailsRequest.asJson

    val expectedJson =
      """
        |{
        |   "primaryContactFirstName": "Michael",
        |   "primaryContactLastName": "Yau",
        |   "contactEmail": "mike@gmail.com",
        |   "contactNumber": "07402205071"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
