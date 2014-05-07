package models.core

// TODO move to playground
trait CodedException { self: Exception =>
  def code: String
}

class BusinessException(val code: String, message: String, cause: Throwable) extends Exception(message, cause) with CodedException {
  def this(code: String, message: String) = this(code, message, null)
}
