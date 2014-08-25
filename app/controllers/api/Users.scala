package controllers.api

import scala.concurrent.Future

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits._

import playground.json._
import playground.json.Implicits._
import playground.log._
import playground.form.Mappings._
import playground.models._

import controllers.JsonImplicits._
import services._
import models.requests._
import utils._
import utils.credentials._

object Users extends Controller with JsonWriteable with Logger {
  // curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X GET http://localhost:9000/api/users
  def all = API.async(UserService.all.map(Ok(_)))

  // curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X POST http://localhost:9000/api/users -d '{"firstName":"Yohann","lastName":"Brédoux1","email":"yo.bre@domain.com","active":true,"creation":1398435384074}'
  def create = API.async(parse.json) { implicit request =>
    AcceptJson.async[UserCreate] { uc =>
      UserService.create(uc, uc.email, RandomPassword.generate) map { user =>
        Created(user)
      }
    }
  }

  // curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X PUT http://localhost:9000/api/users/1 -d '{"firstName":"Yo","lastName":"Bré","email":"yo.bre@gmail.com","active":true}'
  def update(id: Id) = API.async(parse.json) { implicit request =>
    AcceptJson.async[UserUpdate] { uu =>
      for {
        maybeUser <- UserService.byId(id)
        maybeUpdated <- FutureUtils.sequence(maybeUser.map(UserService.update(_, uu))).map(_.flatten)
      } yield maybeUpdated match {
        case Some(updatedUser) => Ok(updatedUser)
        case None => NotFound
      }
    }
  }

  // curl -v -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X PUT http://localhost:9000/api/users/1/password -d 'password=pwd&newpassword=new'
  def updatePassword(id: Id) = API.async { implicit request =>
    updatePasswordForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errors)),
      updatePassword => {
        val (password, newPassword) = updatePassword
        for {
          maybeUser <- UserService.byId(id)
          maybeLogin <- FutureUtils.sequence(maybeUser.map(UserService.getLogin)).map(_.flatten)
          maybeUpdated <- FutureUtils.sequence(maybeLogin.map(UserService.updatePassword(_, password, newPassword))).map(_.flatten)
        } yield maybeUpdated match {
          case Some(_) => Ok
          case None => NotFound
        }
      }
    )
  }
  val updatePasswordForm = Form(tuple(
    "password" -> nonEmptyText(maxLength = 255).password,
    "newpassword" -> nonEmptyText(maxLength = 255).password
  ))
}