package models.database

trait SqlErrors

case object InsertionFailed extends SqlErrors

case object ConstraintViolation extends SqlErrors

case object DatabaseError extends SqlErrors

case object UnknownError extends SqlErrors