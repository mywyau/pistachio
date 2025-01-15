package models.deskPricing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder

case class RetrievedDeskPricing(
  pricePerHour: BigDecimal,
  pricePerDay: BigDecimal,
  pricePerWeek: BigDecimal,
  pricePerMonth: BigDecimal,
  pricePerYear: BigDecimal
)

object RetrievedDeskPricing {
  implicit val encoder: Encoder[RetrievedDeskPricing] = deriveEncoder[RetrievedDeskPricing]
  implicit val decoder: Decoder[RetrievedDeskPricing] = deriveDecoder[RetrievedDeskPricing]
}
