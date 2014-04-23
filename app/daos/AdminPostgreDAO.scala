package daos

import scala.concurrent.Future
import scala.language.postfixOps

import anorm.SQL
import anorm.SqlParser._

import play.api.db.DB
import play.api.Play.current

import ybr.sql.SqlParsers._

import models._
import models.requests._
import models.exceptions._
import utils.SqlParsers._

object AdminPostgreDAO extends AdminDAO with PostgreDAO {
  val simple = id("id") ~
    str("first_name") ~
    str("last_name") ~
    str("email") ~
    joda.date("creation") map flatten

  def create(request: AdminCreate, login: String, password: String, salt: String): Future[Admin] = Future {
    DB.withTransaction { implicit c =>
      val credentialsId = SQL("""
        INSERT INTO T_CREDENTIALS(login, password, salt, creation)
        VALUES ({login}, {password}, {salt}, {creation})
      """).on(
        "login" -> login,
        "password" -> password,
        "salt" -> salt,
        "creation" -> param(request.creation)
      ).executeInsert().map(Id(_)).get

      SQL("""
        INSERT INTO T_ADMIN(first_name, last_name, email, creation, credentials_id)
        VALUES ({firstName}, {lastName}, {email}, {creation}, {credentialsId})
      """).on(
        "firstName" -> request.firstName,
        "lastName" -> request.lastName,
        "email" -> request.email,
        "creation" -> param(request.creation),
        "credentialsId" -> param(credentialsId)
      ).executeInsert().map(Id(_)).map(request.withId).get
    }
  }.transform(identity, {
    case x: org.postgresql.util.PSQLException => x.getSQLState match {
      case `unique_violation` => AccountAlreadyExistsException(login, x)
      case _ => x
    }
    case x: org.h2.jdbc.JdbcSQLException => x.getErrorCode.toString match {
      case `unique_violation` => AccountAlreadyExistsException(login, x)
      case _ => x
    }
  })

  def salt(login: String): Future[Option[String]] = Future {
    DB.withTransaction { implicit c =>
      SQL("""
        SELECT salt
        FROM T_CREDENTIALS
        WHERE login = {login}
      """).on("login" -> login).as(scalar[String].singleOpt)
    }
  }

  def authenticate(login: String, password: String): Future[Option[Admin]] = Future {
    DB.withTransaction { implicit c =>
      SQL("""
        SELECT
          a.*
        FROM
          T_CREDENTIALS c
          INNER JOIN T_ADMIN a ON a.credentials_id = c.id
        WHERE
          login = {login}
          AND password = {password}
      """).on(
        "login" -> login,
        "password" -> password
      ).as(simple.singleOpt.map(_.map(Admin.apply _ tupled)))
    }
  }

  def count(): Future[Long] = Future {
    DB.withTransaction { implicit c =>
      SQL("SELECT COUNT(*) FROM T_ADMIN").as(scalar[Long].single)
    }
  }

  def byLogin(login: String): Future[Option[Admin]] = Future {
    DB.withTransaction { implicit c =>
      SQL("""
        SELECT
          a.*
        FROM
          T_CREDENTIALS c
          INNER JOIN T_ADMIN a ON a.credentials_id = c.id
        WHERE c.login = {login}
      """).on(
        "login" -> login
      ).as(simple.singleOpt.map(_.map(Admin.apply _ tupled)))
    }
  }
}
