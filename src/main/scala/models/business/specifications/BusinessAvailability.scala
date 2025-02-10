package models.business.specifications

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalTime
import models.desk.deskSpecifications.OpeningHours

case class BusinessAvailability(
  availability: List[OpeningHours]
)

object BusinessAvailability {
  implicit val encoder: Encoder[BusinessAvailability] = deriveEncoder[BusinessAvailability]
  implicit val decoder: Decoder[BusinessAvailability] = deriveDecoder[BusinessAvailability]

  // Define Doobie Meta for JSON
  implicit val availabilityMeta: Meta[BusinessAvailability] =
    Meta[String].imap(jsonStr => decode[BusinessAvailability](jsonStr).getOrElse(throw new Exception("Invalid JSON")))(availability => availability.asJson.noSpaces)
}
