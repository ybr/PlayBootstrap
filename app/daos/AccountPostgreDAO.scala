package daos

import scala.concurrent.Future

import play.api.Play._
import play.api.db.DB

import anorm.SQL

import models._
import models.requests._

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
  }
}
