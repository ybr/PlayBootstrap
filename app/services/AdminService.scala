package services

import scala.concurrent.Future

import play.api.Play._
import play.api.libs.concurrent.Execution.Implicits._

import com.typesafe.plugin._

import ybr.playground.log._

import models._
import models.requests._
import daos._
import utils._
import utils.credentials._

object AdminService extends Logger {
  def create(request: AdminCreate, login: String, password: Password, creator: Admin)(implicit adminDAO: AdminDAO): Future[Admin] = {
    log.debug(s"Creating ${request} with login ${login} ...")

    val salt = SaltGeneratorUUID.generateSalt
    val hashedPassword = PasswordHasherSha512ToBase64.hashPassword(password, salt)


    adminDAO.create(request, login, hashedPassword, salt).map { admin =>
      val mail = use[MailerPlugin].email
      mail.setSubject("Creation of your admin account")
      mail.setRecipient(request.email)
      mail.setFrom(configuration.getString("email.from").getOrElse(creator.email))
      mail.sendHtml(views.html.emails.admins.create(admin, login , password, creator).body)

      admin
    }
  }

  def authenticate(login: String, password: Password)(implicit adminDAO: AdminDAO): Future[Option[Admin]] = {
    log.debug(s"Authenticating admin with login ${login} ...")
    for {
      maybeSalt <- adminDAO.salt(login)
      maybeAdmin <- FutureUtils.sequence(maybeSalt.map { salt =>
        val hashedPassword = PasswordHasherSha512ToBase64.hashPassword(password, salt)
        adminDAO.authenticate(login, hashedPassword)
      }).map(_.flatten)
    } yield maybeAdmin
  }

  def byLogin(login: String)(implicit adminDAO: AdminDAO) = adminDAO.byLogin(login)
  def byId(id: Id)(implicit adminDAO: AdminDAO) = adminDAO.byId(id)

  def all(implicit adminDAO: AdminDAO): Future[Seq[Admin]] = adminDAO.all

  def update(admin: Admin, request: AdminUpdate)(implicit adminDAO: AdminDAO): Future[Option[Admin]] = {
    log.debug(s"Updating ${admin} with ${request}")
    adminDAO.update(admin, request)
  }
}
