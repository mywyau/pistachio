package models.deskListing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.deskListing.DeskType
import models.deskListing.Availability

case class DeskListing(
  id: Option[Int],
  businessId: String,
  workspaceId: String,
  title: String,
  description: Option[String],
  deskType: DeskType,
  quantity: Int,
  features: List[String],
  availability: Availability,
  rules: Option[String],
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object DeskListing {
  implicit val encoder: Encoder[DeskListing] = deriveEncoder[DeskListing]
  implicit val decoder: Decoder[DeskListing] = deriveDecoder[DeskListing]
}
