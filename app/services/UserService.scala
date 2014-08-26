package services

import scala.concurrent.Future

import play.api.Play._
import play.api.i18n._
import play.api.libs.concurrent.Execution.Implicits._

import com.typesafe.plugin._

import org.joda.time._

import playground.log._
import playground.models._

import utils.credentials._
import daos._
import models.requests._
import models._
import utils._

object UserService extends Logger {
  def create(unsafeRequest: UserCreate, login: String, password: Password)(implicit userDAO: UserDAO, lang: Lang): Future[User] = {
    val request = unsafeRequest.copy(creation = DateTime.now)
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

  def authenticate(login: String, password: Password)(implicit userDAO: UserDAO): Future[Option[User]] = {
    log.debug(s"Authenticating user with login ${login} ...")
    for {
      maybeSalt <- userDAO.salt(login)
      maybeUser <- FutureUtils.sequence(maybeSalt.map { salt =>
        userDAO.authenticate(login, PasswordHasherSha512ToBase64.hashPassword(password, salt))
      }).map(_.flatten)
    } yield maybeUser
  }

  def byLogin(login: String)(implicit userDAO: UserDAO) = userDAO.byLogin(login)
  def byId(id: Id)(implicit userDAO: UserDAO) = userDAO.byId(id)

  def all(implicit userDAO: UserDAO) = userDAO.all

  def update(user: User, request: UserUpdate)(implicit userDAO: UserDAO): Future[Option[User]] = {
    log.debug(s"Updating ${user} with ${request}...")
    userDAO.update(user, request)
  }
}
