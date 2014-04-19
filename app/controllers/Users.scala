package controllers

import scala.concurrent.Future

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._

import play.api.libs.concurrent.Execution.Implicits._

import models.requests._
import services._

object Users extends UserController {
  def home() = WithUser { implicit request =>
    Ok(views.html.users.home())
  }

  def userUpdateForm = Form(mapping(
    "firstname" -> nonEmptyText.verifying(maxLength(255)),
    "lastname" -> nonEmptyText.verifying(maxLength(255)),
    "email" -> email.verifying(maxLength(255))
  )(UserUpdate.apply)(UserUpdate.unapply))

  def profile = WithUser { implicit request =>
    val me = request.me
    val knownData = UserUpdate(me.firstName, me.lastName, me.email)
    Ok(views.html.users.profile(userUpdateForm.fill(knownData)))
  }

  def profileUpdate = WithUser.async { implicit request =>
    userUpdateForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.users.profile(formWithErrors))),
      userUpdate => {
        UserService.update(request.me, userUpdate).map { _ =>
          Redirect(routes.Users.home).flashing("success" -> Messages("flash.users.profile.update"))
        }
      }
    )
  }
}
