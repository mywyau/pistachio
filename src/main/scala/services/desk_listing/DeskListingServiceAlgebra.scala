package services.desk_listing

import models.desk_listing.errors.DeskListingErrors
import models.desk_listing.requests.DeskListingRequest
import models.desk_listing.service.DeskListing

trait DeskListingServiceAlgebra[F[_]] {

  def findByUserId(userId: String): F[Either[DeskListingErrors, DeskListing]]

  def createDesk(DeskListing: DeskListingRequest): F[Either[DeskListingErrors, Int]]
}
