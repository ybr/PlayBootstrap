package services

import scala.concurrent.Future

import utils.credentials._

object CredentialsService {
  def credentialsDAO = daos.CredentialsPostgreDAO

  def create(login: String, password: String): Future[String] = {
    val salt = SaltGeneratorUUID.generateSalt
    val hashedPassword = PasswordHasherSha512ToBase64.hashPassword(password, salt)
    credentialsDAO.create(login, hashedPassword, salt)
  }
}
