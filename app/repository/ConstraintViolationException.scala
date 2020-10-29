package repository

case class ConstraintViolationException(constraint: String,
                                        source: ConstraintViolationException.Source)
  extends Exception

object ConstraintViolationException {
  sealed trait Source {}
  case class Unique(column: String, value: String) extends Source
}

