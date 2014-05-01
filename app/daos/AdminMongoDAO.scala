package daos

import scala.concurrent.Future

import play.api.Play._
import play.api.libs.json._

import reactivemongo.core.commands.LastError
import reactivemongo.bson._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection

import models._
import models.requests._
import models.exceptions._

object AdminMongoDAO extends AdminDAO with MongoDAO {
  private val collection = ReactiveMongoPlugin.db.collection[JSONCollection]("admins")

  implicit val adminUpdateWrites = Json.writes[AdminUpdate]
  implicit val adminFormat = Format[Admin](
    Json.reads.compose(id),
    Json.writes.transform(_id)
  )

  def create(request: AdminCreate, login: String, password: String, salt: String): Future[Admin] = {
    val id = Id(BSONObjectID.generate)
    val admin = request.withId(id)
    collection.insert(
      Json.toJson(admin).as[JsObject] +
        ("login" -> JsString(login)) +
        ("password" -> JsString(password)) +
        ("salt" -> JsString(salt))
    ).map(_ => admin).transform(identity, {
      case x@LastError(_, _, Some(unique_violation), _, _, _, _) => AccountAlreadyExistsException(login, x)
    })
  }

  def update(admin: Admin, request: AdminUpdate): Future[Option[Admin]] = collection.update(
    _id(admin),
    Json.obj("$set" -> request)
  ) flatMap { _ =>
    byId(admin.id)
  }

  def salt(login: String): Future[Option[String]] = collection.find(
    Json.obj("login" -> login),
    Json.obj("salt" -> 1)
  )
  .one[JsValue]
  .map(_.flatMap { js =>
    (js \ "salt").asOpt[String]
  })

  def authenticate(login: String, password: String): Future[Option[Admin]] = collection.find(
    Json.obj(
      "login" -> login,
      "password" -> password,
      "active" -> true
    )
  ).one[Admin]

  def all: Future[Seq[Admin]] = collection.find(Json.obj()).cursor[Admin].collect[Seq]()

  def byLogin(login: String): Future[Option[Admin]] = collection.find(Json.obj("login" -> login)).one[Admin]

  def byId(id: Id): Future[Option[Admin]] = collection.find(_id(id)).one[Admin]
}
