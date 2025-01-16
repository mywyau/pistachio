package models.deskListing.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime
import models.deskListing.Availability
import models.deskListing.DeskType

case class UpdateDeskListingRequest(
  deskName: String,
  description: Option[String],
  deskType: DeskType,
  quantity: Int,
  features: List[String],
  availability: Availability,
  rules: Option[String]
)

object UpdateDeskListingRequest {
  implicit val encoder: Encoder[UpdateDeskListingRequest] = deriveEncoder[UpdateDeskListingRequest]
  implicit val decoder: Decoder[UpdateDeskListingRequest] = deriveDecoder[UpdateDeskListingRequest]
}
