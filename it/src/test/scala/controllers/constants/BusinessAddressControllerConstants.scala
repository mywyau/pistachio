package controllers.constants

import cats.effect.*
import models.business.address.BusinessAddress
import models.business.address.requests.CreateBusinessAddressRequest

import java.time.LocalDateTime
import models.business.address.BusinessAddressPartial

object BusinessAddressControllerConstants {

  def testBusinessAddress(userId: String, businessId: String): BusinessAddressPartial = {
    BusinessAddressPartial(
      userId = userId,
      businessId = businessId,
      buildingName = Some("building_name_1"),
      floorNumber = Some("floor_1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("Manhattan"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )
  }

  def testBusinessAddressRequest(userId: String, businessId: String): CreateBusinessAddressRequest = {
    CreateBusinessAddressRequest(
      userId = userId,
      businessId = businessId,
      businessName = Some("business_name_1"),
      buildingName = Some("building_name_1"),
      floorNumber = Some("floor_1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("Manhattan"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )
  }

}
