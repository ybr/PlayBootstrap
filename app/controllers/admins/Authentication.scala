package controllers.admins

import scala.concurrent.Future

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._

import play.api.libs.concurrent.Execution.Implicits._

import playground.form.Mappings._

import services._

object Authentication extends Controller {
  private val signinForm = Form(tuple(
    "login" -> nonEmptyText.verifying(maxLength(255)),
    "password" -> nonEmptyText(maxLength = 255).password
  ))

  def signin() = Action { implicit request =>
    Ok(views.html.admins.signin(signinForm))
  }

  def signout() = Action {
    Redirect(controllers.admins.routes.Admins.home).withNewSession
  }

  def authenticate() = Action.async { implicit request =>
    signinForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.admins.signin(formWithErrors))),
      signinData => {
        val (login, password) = signinData
        AdminService.authenticate(login, password) map {
          case Some(admin) => Redirect(routes.Admins.home).withSession("admin" -> login)
          case None => {
            implicit val flash = Flash(Map("error" -> Messages("flash.admins.credentialsUnknown")))
            Unauthorized(views.html.admins.signin(signinForm.fill(signinData)))
          }
        }
      }
    )
  }
}
