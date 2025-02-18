package models.business.availability

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import models.Day

case class CreateBusinessDaysRequest(
  days: List[Day]
)

object CreateBusinessDaysRequest {
  implicit val encoder: Encoder[CreateBusinessDaysRequest] = deriveEncoder[CreateBusinessDaysRequest]
  implicit val decoder: Decoder[CreateBusinessDaysRequest] = deriveDecoder[CreateBusinessDaysRequest]
}
