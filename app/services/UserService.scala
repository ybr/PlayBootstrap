package services

import scala.concurrent.Future

import play.api.Play._
import play.api.i18n._
import play.api.libs.concurrent.Execution.Implicits._

import com.typesafe.plugin._

import ybr.playground.log._

import utils.credentials._
import daos._
import models.requests._
import models._
import utils._

object UserService extends Logger {
  def userDAO: UserDAO = UserPostgreDAO

  def create(request: UserCreate, login: String, password: Password)(implicit lang: Lang): Future[User] = {
    log.debug(s"Creating ${request} with login ${login} ...")
    val salt = SaltGeneratorUUID.generateSalt
    val hashedPassword = PasswordHasherSha512ToBase64.hashPassword(password, salt)

    userDAO.create(request, login, hashedPassword, salt) map { user =>
      val mail = use[MailerPlugin].email
      mail.setSubject(Messages("emails.users.create.subject", user))
      mail.setRecipient(request.email)
      mail.setFrom(configuration.getString("email.from").getOrElse("noreply@unknown.com"))
      mail.sendHtml(views.html.emails.users.create(user, login, password).body)

      user
    }
  }

  def authenticate(login: String, password: Password): Future[Option[User]] = {
    log.debug(s"Authenticating user with login ${login} ...")
    for {
      maybeSalt <- userDAO.salt(login)
      maybeUser <- FutureUtils.sequence(maybeSalt.map { salt =>
        userDAO.authenticate(login, PasswordHasherSha512ToBase64.hashPassword(password, salt))
      }).map(_.flatten)
    } yield maybeUser
  }

  def byLogin = userDAO.byLogin _
  def byId = userDAO.byId _

  def all = userDAO.all

  def update(user: User, request: UserUpdate): Future[Option[User]] = {
    log.debug(s"Updating ${user} with ${request}...")
    userDAO.update(user, request)
  }

  // TODO use monad transformer future option
  def updatePassword(login: String, oldPassword: Password, newPassword: Password): Future[Option[Unit]] = {
    log.debug(s"Updating password for login ${login}")
    for {
      maybeSalt <- userDAO.salt(login)
      maybeUser <- FutureUtils.sequence(maybeSalt.map { salt =>
        userDAO.authenticate(login, PasswordHasherSha512ToBase64.hashPassword(oldPassword, salt))
      }).map(_.flatten)
      update <- FutureUtils.sequence(maybeUser map { user =>
        log.debug(s"Updating password for ${user}...")
        val salt = SaltGeneratorUUID.generateSalt
        val hashedPassword = PasswordHasherSha512ToBase64.hashPassword(newPassword, salt)
        userDAO.updatePassword(login, hashedPassword, salt)
      }).map(_.flatten)
    } yield update
  }
}
