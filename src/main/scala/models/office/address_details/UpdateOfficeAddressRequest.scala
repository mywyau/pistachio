package models.office.address_details

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class UpdateOfficeAddressRequest(
  buildingName: Option[String],
  floorNumber: Option[String],
  street: Option[String],
  city: Option[String],
  country: Option[String],
  county: Option[String],
  postcode: Option[String],
  latitude: Option[BigDecimal],
  longitude: Option[BigDecimal]
)

object UpdateOfficeAddressRequest {
  implicit val createOfficeAddressRequestEncoder: Encoder[UpdateOfficeAddressRequest] = deriveEncoder[UpdateOfficeAddressRequest]
  implicit val createOfficeAddressRequestDecoder: Decoder[UpdateOfficeAddressRequest] = deriveDecoder[UpdateOfficeAddressRequest]
}
