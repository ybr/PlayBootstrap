package models.exceptions

import models.core.BusinessException

case class AccountAlreadyExistsException(login: String, cause: Throwable) extends BusinessException("account.already.exists", s"The login ${login} already exists", cause)
