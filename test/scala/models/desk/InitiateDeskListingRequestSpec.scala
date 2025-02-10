package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.desk.deskListing.requests.InitiateDeskListingRequest
import testData.DeskTestConstants.sampleInitiateDeskListingRequest
import weaver.SimpleIOSuite

object InitiateDeskListingRequestSpec extends SimpleIOSuite {

  test("InitiateDeskListingRequest model encodes correctly to JSON") {

    val jsonResult = sampleInitiateDeskListingRequest.asJson

    val expectedJson =
      """
        |{
        |  "businessId" : "business001",
        |  "officeId" : "office001",
        |  "deskId" : "deskId1",
        |  "deskName" : "Luxury supreme desk",
        |  "description" : "Some description"
        |}
      """.stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }
}
