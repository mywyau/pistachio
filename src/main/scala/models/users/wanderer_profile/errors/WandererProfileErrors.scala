package models.users.wanderer_profile.errors

sealed trait WandererProfileErrors {
  val code: String
  val message: String
}

sealed trait OtherWandererProfileError extends WandererProfileErrors

case object MissingDetails extends OtherWandererProfileError:
  override val code: String = "MissingDetails"
  override val message: String = "MissingDetails"
  
case object UserIdNotFound extends OtherWandererProfileError:
  override val code: String = "UserIdNotFound"
  override val message: String = "UserIdNotFound"


case object IncompleteProfile extends OtherWandererProfileError:
  override val code: String = "IncompleteProfile"
  override val message: String = "IncompleteProfile"


case object OutdatedDetails extends OtherWandererProfileError:
  override val code: String = "OutdatedDetails"
  override val message: String = "OutdatedDetails"


sealed trait WandererLoginDetailsError extends WandererProfileErrors

case object MissingLoginDetails extends WandererLoginDetailsError:
  override val code: String = "MissingLoginDetails"
  override val message: String = "MissingLoginDetails"

sealed trait WandererAddressError extends WandererProfileErrors

case object MissingAddress extends WandererAddressError:
  override val code: String = "MissingAddress"
  override val message: String = "MissingAddress"


sealed trait WandererContactDetailsError extends WandererProfileErrors

case object MissingContactDetails extends WandererContactDetailsError:
  override val code: String = "MissingContactDetails"
  override val message: String = "MissingContactDetails"

