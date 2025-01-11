package models.desk_listing.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime
import models.desk_listing.Availability
import models.desk_listing.DeskType

case class DeskListingRequest(
  deskName: String,
  description: Option[String],
  desk_type: DeskType,
  quantity: Int,
  price_per_hour: BigDecimal,
  price_per_day: BigDecimal,
  rules: Option[String],
  features: List[String], 
  availability: Availability
)

object DeskListingRequest {
  implicit val eskRequestEncoder: Encoder[DeskListingRequest] = deriveEncoder[DeskListingRequest]
  implicit val eskRequestDecoder: Decoder[DeskListingRequest] = deriveDecoder[DeskListingRequest]
}
