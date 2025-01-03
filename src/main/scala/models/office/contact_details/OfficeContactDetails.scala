package models.office.contact_details

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class OfficeContactDetails(
  id: Option[Int],
  businessId: String,
  officeId: String,
  primaryContactFirstName: Option[String],
  primaryContactLastName: Option[String],
  contactEmail: Option[String],
  contactNumber: Option[String],
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object OfficeContactDetails {
  implicit val officeContactDetailsEncoder: Encoder[OfficeContactDetails] = deriveEncoder[OfficeContactDetails]
  implicit val officeContactDetailsDecoder: Decoder[OfficeContactDetails] = deriveDecoder[OfficeContactDetails]
}
