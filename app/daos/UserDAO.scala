package daos

import models._
import models.requests._

trait UserDAO {
  def create(credentialsId: Id, request: UserCreate): Tx[User]
  def byLogin(login: String): Tx[Option[User]]
}
