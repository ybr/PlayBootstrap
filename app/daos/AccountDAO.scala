package daos

import scala.concurrent.Future

import models._
import models.requests._

trait AccountDAO {
  def create(login: String, password: String): Future[Boolean]
}
