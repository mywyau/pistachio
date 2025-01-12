package models.business.contact_details.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class UpdateBusinessContactDetailsRequest(
  primaryContactFirstName: String,
  primaryContactLastName: String,
  contactEmail: String,
  contactNumber: String,
  websiteUrl: Option[String]
)

object UpdateBusinessContactDetailsRequest {
  implicit val createOfficeAddressRequestEncoder: Encoder[UpdateBusinessContactDetailsRequest] = deriveEncoder[UpdateBusinessContactDetailsRequest]
  implicit val createOfficeAddressRequestDecoder: Decoder[UpdateBusinessContactDetailsRequest] = deriveDecoder[UpdateBusinessContactDetailsRequest]
}
