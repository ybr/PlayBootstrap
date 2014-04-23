package controllers

import scala.concurrent.Future

import org.joda.time._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._

import play.api.libs.concurrent.Execution.Implicits._

import models._
import models.exceptions._
import models.requests._
import utils._

object Visitors extends UserController {
  def home() = WithMaybeUser { implicit request =>
    Ok(views.html.visitors.home())
  }

  private val signupForm = Form(tuple(
    "firstname" -> nonEmptyText.verifying(maxLength(255)),
    "lastname" -> nonEmptyText.verifying(maxLength(255)),
    "email" -> email.verifying(maxLength(255)),
    "password" -> nonEmptyText(maxLength = 255)
  ))

  def signup() = WithMaybeUser { implicit request =>
    Ok(views.html.visitors.signup(signupForm))
  }

  def subscribe() = WithMaybeUser.async { implicit request =>
    signupForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.visitors.signup(formWithErrors))),
      signupData => {
        val (firstName, lastName, email, password) = signupData
        userService.create(UserCreate(firstName, lastName, email, DateTime.now), email, password) map { _ =>
          Redirect(routes.Authentication.signin).flashing("success" -> i18n.Messages("flash.visitors.subscribe"))
        } recover {
          case AccountAlreadyExistsException(login, _) =>
            implicit val flash = Flash(Map("error" -> Messages("flash.visitors.alreadyExists", login)))
            BadRequest(views.html.visitors.signup(signupForm.fill(signupData)))
        }
      }
    )
  }
}
