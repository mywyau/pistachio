package services.constants

import models.business.address.requests.CreateBusinessAddressRequest
import models.business.business_listing.requests.BusinessListingRequest
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.specifications.requests.CreateBusinessSpecificationsRequest

import java.time.LocalDateTime


object BusinessListingServiceConstants {

  val testCreateBusinessAddressRequest: CreateBusinessAddressRequest =
    CreateBusinessAddressRequest(
      userId = "user_id_1",
      businessId = Some("business_id_1"),
      businessName = Some("businessCorp"),
      buildingName = Some("build_123"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  val testCreateBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest =
    CreateBusinessSpecificationsRequest(
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = "Modern Workspace",
      description = "A vibrant business space in the heart of the city, ideal for teams or individuals."
    )

  val testCreateBusinessContactDetailsRequest =
    CreateBusinessContactDetailsRequest(
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = "businessCorp",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey.com"
    )

  val businessListingRequest: BusinessListingRequest =
    BusinessListingRequest(
      businessId = "business_id_1",
      addressDetails = testCreateBusinessAddressRequest,
      businessSpecs = testCreateBusinessSpecificationsRequest,
      contactDetails = testCreateBusinessContactDetailsRequest,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
}
