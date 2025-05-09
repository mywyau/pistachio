package controllers.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.office.address_details.CreateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.office.address_details.OfficeAddressPartial

import models.office.contact_details.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import models.office.specifications.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeSpecifications
import models.office.specifications.OfficeSpecificationsPartial
import models.office_listing.requests.InitiateOfficeListingRequest
import models.office_listing.OfficeListing
import testData.OfficeTestConstants.*
import testData.TestConstants.*
import models.office.OpenPlanOffice

object OfficeListingControllerConstants {

  val testCreateOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = "businessId1",
      officeId = "officeId1",
      officeName = "Maginificanent Office",
      description = "some office description",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      openingHours = officeOpeningHours1,
      rules = Some("Please keep the office clean and tidy.")
    )

  val testOfficeAddressRequest: CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = "businessId1",
      officeId = "officeId1",
      buildingName = Some("OfficeListingControllerISpec Building"),
      floorNumber = Some("floor 1"),
      street = Some("Main street 123"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("County 123"),
      postcode = Some("123456"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  val testCreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = "businessId1",
      officeId = "officeId1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  def testInitiateOfficeListingRequest(businessId: String, officeId: String): InitiateOfficeListingRequest =
    InitiateOfficeListingRequest(
      businessId = businessId,
      officeId = officeId,
      officeName = "some office name",
      description = "some desc"
    )

  def testOfficeAddressPartial(businessId: String, officeId: String): OfficeAddressPartial =
    OfficeAddressPartial(
      businessId = businessId,
      officeId = officeId,
      buildingName = None,
      floorNumber = None,
      street = None,
      city = None,
      country = None,
      county = None,
      postcode = None,
      latitude = None,
      longitude = None
    )

  def testOfficeContactDetailsPartial(businessId: String, officeId: String): OfficeContactDetailsPartial =
    OfficeContactDetailsPartial(
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = None,
      primaryContactLastName = None,
      contactEmail = None,
      contactNumber = None
    )

  def testOfficeSpecificationsPartial(businessId: String, officeId: String) =
    OfficeSpecificationsPartial(
      businessId = businessId,
      officeId = officeId,
      officeName = None,
      description = None,
      officeType = None,
      numberOfFloors = None,
      totalDesks = None,
      capacity = None,
      amenities = None,
      openingHours = None,
      rules = None
    )

  def testOfficeListing(id: Option[Int], businessId: String, officeId: String): OfficeListing =
    OfficeListing(
      officeId = officeId,
      addressDetails = testOfficeAddressPartial(businessId, officeId),
      contactDetails = testOfficeContactDetailsPartial(businessId, officeId),
      specifications = testOfficeSpecificationsPartial(businessId, officeId)
    )
}
