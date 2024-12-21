package repository.business

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.address_details.requests.BusinessAddressRequest
import models.business.address_details.service.BusinessAddress
import repository.business.mocks.MockBusinessAddressRepository
import repository.constants.BusinessAddressConstants.*
import weaver.SimpleIOSuite

object BusinessAddressRepositorySpec extends SimpleIOSuite {

  test(".findByBusinessId() - should return an address if business_id_1 exists") {

    val existingAddressForUser = testAddress(Some(1), "user_id_1", Some("business_id_1"))

    for {
      mockRepo <- createMockRepo(List(existingAddressForUser))
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.contains(existingAddressForUser))
  }

  test(".findByBusinessId() - should return None if business_id_1 does not exist") {
    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.isEmpty)
  }

  test(".createBusinessAddress() - when given a valid business address should insert an address into the postgres db") {

    val testBusinessAddressRequest: BusinessAddressRequest = testAddressRequest("user_id_2", Some("business_id_2"))
    val testAddressForUser2: BusinessAddress = testAddress(Some(1), "user_id_2", Some("business_id_2"))

    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.createBusinessAddress(testBusinessAddressRequest)
      findInsertedAddress <- mockRepo.findByBusinessId("business_id_2")
    } yield expect.all(
      result == Valid(1),
      findInsertedAddress == Some(testAddressForUser2)
    )
  }

  test(".deleteBusinessAddress() - when given a valid businessId should delete the business address details") {

    val existingAddressForUser: BusinessAddress = testAddress(Some(1), "user_id_3", Some("business_id_3"))

    for {
      mockRepo <- createMockRepo(List(existingAddressForUser))
      findInitiallyCreatedAddress <- mockRepo.findByBusinessId("business_id_3")
      result <- mockRepo.deleteBusinessAddress("business_id_3")
      findInsertedAddress <- mockRepo.findByBusinessId("business_id_3")
    } yield expect.all(
      findInitiallyCreatedAddress == Some(existingAddressForUser),
      result == Valid(1),
      findInsertedAddress == None
    )
  }
}
