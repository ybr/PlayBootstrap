package daos

import scala.concurrent.Future

import play.api.Play._
import play.api.libs.json._

import ybr.playground.log._

import reactivemongo.core.commands.LastError
import reactivemongo.bson._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection

import models._
import models.requests._
import models.exceptions._

object UserMongoDAO extends UserDAO with MongoDAO with Logger {
  private val collection = ReactiveMongoPlugin.db.collection[JSONCollection]("users")

  implicit val userUpdateWrites = Json.writes[UserUpdate]
  implicit val userFormat = Format[User](
    Json.reads.compose(id),
    Json.writes.transform(_id)
  )

  def create(request: UserCreate, login: String, password: String, salt: String): Future[User] = {
    val id = Id(BSONObjectID.generate)
    val user = request.withId(id)
    collection.insert(
      Json.toJson(user).as[JsObject] +
        ("login" -> JsString(login)) +
        ("password" -> JsString(password)) +
        ("salt" -> JsString(salt))
    ).map(_ => user).transform(identity, {
      case x@LastError(_, _, Some(unique_violation), _, _, _, _) => AccountAlreadyExistsException(login, x)
    })
  }

  def update(user: User, request: UserUpdate): Future[Option[User]] = collection.update(
    _id(user),
    Json.obj("$set" -> request)
  ) flatMap { _ =>
    byId(user.id)
  }

  def updatePassword(login: String, password: String, salt: String): Future[Option[Unit]] = collection.update(
    Json.obj("login" -> login),
    Json.obj("$set" -> Json.obj(
      "password" -> password,
      "salt" -> salt
    ))
  ).map(_.updated match {
    case 0 => None
    case _ => Some(())
  })

  def salt(login: String): Future[Option[String]] = {
    log.debug(s"Salt for ${login}")
    collection.find(
      Json.obj("login" -> login),
      Json.obj("salt" -> 1)
    )
    .one[JsValue]
    .map(_.flatMap { js =>
      (js \ "salt").asOpt[String]
    })
  }

  def authenticate(login: String, password: String): Future[Option[User]] = collection.find(
    Json.obj(
      "login" -> login,
      "password" -> password,
      "active" -> true
    )
  ).one[User]

  def byLogin(login: String): Future[Option[User]] = collection.find(Json.obj("login" -> login)).one[User]

  def all(): Future[Seq[User]] = collection.find(Json.obj()).cursor[User].collect[Seq]()

  def byId(id: Id): Future[Option[User]] = collection.find(_id(id)).one[User]
}
