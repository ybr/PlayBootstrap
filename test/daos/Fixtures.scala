package daos

import org.joda.time._

import playground.models._

import models._
import models.requests._

object Fixtures {
  val admin0 = Admin(id("0"), "firstName0", "lastName0", "admin0@domain.com", true, DateTime.now)
  val admin1 = Admin(id("1"), "firstName1", "lastName1", "admin1@domain.com", true, DateTime.now)

  val adminCreate0 = AdminCreate("firstName0", "lastName0", "admin0@domain.com", true, DateTime.now)
  val adminCreate1 = AdminCreate("firstName1", "lastName1", "admin1@domain.com", true, DateTime.now)

  val adminUpdate0 = AdminUpdate.from(admin0)
  val adminUpdate1 = AdminUpdate.from(admin1)

  val user0 = User(id("0"), "firstName0", "lastName0", "user0@domain.com", true, DateTime.now)
  val user1 = User(id("1"), "firstName1", "lastName1", "user1@domain.com", true, DateTime.now)

  val userCreate0 = UserCreate("firstName0", "lastName0", "user0@domain.com", true, DateTime.now)
  val userCreate1 = userCreate0.copy(firstName = "firstName1", lastName = "lastName1", email = "user1@domain.com")

  val userUpdate0 = UserUpdate.from(user0)
  val userUpdate1 = UserUpdate.from(user1)

  private def id(s: String) = new Id { val value = s }
}
