package services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

import utils.credentials._
import daos._
import models.requests._
import models._

object CredentialsService {
  def credentialsDAO = CredentialsPostgreDAO
  def userDAO = UserPostgreDAO

  def create(login: String, password: String, request: UserCreate): Future[User] = {
    val salt = SaltGeneratorUUID.generateSalt
    val hashedPassword = PasswordHasherSha512ToBase64.hashPassword(password, salt)
    val tx = for {
      id <- credentialsDAO.create(login, hashedPassword, salt)
      user <- userDAO.create(id, request)
    } yield user
    tx.commit
  }

  def authenticate(login: String, password: String): Future[Option[String]] = {
    (for {
      maybeSalt <- credentialsDAO.salt(login)
      maybeLogin <- Tx.sequence(maybeSalt.map { salt =>
        credentialsDAO.authenticate(login, PasswordHasherSha512ToBase64.hashPassword(password, salt))
      }).map(_.flatten)
    } yield maybeLogin).commit
  }
}
