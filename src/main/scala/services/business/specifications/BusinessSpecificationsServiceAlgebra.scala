package services.business.specifications

import models.business.business_contact_details.BusinessSpecifications
import models.business.business_contact_details.errors.BusinessSpecificationsErrors
import models.business.business_specs.BusinessSpecifications
import models.business.business_specs.errors.BusinessSpecificationsErrors

trait BusinessSpecificationsServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessSpecificationsErrors, BusinessSpecifications]]

  def create(businessSpecifications: BusinessSpecifications): F[cats.data.ValidatedNel[BusinessSpecificationsErrors, Int]]
}