package controllers

import play.api.mvc._

import controllers._

object Users extends UserController {
  def home() = WithUser { implicit request =>
    Ok(views.html.users.home())
  }

  def signout() = WithUser {
    Redirect(routes.Visitors.home).withNewSession
  }
}
