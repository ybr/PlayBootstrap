package daos

import models._

trait CredentialsDAO {
  def create(login: String, password: String, salt: String): Tx[Id]
  def salt(login: String): Tx[Option[String]]
  def authenticate(login: String, password: String): Tx[Option[String]]
}
