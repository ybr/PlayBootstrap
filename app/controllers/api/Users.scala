package controllers.api

import scala.concurrent.Future

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

import playground.json._
import playground.json.Implicits._
import playground.log._
import playground.form.Mappings._
import playground.models._

import services._
import models.requests._
import utils._
import utils.credentials._
import JsonImplicits._
import App.Daos._

object Users extends Controller with Logger {
  // curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X GET http://localhost:9000/api/users
  def all = API.async(UserService.all.map { users =>
    Ok(Json.toJson(users))
  })

  // curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X POST http://localhost:9000/api/users -d '{"firstName":"Yohann","lastName":"Brédoux1","email":"yo.bre@domain.com","active":true,"creation":1398435384074}'
  def create = API.async(parse.json) { implicit request =>
    AcceptJson.async[UserCreate] { uc =>
      UserService.create(uc, uc.email, RandomPassword.generate) map { user =>
        Created(Json.toJson(user))
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
        case Some(updatedUser) => Ok(Json.toJson(updatedUser))
        case None => NotFound
      }
    }
  }
}
