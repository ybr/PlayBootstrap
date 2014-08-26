package daos

import scala.concurrent.Future

import playground.models._

import models._
import models.requests._

trait UserDAO {
  def create(request: UserCreate, login: String, password: String, salt: String): Future[User]
  def update(user: User, request: UserUpdate): Future[Option[User]]

  def salt(login: String): Future[Option[String]]
  def authenticate(login: String, password: String): Future[Option[User]]

  def byLogin(login: String): Future[Option[User]]
  def byId(id: Id): Future[Option[User]]
  def all(): Future[Seq[User]]
}
