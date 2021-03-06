package controllers

import scala.concurrent.Future

import org.joda.time._

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._

import play.api.libs.concurrent.Execution.Implicits._

import playground.form.Mappings._

import models._
import models.exceptions._
import models.requests._
import services._
import utils._
import App.Daos._

object Visitors extends Controller with UserController {
  def home = WithMaybeUser { implicit request =>
    Ok(views.html.visitors.home())
  }

  val localeForm = Form(single(
    "locale" -> nonEmptyText
  ))
  def locale(maybeRedirectURL: Option[String]) = WithMaybeUser { implicit request =>
    val redirectURL = maybeRedirectURL orElse request.headers.get("Referer") getOrElse routes.Visitors.home.absoluteURL()
    localeForm.bindFromRequest.fold(
      formWithErrors => Redirect(redirectURL).flashing("error" -> Messages("locale.notChanged")),
      locale => Redirect(redirectURL).withCookies(Cookie(Play.langCookieName, locale))
    )
  }

  private val signupForm = Form(tuple(
    "firstname" -> nonEmptyText.verifying(maxLength(255)),
    "lastname" -> nonEmptyText.verifying(maxLength(255)),
    "email" -> email.verifying(maxLength(255)),
    "password" -> nonEmptyText(maxLength = 255).password
  ))

  def signup = WithMaybeUser { implicit request =>
    Ok(views.html.visitors.signup(signupForm))
  }

  def subscribe = WithMaybeUser.async { implicit request =>
    signupForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.visitors.signup(formWithErrors))),
      signupData => {
        val (firstName, lastName, email, password) = signupData
        UserService.create(UserCreate(firstName, lastName, email, true, DateTime.now), email, password) map { _ =>
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
