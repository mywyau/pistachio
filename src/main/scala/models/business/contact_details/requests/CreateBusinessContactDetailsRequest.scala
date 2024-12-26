package models.business.contact_details.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class CreateBusinessContactDetailsRequest(
                                                userId: String,
                                                businessId: String,
                                                businessName: String,
                                                primaryContactFirstName: String,
                                                primaryContactLastName: String,
                                                contactEmail: String,
                                                contactNumber: String,
                                                websiteUrl: String
                                              )

object CreateBusinessContactDetailsRequest {
  implicit val createBusinessContactDetailsRequestEncoder: Encoder[CreateBusinessContactDetailsRequest] = deriveEncoder[CreateBusinessContactDetailsRequest]
  implicit val createBusinessContactDetailsRequestDecoder: Decoder[CreateBusinessContactDetailsRequest] = deriveDecoder[CreateBusinessContactDetailsRequest]
}

