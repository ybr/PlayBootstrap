package controllers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import org.joda.time.DateTime

import models._
import models.requests._

// TODO move to playground
object Formats {
  implicit val stringIdProvider = new IdProvider[String] {
    def toId(s: String) = new Id {
      val value = s
    }
  }

  implicit val idFormat = Format[Id](
    (__).read[String].map(Id(_)),
    Writes[Id](id => JsString(id.value))
  )

  implicit val joDateTimeFormat = Format[DateTime](
    (__).read[Long].map(new DateTime(_)),
    Writes[DateTime] { dt =>
      Json.obj(
        "millis" -> dt.getMillis,
        "human" -> dt.toString()
      )
    }
  )

  implicit val validationErrorWrites = Writes[ValidationError] { error =>
    Json.obj(
      "message" -> error.message,
      "args" -> error.args.map(_.toString)
    )
  }
  implicit val jsErrorWrites = Writes[(JsPath, Seq[ValidationError])] {
    case (path, errors) => Json.obj(
      "path" -> JsString(path.toString),
      "errors" -> Json.toJson(errors)
    )
  }

  implicit val userWrites = Json.writes[User]
  implicit val userCreateReads = Json.reads[UserCreate]
  implicit val userUpdateReads = Json.reads[UserUpdate]
}
