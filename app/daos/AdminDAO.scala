package daos

import scala.concurrent.Future

import models._
import models.requests._

trait AdminDAO {
  def create(request: AdminCreate, login: String, password: String, salt: String): Future[Admin]

  def salt(login: String): Future[Option[String]]
  def authenticate(login: String, password: String): Future[Option[Admin]]

  def all(): Future[Seq[Admin]]
  def byLogin(login: String): Future[Option[Admin]]
}
