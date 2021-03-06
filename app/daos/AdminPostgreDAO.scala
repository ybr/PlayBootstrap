package daos

import scala.concurrent.Future
import scala.language.postfixOps

import anorm.SQL
import anorm.SqlParser._

import play.api.db.DB
import play.api.Play.current

import playground.db.sql.SqlParsers._
import playground.models._

import models._
import models.requests._
import models.exceptions._

object AdminPostgreDAO extends AdminDAO with PostgreDAO {
  val simple = id("id") ~
    str("first_name") ~
    str("last_name") ~
    str("email") ~
    bool("active") ~
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

  def update(admin: Admin, request: AdminUpdate): Future[Option[Admin]] = Future {
    DB.withTransaction { implicit c =>
      SQL("""
        UPDATE T_ADMIN
        SET
          first_name = {firstName},
          last_name = {lastName},
          email = {email},
          active = {active}
        WHERE id = {id}
      """).on(
        "id" -> param(admin.id),
        "firstName" -> request.firstName,
        "lastName" -> request.lastName,
        "email" -> request.email,
        "active" -> request.active
      ).executeUpdate
    }
  } flatMap { _ =>
    byId(admin.id)
  }

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
          c.login = {login}
          AND c.password = {password}
          AND a.active
      """).on(
        "login" -> login,
        "password" -> password
      ).as(simple.singleOpt.map(_.map(Admin.apply _ tupled)))
    }
  }

  def all(): Future[Seq[Admin]] = Future {
    DB.withTransaction { implicit c =>
      SQL("SELECT * FROM T_ADMIN ORDER BY creation").as(simple *).map(Admin.apply _ tupled)
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

  def byId(id: Id): Future[Option[Admin]] = Future {
    DB.withTransaction { implicit c =>
      SQL("""
        SELECT *
        FROM T_ADMIN
        WHERE id = {id}
      """).on(
        "id" -> param(id)
      ).as(simple.singleOpt.map(_.map(Admin.apply _ tupled)))
    }
  }
}
