package daos

import scala.concurrent.Future

import models._

trait AccountDAO {
  def create(login: String, password: String): Future[Boolean]
}
