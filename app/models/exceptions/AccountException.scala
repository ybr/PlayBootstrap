package models.exceptions

import models.core.BusinessException

case class AccountAlreadyExistsException(login: String, cause: Throwable) extends BusinessException("The login already exists", cause)
