package controllers.api

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

import playground.models._
import playground.json._

import utils._
import models.requests._
import services._
import JsonImplicits._
import App.Daos._

object Admins extends Controller {
  // curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X POST http://localhost:9000/api/admins -d '{"firstName":"Yohann","lastName":"Brédoux","email":"yo.bre@domain.com","active":true,"creation":1398435341051}'
  def create = API.async(parse.json) { implicit request =>
    AcceptJson.async[AdminCreate] { ac =>
      AdminService.create(ac, ac.email, credentials.RandomPassword.generate, None).map { admin =>
        Created(Json.toJson(admin))
      }
    }
  }

  // curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X GET http://localhost:9000/api/admins
  def all = API.async(AdminService.all.map { admins =>
    Ok(Json.toJson(admins))
  })

  // curl -v -H "Content-Type: application/json" -H "Authorization: Basic YXBpOmNoYW5nZW1l" -X PUT http://localhost:9000/api/admins/9 -d '{"firstName":"Yo","lastName":"Bre","email":"yo.bre@domain.com","active":false}'
  def update(id: Id) = API.async(parse.json) { implicit request =>
    AcceptJson.async[AdminUpdate] { au =>
      for {
        maybeAdmin <- AdminService.byId(id)
        maybeUpdated <- FutureUtils.sequence(maybeAdmin.map(AdminService.update(_, au))).map(_.flatten)
      } yield maybeUpdated match {
        case Some(updatedAdmin) => Ok(Json.toJson(updatedAdmin))
        case None => NotFound
      }
    }
  }
}
