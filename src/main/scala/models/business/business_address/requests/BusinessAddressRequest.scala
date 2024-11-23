package models.business.business_address.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class BusinessAddressRequest(
                                   userId: String,
                                   street: String,
                                   city: String,
                                   country: String,
                                   county: Option[String],
                                   postcode: String,
                                   createdAt: LocalDateTime,
                                   updated_at: LocalDateTime
                                 )

object BusinessAddressRequest {
  implicit val businessAddressRequestEncoder: Encoder[BusinessAddressRequest] = deriveEncoder[BusinessAddressRequest]
  implicit val businessAddressRequestDecoder: Decoder[BusinessAddressRequest] = deriveDecoder[BusinessAddressRequest]
}
