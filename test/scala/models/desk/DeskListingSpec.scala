package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalTime
import models.desk.deskListing.DeskListing
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing

import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import weaver.SimpleIOSuite
import models.OpeningHours
import models.Monday
import testData.DeskTestConstants.*

object DeskListingSpec extends SimpleIOSuite {

  test("DeskListing model encodes correctly to JSON") {

    val jsonResult = sampleDeskListing.asJson

    val expectedJson =
      """
        |{
        |  "deskId" : "deskId1",
        |  "specifications" : {
        |    "deskId" : "deskId1",
        |    "deskName" : "Private Office Desk",
        |    "description" : "A comfortable desk in a private office space with all amenities included.",
        |    "deskType" : "PrivateDesk",
        |    "quantity" : 5,
        |    "features" : [
        |      "Wi-Fi",
        |      "Power Outlets",
        |      "Monitor",
        |      "Ergonomic Chair"
        |    ],
        |   "availability": [
        |     {
        |        "day": Monday"
        |        "openingTime": "09:00:00",
        |        "closingTime": "17:00:00"
        |     },
        |     {
        |        "day": Tuesday"
        |        "openingTime": "09:00:00",
        |        "closingTime": "17:00:00"
        |     }
        |   ],
        |    "rules" : "Please keep the desk clean and quiet."
        |  },
        |  "pricing" : {
        |    "pricePerHour" : 30.0,
        |    "pricePerDay" : 180.0,
        |    "pricePerWeek" : 450.0,
        |    "pricePerMonth" : 1000.0,
        |    "pricePerYear" : 9000.0
        |  }
        |}
      """.stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }
}
