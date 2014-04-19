package services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

import ybr.log._

import utils.credentials._
import daos._
import models.requests._
import models._
import utils._

object UserService extends Loggable {
  def userDAO: UserDAO = UserPostgreDAO

  def create(request: UserCreate, login: String, password: String): Future[User] = {
    log.debug("Creating ${request}...")
    val salt = SaltGeneratorUUID.generateSalt
    val hashedPassword = PasswordHasherSha512ToBase64.hashPassword(password, salt)

    userDAO.create(request, login, hashedPassword, salt)
  }

  def authenticate(login: String, password: String): Future[Option[User]] = {
    log.debug(s"Authenticating user with login ${login} ...")
    for {
      maybeSalt <- userDAO.salt(login)
      maybeUser <- FutureUtils.sequence(maybeSalt.map { salt =>
        userDAO.authenticate(login, PasswordHasherSha512ToBase64.hashPassword(password, salt))
      }).map(_.flatten)
    } yield maybeUser
  }

  def update(user: User, request: UserUpdate): Future[User] = {
    log.debug(s"Updating ${user} with ${request}...")
    userDAO.update(user, request)
  }
}
