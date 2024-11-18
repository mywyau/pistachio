package models.users.registration.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class UserAddress(
                        userId: String,
                        street: String,
                        city: String,
                        country: String,
                        county: Option[String],
                        postcode: String,
                        createdAt: LocalDateTime,
                        updated_at: LocalDateTime
                      )

object UserAddress {
  implicit val userAddressEncoder: Encoder[UserAddress] = deriveEncoder[UserAddress]
  implicit val userAddressDecoder: Decoder[UserAddress] = deriveDecoder[UserAddress]
}

