package utils

import scala.concurrent.Future
import scala.util._

import play.api.mvc._
import play.api.mvc.BodyParsers.parse
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._

import ybr.playground.log._

import controllers.Formats._
import models.core._

// TODO move to playground
object AcceptJson extends Results with JsonWriteable with Logger {
  def async[A](action: A => Future[SimpleResult])(implicit reader: Reads[A], request: Request[JsValue]) = Action.async(parse.json) { request =>
    reader.reads(request.body) match {
      case JsError(errors) => Future.successful(UnprocessableEntity(errors))
      case JsSuccess(value, _) => action(value) recover {
        case x: CodedException => {
          log.debug("Expected error", x)
          BadRequest(Json.obj(
            "code" -> x.code,
            "message" -> x.getMessage
          ))
        }
        case x => {
          log.error("Unexpected error", x)
          InternalServerError(Json.obj("message" -> "An unexpected error occured"))
        }
      }
    }
  } (request)

  def apply[A](action: A => SimpleResult)(implicit reader: Reads[A], request: Request[JsValue]) = async[A](a => Future(action(a)))
}
