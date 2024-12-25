package models.business.specifications.requests

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import models.business.specifications.BusinessAvailability

import java.time.LocalDateTime

case class BusinessSpecificationsRequest(
                                          userId: String,
                                          businessId: String,
                                          businessName: String,
                                          description: String,
                                        )

object BusinessSpecificationsRequest {
  implicit val businessSpecificationsRequestEncoder: Encoder[BusinessSpecificationsRequest] = deriveEncoder[BusinessSpecificationsRequest]
  implicit val businessSpecificationsRequestDecoder: Decoder[BusinessSpecificationsRequest] = deriveDecoder[BusinessSpecificationsRequest]
}

