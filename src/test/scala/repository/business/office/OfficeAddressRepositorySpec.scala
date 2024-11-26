package repository.business.office

import cats.effect.IO
import cats.effect.kernel.Ref
import models.office.office_address.OfficeAddress
import repository.business.office.mocks.MockOfficeAddressRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object OfficeAddressRepositorySpec extends SimpleIOSuite {

  def testAddress(id: Option[Int], businessId: String, office_id: String): OfficeAddress =
    OfficeAddress(
      id = id,
      businessId = businessId,
      office_id = office_id,
      building_name = Some("building name"),
      floor_number = Some("floor 3"),
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("United Kingdom"),
      county = Some("fake County"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(-100), 
      longitude = Some(-96.7),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[OfficeAddress]): IO[MockOfficeAddressRepository] =
    Ref.of[IO, List[OfficeAddress]](initialUsers).map(MockOfficeAddressRepository.apply)

  test(".findByBusinessId() - should return an address if business_id exists") {
    val existingAddressForUser = testAddress(Some(1), "business_id_1", "office_1")

    for {
      mockRepo <- createMockRepo(List(existingAddressForUser))
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.contains(existingAddressForUser))
  }

  test(".findByBusinessId() - should return None if business_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.isEmpty)
  }

  test(".createOfficeAddress() - when given a valid office address should insert an address into the postgres db") {

    val testAddressForUser2: OfficeAddress = testAddress(Some(2), "business_id_2", "office_2")
    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.createOfficeAddress(testAddressForUser2)
      findInsertedAddress <- mockRepo.findByBusinessId("business_id_2")
    } yield expect.all(
      result == 1,
      findInsertedAddress == Some(testAddressForUser2)
    )
  }
}