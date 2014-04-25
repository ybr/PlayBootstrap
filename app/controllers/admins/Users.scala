package controllers.admins

import scala.concurrent.Future

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._
import play.api.libs.concurrent.Execution.Implicits._

import models._
import models.requests._
import services._

object Users extends AdminController {
  def userService = UserService

  def all = WithAdmin.async { implicit request =>
    userService.all map { users =>
      Ok(views.html.admins.users(users))
    }
  }

  def activeForm = Form(single(
    "active" -> boolean
  ))

  def details(id: Id) = WithAdmin.async { implicit request =>
    userService.byId(id) map {
      case Some(user) => Ok(views.html.admins.user(activeForm.fill(user.active), user))
      case None => NotFound(views.html.admins.notfound("The user can not be found"))
    }
  }

  def update(id: Id) = WithAdmin.async { implicit request =>
    userService.byId(id) flatMap {
      case Some(user) => activeForm.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest(views.html.admins.user(activeForm.fill(user.active), user))),
        active => userService.update(user, UserUpdate(user).copy(active = active)) map { _ =>
          Redirect(controllers.admins.routes.Users.all).flashing("success" -> Messages("flash.admin.users.update", user.firstName, user.lastName))
        }
      )
      case None => Future.successful(NotFound(views.html.admins.notfound("The user can not be found")))
    }
  }
}
