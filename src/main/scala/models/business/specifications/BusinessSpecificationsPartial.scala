package models.business.specifications

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.business.specifications.BusinessAvailability
import models.OpeningHours

case class BusinessSpecificationsPartial(
  userId: String,
  businessId: String,
  businessName: Option[String],
  description: Option[String],
  openingHours: Option[List[OpeningHours]]
)

object BusinessSpecificationsPartial {

  implicit val encoder: Encoder[BusinessSpecificationsPartial] = deriveEncoder[BusinessSpecificationsPartial]
  implicit val decoder: Decoder[BusinessSpecificationsPartial] = deriveDecoder[BusinessSpecificationsPartial]
}
