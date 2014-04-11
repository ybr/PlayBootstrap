package controllers

import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.libs.concurrent.Execution.Implicits._

import models.requests._
import utils._

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
        daos.AccountPostgreDAO.create(email, password).map { _ =>
          Redirect(routes.Visitors.signin).flashing("success" -> i18n.Messages("flash.visitors.subscribe"))
        }
      }
    )
  }

  def signin() = TODO
}
