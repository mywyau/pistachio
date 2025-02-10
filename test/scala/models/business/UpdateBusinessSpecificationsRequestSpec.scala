package models.business

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import testData.BusinessTestConstants.testUpdateBusinessSpecificationsRequest
import weaver.SimpleIOSuite

object UpdateBusinessSpecificationsRequestSpec extends SimpleIOSuite {

  test("UpdateBusinessSpecificationsRequest model encodes correctly to JSON") {

    val jsonResult = testUpdateBusinessSpecificationsRequest.asJson

    val expectedJson =
      """
        |{
        |  "businessName": "MikeyCorp",
        |  "description": "Some description",
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
        |   }
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
