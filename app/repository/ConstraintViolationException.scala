package repository

case class ConstraintViolationException(constraint: String, column: String, value: String) extends Exception

