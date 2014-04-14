package daos

import scala.concurrent.Future

import models._

trait CredentialsDAO {
  def create(login: String, password: String, salt: String): Future[String]
}
