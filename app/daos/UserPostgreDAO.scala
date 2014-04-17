package daos

import scala.language.postfixOps

import anorm.SQL
import anorm.SqlParser._

import models._
import models.requests._
import utils.SqlParsers._

object UserPostgreDAO extends UserDAO with PostgreDAO {
  val simple = id("id") ~
    str("first_name") ~
    str("last_name") ~
    str("email") ~
    joda.date("creation") map flatten

  def create(credentialsId: Id, request: UserCreate): Tx[User] = Tx { implicit c =>
    SQL("""
      INSERT INTO T_USER(first_name, last_name, creation, credentials_id)
      VALUES ({firstName}, {lastName}, {creation}, {credentialsId})
    """).on(
      "firstName" -> request.firstName,
      "lastName" -> request.lastName,
      "creation" -> request.creation.toDate,
      "credentialsId" -> credentialsId.value
    ).executeInsert().map(id => request.withId(Id(id))).get
  }

  def byLogin(login: String): Tx[Option[User]] = Tx { implicit c =>
    SQL("""
      SELECT
        u.*,
        c.login AS email
      FROM
        T_USER u
        INNER JOIN T_CREDENTIALS c ON c.id = u.credentials_id
      WHERE
        c.login = {login}
    """).on(
      "login" -> login
    ).as(simple.singleOpt.map(_.map(User.apply _ tupled)))
  }
}
