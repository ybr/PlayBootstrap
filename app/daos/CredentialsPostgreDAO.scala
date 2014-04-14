package daos

import scala.concurrent.Future

import play.api.Play._
import play.api.db.DB

import anorm.SQL

import models._
import models.exceptions._

object CredentialsPostgreDAO extends CredentialsDAO with PostgreDAO {
  def create(login: String, password: String, salt: String): Future[String] = Future {
    DB.withConnection { implicit c =>
      SQL("""
        INSERT INTO credentials(login, password, salt)
        VALUES({login}, {password}, {salt})
      """).on(
        "login" -> login,
        "password" -> password,
        "salt" -> salt
      ).executeInsert().map(_ => login).get
    }
  } transform(identity, {
    case x: org.postgresql.util.PSQLException => x.getSQLState match {
      case `unique_violation` => AccountAlreadyExistsException(login, x)
      case _ => x
    }
    case x: org.h2.jdbc.JdbcSQLException => x.getErrorCode.toString match {
      case `unique_violation` => AccountAlreadyExistsException(login, x)
      case _ => x
    }
  })
}
