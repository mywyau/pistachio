package models.business.contact_details.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class BusinessContactDetailsRequest(
                                        userId: String,
                                        businessId: String,
                                        businessName: String,
                                        primaryContactFirstName: String,
                                        primaryContactLastName: String,
                                        contactEmail: String,
                                        contactNumber: String,
                                        websiteUrl: String
                                      )

object BusinessContactDetailsRequest {
  implicit val businessContactDetailsRequestEncoder: Encoder[BusinessContactDetailsRequest] = deriveEncoder[BusinessContactDetailsRequest]
  implicit val businessContactDetailsRequestDecoder: Decoder[BusinessContactDetailsRequest] = deriveDecoder[BusinessContactDetailsRequest]
}

