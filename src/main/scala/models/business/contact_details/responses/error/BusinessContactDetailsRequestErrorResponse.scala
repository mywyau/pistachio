package models.business.contact_details.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class BusinessContactDetailsRequestErrorResponse(code:String, message:String)

object BusinessContactDetailsRequestErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val businessConactDetailsErrorEncoder: Encoder[BusinessContactDetailsRequestErrorResponse] = deriveEncoder[BusinessContactDetailsRequestErrorResponse]
  implicit val businessConactDetailsErrorDecoder: Decoder[BusinessContactDetailsRequestErrorResponse] = deriveDecoder[BusinessContactDetailsRequestErrorResponse]
}