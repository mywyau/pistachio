package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.desk.deskListing.DeskListingCard
import weaver.SimpleIOSuite
import testData.DeskTestConstants.*

object DeskListingCardSpec extends SimpleIOSuite {

  test("DeskListingCard model encodes correctly to JSON") {

    val jsonResult = sampleDeskListingCard.asJson

    val expectedJson =
      """
        |{
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
