package models.office.contact_details.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class CreateOfficeContactDetailsRequest(
  businessId: String,
  officeId: String,
  primaryContactFirstName: String,
  primaryContactLastName: String,
  contactEmail: String,
  contactNumber: String
)

object CreateOfficeContactDetailsRequest {
  implicit val createOfficeContactDetailsRequestEncoder: Encoder[CreateOfficeContactDetailsRequest] = deriveEncoder[CreateOfficeContactDetailsRequest]
  implicit val createOfficeContactDetailsRequestDecoder: Decoder[CreateOfficeContactDetailsRequest] = deriveDecoder[CreateOfficeContactDetailsRequest]
}
