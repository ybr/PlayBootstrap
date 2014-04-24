package controllers.admins

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import models._
import services._

object Users extends AdminController {
  def userService = UserService

  def all = WithAdmin.async { implicit request =>
    userService.all map { users =>
      Ok(views.html.admins.users(users))
    }
  }

  def details(id: Id) = TODO
}
