package services

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._

import ybr.playground.log._

import models._
import models.requests._
import daos._
import utils._
import utils.credentials._

object AdminService extends Logger {
  def adminDAO: AdminDAO = AdminPostgreDAO

  def create(request: AdminCreate, login: String, password: String): Future[Admin] = {
    log.debug(s"Creating ${request} with login ${login} ...")

    val salt = SaltGeneratorUUID.generateSalt
    val hashedPassword = PasswordHasherSha512ToBase64.hashPassword(password, salt)

    adminDAO.create(request, login, hashedPassword, salt)
  }

  def authenticate(login: String, password: String): Future[Option[Admin]] = {
    log.debug(s"Authenticating admin with login ${login} ...")
    for {
      maybeSalt <- adminDAO.salt(login)
      maybeAdmin <- FutureUtils.sequence(maybeSalt.map { salt =>
        adminDAO.authenticate(login, PasswordHasherSha512ToBase64.hashPassword(password, salt))
      }).map(_.flatten)
    } yield maybeAdmin
  }

  def byLogin(login: String): Future[Option[Admin]] = adminDAO.byLogin(login)

  def all(): Future[Seq[Admin]] = adminDAO.all
}
