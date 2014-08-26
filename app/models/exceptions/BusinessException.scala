package models.exceptions

import playground.models.exceptions.CodedException

class BusinessException(val code: String, message: String, cause: Throwable) extends Exception(message, cause) with CodedException {
  def this(code: String, message: String) = this(code, message, null)
}
