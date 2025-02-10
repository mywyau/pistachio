package models.desk.deskSpecifications

import doobie.util.meta.Meta
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*

import java.time.LocalDateTime
import java.time.LocalTime
import models.Day

case class OpeningHours(
  day: Day, 
  openingTime: LocalTime,
  closingTime: LocalTime
)

object OpeningHours {

  implicit val encoder: Encoder[OpeningHours] = deriveEncoder[OpeningHours]
  implicit val decoder: Decoder[OpeningHours] = deriveDecoder[OpeningHours]

  // Define Doobie Meta for JSON
  implicit val openingHoursMeta: Meta[OpeningHours] =
    Meta[String].imap(jsonStr => 
      decode[OpeningHours](jsonStr)
    .getOrElse(throw new Exception("Invalid JSON"))
    )(openingHours => openingHours.asJson.noSpaces)
}
