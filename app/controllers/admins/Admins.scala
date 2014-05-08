package controllers.admins

import scala.concurrent.Future

import org.joda.time._

import play.api.Play
import play.api.Play.current
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.concurrent.Execution.Implicits._

import ybr.playground.log._

import models._
import models.requests._
import models.exceptions._
import services._
import utils.Mappings._

object Admins extends AdminController with Logger {
  def home() = WithAdmin { implicit request =>
    Ok(views.html.admins.home())
  }

  val localeForm = Form(single(
    "locale" -> nonEmptyText
  ))
  def locale(maybeRedirectURL: Option[String]) = Action { implicit request =>
    val redirectURL = maybeRedirectURL orElse request.headers.get("Referer") getOrElse controllers.admins.routes.Admins.home.absoluteURL()
    localeForm.bindFromRequest.fold(
      formWithErrors => Redirect(redirectURL).flashing("error" -> Messages("locale.notChanged")),
      locale => Redirect(redirectURL).withCookies(Cookie(Play.langCookieName, locale))
    )
  }

  def all = WithAdmin.async { implicit request =>
    adminService.all map { admins =>
      Ok(views.html.admins.adminsTable(admins))
    }
  }

  val activeForm = Form(single(
    "active" -> boolean
  ))

  def details(id: Id) = WithAdmin.async { implicit request =>
    adminService.byId(id).map {
      case Some(admin) => Ok(views.html.admins.adminDetails(activeForm.fill(admin.active), admin))
      case None => NotFound(views.html.admins.notfound("The admin can not be found"))
    }
  }

  // TODO eventually use a monad transformer
  def update(id: Id) = WithAdmin.async { implicit request =>
    adminService.byId(id) flatMap {
      case Some(admin) => activeForm.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest(views.html.admins.adminDetails(formWithErrors, admin))),
        active => adminService.update(admin, AdminUpdate.from(admin).copy(active = active)) map {
          case Some(updatedAdmin) => Redirect(controllers.admins.routes.Admins.all).flashing("success" -> Messages("flash.admin.admins.update", updatedAdmin.firstName, updatedAdmin.lastName))
          case None => NotFound(views.html.admins.notfound("The admin can not be found"))
        }
      )
      case None => Future.successful(NotFound(views.html.admins.notfound("The admin can not be found")))
    }
  }

  private val creationForm = Form(tuple(
    "firstname" -> nonEmptyText.verifying(maxLength(255)),
    "lastname" -> nonEmptyText.verifying(maxLength(255)),
    "email" -> email.verifying(maxLength(255)),
    "password" -> nonEmptyText(maxLength = 255).password
  ))

  def createGet = WithAdmin { implicit request =>
    Ok(views.html.admins.adminCreate(creationForm))
  }

  def create = WithAdmin.async { implicit request =>
    creationForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.admins.adminCreate(formWithErrors))),
      creationData => {
        val (firstName, lastName, email, password) = creationData
        adminService.create(AdminCreate(firstName, lastName, email, true, DateTime.now), email, password, Some(me)) map { admin =>
          Redirect(controllers.admins.routes.Admins.all).flashing("success" -> Messages("flash.admin.admins.create", admin.firstName, admin.lastName))
        } recover {
          case AccountAlreadyExistsException(login, _) =>
            implicit val flash = Flash(Map("error" -> Messages("flash.admins.alreadyExists", login)))
            BadRequest(views.html.admins.adminCreate(creationForm.fill(creationData)))
        }
      }
    )
  }
}
