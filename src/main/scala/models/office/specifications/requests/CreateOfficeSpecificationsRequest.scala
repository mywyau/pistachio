package models.office.specifications.requests

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.office.adts.OfficeType
import models.office.specifications.OfficeAvailability

case class CreateOfficeSpecificationsRequest(
  businessId: String,
  officeId: String,
  officeName: String,
  description: String,
  officeType: OfficeType,
  numberOfFloors: Int,
  totalDesks: Int,
  capacity: Int,
  amenities: List[String],
  availability: OfficeAvailability,
  rules: Option[String]
)

object CreateOfficeSpecificationsRequest {
  implicit val createOfficeSpecificationsRequestEncoder: Encoder[CreateOfficeSpecificationsRequest] = deriveEncoder[CreateOfficeSpecificationsRequest]
  implicit val createOfficeSpecificationsRequestDecoder: Decoder[CreateOfficeSpecificationsRequest] = deriveDecoder[CreateOfficeSpecificationsRequest]
}
