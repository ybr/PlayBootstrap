package controllers

import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._

import play.api.libs.concurrent.Execution.Implicits._

import models.exceptions._

object Visitors extends Controller {
  def accountDAO = daos.AccountPostgreDAO

  def home() = Action { implicit request =>
    Ok(views.html.users.home())
  }

  val emailPwdForm = Form(tuple(
    "email" -> email,
    "password" -> nonEmptyText
  ))

  def signup() = Action { implicit request =>
    Ok(views.html.users.signup(emailPwdForm))
  }

  def subscribe() = Action.async { implicit request =>
    emailPwdForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.users.signup(formWithErrors))),
      signupData => {
        val (email, password) = signupData
        accountDAO.create(email, password) map { _ =>
          Redirect(routes.Visitors.signin).flashing("success" -> i18n.Messages("flash.visitors.subscribe"))
        } recover {
          case AccountAlreadyExistsException(login, _) =>
            implicit val flash = Flash() + ("error" -> Messages("flash.visitors.alreadyExists", login))
            BadRequest(views.html.users.signup(emailPwdForm.fill(signupData)))
        }
      }
    )
  }

  def signin() = TODO
}
