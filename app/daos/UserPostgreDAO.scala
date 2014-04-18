package daos

import scala.concurrent.Future
import scala.language.postfixOps

import anorm.SQL
import anorm.SqlParser._

import play.api.Play.current
import play.api.db.DB

import ybr.sql.SqlParsers._

import models._
import models.requests._
import models.exceptions._
import utils.SqlParsers._

object UserPostgreDAO extends UserDAO with PostgreDAO {
  val simple = id("id") ~
    str("first_name") ~
    str("last_name") ~
    str("email") ~
    joda.date("creation") map flatten

  def create(request: UserCreate, login: String, password: String, salt: String): Future[User] = Future {
    DB.withTransaction { implicit c =>
      val credentialsId = SQL("""
        INSERT INTO T_CREDENTIALS(login, password, salt, creation)
        VALUES ({login}, {password}, {salt}, {creation})
      """).on(
        "login" -> login,
        "password" -> password,
        "salt" -> salt,
        "creation" -> request.creation.toDate
      ).executeInsert().map(Id(_)).get

      SQL("""
        INSERT INTO T_USER(first_name, last_name, email, creation, credentials_id)
        VALUES ({firstName}, {lastName}, {email}, {creation}, {credentialsId})
      """).on(
        "firstName" -> request.firstName,
        "lastName" -> request.lastName,
        "email" -> request.email,
        "creation" -> request.creation.toDate,
        "credentialsId" -> credentialsId.value.toInt
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

  def authenticate(login: String, password: String): Future[Option[User]] = Future {
    DB.withTransaction { implicit c =>
      SQL("""
        SELECT
          u.*
        FROM
          T_CREDENTIALS c
          INNER JOIN T_USER u ON u.credentials_id = c.id
        WHERE
          login = {login}
          AND password = {password}
      """).on(
        "login" -> login,
        "password" -> password
      ).as(simple.singleOpt.map(_.map(User.apply _ tupled)))
    }
  }

  def byLogin(login: String): Future[Option[User]] = Future {
    DB.withTransaction { implicit c =>
      SQL("""
        SELECT
          u.*
        FROM
          T_CREDENTIALS c
          INNER JOIN T_USER u ON u.credentials_id = c.id
        WHERE c.login = {login}
      """).on(
        "login" -> login
      ).as(simple.singleOpt.map(_.map(User.apply _ tupled)))
    }
  }
}
