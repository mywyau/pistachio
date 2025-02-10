package models.office.specifications

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import models.office.adts.OfficeType
import models.office.specifications.OfficeAvailability

import java.time.LocalDateTime
import models.OpeningHours

case class OfficeSpecificationsPartial(
  businessId: String,
  officeId: String,
  officeName: Option[String],
  description: Option[String],
  officeType: Option[OfficeType],
  numberOfFloors: Option[Int],
  totalDesks: Option[Int],
  capacity: Option[Int],
  amenities: Option[List[String]],
  openingHours: List[OpeningHours],
  rules: Option[String]
)

object OfficeSpecificationsPartial {
  implicit val encoder: Encoder[OfficeSpecificationsPartial] = deriveEncoder[OfficeSpecificationsPartial]
  implicit val decoder: Decoder[OfficeSpecificationsPartial] = deriveDecoder[OfficeSpecificationsPartial]
}
