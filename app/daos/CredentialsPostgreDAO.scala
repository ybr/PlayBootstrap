package daos

import anorm.SQL
import anorm.SqlParser._

import models._
import models.exceptions._

object CredentialsPostgreDAO extends CredentialsDAO with PostgreDAO {
  def create(login: String, password: String, salt: String): Tx[Id] = Tx { implicit c =>
    SQL("""
      INSERT INTO T_CREDENTIALS(login, password, salt)
      VALUES({login}, {password}, {salt})
    """).on(
      "login" -> login,
      "password" -> password,
      "salt" -> salt
    ).executeInsert().map(Id(_)).get
  } mapFailure {
    case x: org.postgresql.util.PSQLException => x.getSQLState match {
      case `unique_violation` => AccountAlreadyExistsException(login, x)
      case _ => x
    }
    case x: org.h2.jdbc.JdbcSQLException => x.getErrorCode.toString match {
      case `unique_violation` => AccountAlreadyExistsException(login, x)
      case _ => x
    }
  }

  def salt(login: String): Tx[Option[String]] = Tx { implicit c =>
    SQL("""
      SELECT salt
      FROM T_CREDENTIALS
      WHERE login = {login}
    """).on("login" -> login).as(scalar[String].singleOpt)
  }

  def authenticate(login: String, password: String): Tx[Option[String]] = Tx { implicit c =>
    SQL("""
      SELECT
        login
      FROM
        T_CREDENTIALS
      WHERE
        login = {login}
        AND password = {password}
    """).on(
      "login" -> login,
      "password" -> password
    ).as(scalar[String].singleOpt)
  }
}
