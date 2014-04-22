package controllers

import scala.concurrent.Future

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._

import play.api.libs.concurrent.Execution.Implicits._

object Authentication extends UserController {
  def userService = services.UserService

  private val signinForm = Form(tuple(
    "email" -> email.verifying(maxLength(255)),
    "password" -> nonEmptyText(maxLength = 255)
  ))

  def signin() = WithMaybeUser { implicit request =>
    Ok(views.html.visitors.signin(signinForm))
  }

  def signout() = WithUser {
    Redirect(routes.Visitors.home).withNewSession
  }

  def authenticate() = WithMaybeUser.async { implicit request =>
    signinForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.visitors.signin(formWithErrors))),
      signinData => {
        val (email, password) = signinData
        userService.authenticate(email, password) map {
          case Some(user) => Redirect(routes.Users.home).withSession("login" -> email)
          case None => {
            implicit val flash = Flash(Map("error" -> Messages("flash.visitors.credentialsUnknown")))
            Unauthorized(views.html.visitors.signin(signinForm.fill(signinData)))
          }
        }
      }
    )
  }
}