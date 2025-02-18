package models.business.availability

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalTime

case class UpdateBusinessOpeningHoursRequest(
  openingTime: LocalTime,
  closingTime: LocalTime
)

object UpdateBusinessOpeningHoursRequest {
  implicit val encoder: Encoder[UpdateBusinessOpeningHoursRequest] = deriveEncoder[UpdateBusinessOpeningHoursRequest]
  implicit val decoder: Decoder[UpdateBusinessOpeningHoursRequest] = deriveDecoder[UpdateBusinessOpeningHoursRequest]
}
