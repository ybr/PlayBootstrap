package controllers

import scala.concurrent.Future

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._

import play.api.libs.concurrent.Execution.Implicits._

import models._
import models.requests._
import services._
import utils.Mappings._

class Users extends UserController {
  def home() = WithUser { implicit request =>
    Ok(views.html.users.home(request.session("login")))
  }

  val userUpdateForm = Form(mapping(
    "firstname" -> nonEmptyText.verifying(maxLength(255)),
    "lastname" -> nonEmptyText.verifying(maxLength(255)),
    "email" -> email.verifying(maxLength(255))
  )(UserUpdateForm.apply)(UserUpdateForm.unapply))

  def profile = WithUser { implicit request =>
    val knownData = UserUpdateForm(me.firstName, me.lastName, me.email)
    Ok(views.html.users.profile(userUpdateForm.fill(knownData)))
  }

  def profileUpdate = WithUser.async { implicit request =>
    userUpdateForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.users.profile(formWithErrors))),
      updateData => {
        userService.update(me, UserUpdate(updateData.firstName, updateData.lastName, updateData.email, me.active)).map { _ =>
          Redirect(Controllers.routes.Users.home).flashing("success" -> Messages("flash.users.profile.update"))
        }
      }
    )
  }

  val updatePasswordForm = Form(tuple(
    "password" -> nonEmptyText(maxLength = 255).password,
    "newpassword" -> nonEmptyText(maxLength = 255).password
  ))

  def password = WithUser { implicit request =>
    Ok(views.html.users.password(updatePasswordForm))
  }

  def passwordUpdate = WithUser.async { implicit request =>
    updatePasswordForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.users.password(formWithErrors))),
      updatePassword => {
        val (password, newPassword) = updatePassword
        userService.updatePassword(request.session("login"), password, newPassword) map {
          case Some(_) => Redirect(Controllers.routes.Users.home).flashing("success" -> Messages("flash.users.password.update"))
          case None => BadRequest(views.html.users.password(
            updatePasswordForm.fill(updatePassword).withError("password", "error.password"))
          )
        }
      }
    )
  }
}

case class UserUpdateForm(firstName: String, lastName: String, email: String)
