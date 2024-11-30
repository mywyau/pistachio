package services.office.office_listing

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.effect.{Concurrent, IO}
import cats.implicits.*
import cats.syntax.all.*
import cats.{Monad, NonEmptyParallel}
import models.database.{SqlErrors, *}
import models.office.office_address.OfficeAddress
import models.office.office_address.errors.OfficeAddressErrors
import models.office.office_listing.errors.OfficeListingErrors
import models.office.office_listing.requests.OfficeListingRequest
import models.office.office_listing.{OfficeListing, errors}
import repositories.office.{OfficeAddressRepositoryAlgebra, OfficeContactDetailsRepositoryAlgebra, OfficeSpecsRepositoryAlgebra}
import services.office.office_listing.OfficeListingServiceAlgebra


class OfficeListingServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                              officeAddressRepo: OfficeAddressRepositoryAlgebra[F],
                                                                              officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F],
                                                                              officeSpecsRepo: OfficeSpecsRepositoryAlgebra[F]
                                                                            ) extends OfficeListingServiceAlgebra[F] {

  override def findByBusinessId(businessId: String): F[Either[OfficeListingErrors, OfficeListing]] = {
    ???
  }

  override def createOffice(officeListing: OfficeListingRequest): F[ValidatedNel[SqlErrors, Int]] = {

    val addressCreation: F[ValidatedNel[SqlErrors, Int]] =
      officeAddressRepo.createOfficeAddress(officeListing.addressDetails)

    val contactDetailsCreation: F[ValidatedNel[SqlErrors, Int]] =
      officeContactDetailsRepo.createContactDetails(officeListing.contactDetails)

    val specsCreation: F[ValidatedNel[SqlErrors, Int]] =
      officeSpecsRepo.createSpecs(officeListing.officeSpecs)

    // Run the operations in parallel
    (addressCreation, contactDetailsCreation, specsCreation).parMapN {
      case (Validated.Valid(addressId), Validated.Valid(contactId), Validated.Valid(specsId)) =>
        Valid(1) // All operations succeeded; return success indicator
      case (addressResult, contactResult, specsResult) =>
        // Collect errors if any operation fails
        // Combine errors from all operations
        val errors =
          addressResult.toEither.left.toSeq ++
            contactResult.toEither.left.toSeq ++
            specsResult.toEither.left.toSeq
        DatabaseError.invalidNel
    }.handleErrorWith { e =>
      Concurrent[F].pure(UnknownError.invalidNel)
    }
  }

}

object OfficeListingService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeAddressRepo: OfficeAddressRepositoryAlgebra[F],
                                                   officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F],
                                                   officeSpecsRepo: OfficeSpecsRepositoryAlgebra[F]
                                                 ): OfficeListingServiceImpl[F] =
    new OfficeListingServiceImpl[F](officeAddressRepo, officeContactDetailsRepo, officeSpecsRepo)
}
