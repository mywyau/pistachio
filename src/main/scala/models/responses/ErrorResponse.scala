package models.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ErrorResponse(code:String, message:String)

object ErrorResponse {
  implicit val errorEncoder: Encoder[ErrorResponse] = deriveEncoder[ErrorResponse]
  implicit val errorDecoder: Decoder[ErrorResponse] = deriveDecoder[ErrorResponse]
}