package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalTime
import models.desk.deskListing.DeskListing
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import weaver.SimpleIOSuite
import models.desk.deskSpecifications.OpeningHours
import models.Monday
import testData.DeskTestConstants.*

object DeskListingSpec extends SimpleIOSuite {

  val sampleDeskSpecificationsPartial: DeskSpecificationsPartial =
    DeskSpecificationsPartial(
      deskId = deskId1,
      deskName = deskName,
      description = Some(description1),
      deskType = Some(PrivateDesk),
      quantity = Some(5),
      features = Some(List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair")),
      availability = Some(availability),
      rules = Some(rules)
    )

  val sampleDeskPricingPartial: RetrievedDeskPricing =
    RetrievedDeskPricing(
      pricePerHour = Some(30.00),
      pricePerDay = Some(180.00),
      pricePerWeek = Some(450.00),
      pricePerMonth = Some(1000.00),
      pricePerYear = Some(9000.00)
    )

  val sampleDeskListing: DeskListing =
    DeskListing(
      deskId = "desk001",
      sampleDeskSpecificationsPartial,
      sampleDeskPricingPartial
    )

  test("DeskListing model encodes correctly to JSON") {

    val jsonResult = sampleDeskListing.asJson

    val expectedJson =
      """
        |{
        |  "deskId" : "desk001",
        |  "specifications" : {
        |    "deskId" : "desk001",
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
        |   "availability": {
        |     [
        |        "day": Monday"
        |        "startTime": "10:00:00",
        |        "endTime": "10:30:00"
        |     ],
        |     [
        |        "day": Tuesday"
        |        "startTime": "10:00:00",
        |        "endTime": "10:30:00"
        |     ]
        |   }
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
