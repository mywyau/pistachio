package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.desk.deskListing.DeskListingCard
import models.ModelsBaseSpec
import testData.DeskTestConstants.*
import weaver.SimpleIOSuite

object DeskListingCardSpec extends SimpleIOSuite with ModelsBaseSpec {

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

    val jsonResultPretty = printer.print(jsonResult)
    val expectedResultPretty = printer.print(expectedResult)

    val differences = jsonDiff(jsonResult, expectedResult, expectedResultPretty, jsonResultPretty)

    for {
      _ <- IO {
        if (differences.nonEmpty) {
          println("=== JSON Difference Detected! ===")
          differences.foreach(diff => println(s"- $diff"))
          println("Generated JSON:\n" + jsonResultPretty)
          println("Expected JSON:\n" + expectedResultPretty)
        }
      }
    } yield expect(differences.isEmpty)
  }
}
