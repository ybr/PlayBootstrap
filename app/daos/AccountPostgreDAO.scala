package daos

import scala.concurrent.Future

import play.api.Play._
import play.api.db.DB

import anorm.SQL

import models._
import models.exceptions._

object AccountPostgreDAO extends AccountDAO with PostgreDAO {
  def create(login: String, password: String): Future[Boolean] = Future {
    DB.withConnection { implicit c =>
      SQL("""
        INSERT INTO account(login, password)
        VALUES({login}, {password})
      """).on(
        "login" -> login,
        "password" -> password
      ).executeInsert().isDefined
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
