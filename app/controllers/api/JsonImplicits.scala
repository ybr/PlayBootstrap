package controllers.api

import play.api.libs.json._

import playground.json.Implicits._

import models._
import models.requests._

object JsonImplicits {
  implicit val userWrites = Json.writes[User]
  implicit val userCreateReads = Json.reads[UserCreate]
  implicit val userUpdateReads = Json.reads[UserUpdate]

  implicit val adminWrites = Json.writes[Admin]
  implicit val adminCreateReads = Json.reads[AdminCreate]
  implicit val adminUpdateReads = Json.reads[AdminUpdate]
}
