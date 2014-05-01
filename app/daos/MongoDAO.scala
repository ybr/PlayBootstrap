package daos

import reactivemongo.bson._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models._

trait MongoDAO extends DAO {
  val unique_violation = 11000

  implicit val mongoStringIdProvider = new IdProvider[String] {
    def toId(s: String) = new Id {
      val value = s
    }
  }

  implicit val mongoBSONObjectIdProvider = new IdProvider[BSONObjectID] {
    def toId(objectID: BSONObjectID) = new Id {
      val value = objectID.stringify
    }
  }

  implicit val idMongoFormat = Format[Id](
    (__ \ "$oid").read[String].map(Id(_)),
    Writes[Id](id => Json.obj("$oid" -> id.value))
  )

  // helps transform a reads _id to reads id
  val id: Reads[JsObject] = __.json.update((__ \ "id").json.copyFrom((__ \ "_id").json.pick)) andThen (__ \ "_id").json.prune
  // helps transform a writes id to a writes _id
  val _id: JsValue => JsValue = json => (__.json.update((__ \ "_id").json.copyFrom((__ \ "id").json.pick)) andThen (__ \ "id").json.prune).reads(json).get

  // transforms an identifiable to a js value
  def _id(identifiable: Identifiable): JsValue = _id(identifiable.id)
  def _id(id: Id): JsValue = Json.obj("_id" -> Json.obj("$oid" -> id.value))
}

case class MongoException(error: String, code: Int, message: String) extends Exception(error)
