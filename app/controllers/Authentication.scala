package controllers

import scala.concurrent.Future

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._

import play.api.libs.concurrent.Execution.Implicits._

import playground.json.Implicits._
import playground.form.Mappings._

import models._
import services._
import App.Daos._

object Authentication extends Controller with UserController {
  val signinForm = Form(tuple(
    "email" -> email.verifying(maxLength(255)),
    "password" -> nonEmptyText(maxLength = 255).password
  ))

  def signin() = WithMaybeUser { implicit request =>
    request.session.get("login") match {
      case Some(_) => Redirect(routes.Users.home)
      case None => Ok(views.html.visitors.signin(signinForm))
    }
  }

  def signout() = Action {
    Redirect(routes.Visitors.home).withNewSession
  }

  def authenticate(maybeRedirectURL: Option[String]) = WithMaybeUser.async { implicit request =>
    signinForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.visitors.signin(formWithErrors))),
      signinData => {
        val (email, password) = signinData
        UserService.authenticate(email, password) map {
          case Some(user) => {
            val redirectUri = maybeRedirectURL orElse request.headers.get("Referer") getOrElse routes.Users.home.absoluteURL()
            Redirect(redirectUri).withSession("login" -> email)
          }
          case None => {
            implicit val flash = Flash(Map("error" -> Messages("flash.visitors.credentialsUnknown")))
            Unauthorized(views.html.visitors.signin(signinForm.fill(signinData)))
          }
        }
      }
    )
  }
}
