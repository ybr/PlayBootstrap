package daos

import scala.concurrent._

import org.joda.time._

import org.specs2.mutable._

import models.requests._
import models.exceptions._

object AdminPostgreDaoSpec extends Specification with DaoSpec {
  "AdminPostgreDAO" should {
    "create an account" in new WithInMemoryDB {
      val admin = await(AdminPostgreDAO.create(AdminCreate("firstName", "lastName", "admin@domain.com", DateTime.now), "login", "pwd", "someSalt"))
      admin.id.value.length > 0 must beTrue
    }

    "not create an account when the login already exists" in new WithInMemoryDB {
      await {
        AdminPostgreDAO.create(AdminCreate("firstName0", "lastName0", "admin@domain.com", DateTime.now),"login",  "pwd", "someSalt")
      }
      await {
        AdminPostgreDAO.create(AdminCreate("firstName1", "lastName1", "admin1@domain.com", DateTime.now),"login",  "otherPwd", "someOtherSalt")
      } must throwAn[AccountAlreadyExistsException]
    }

    "retrieve salt" in new WithInMemoryDB {
      await(AdminPostgreDAO.create(AdminCreate("firstName", "lastName", "admin@domain.com", DateTime.now), "login", "pwd", "someSalt"))
      await {
        AdminPostgreDAO.salt("login")
      }.get must be equalTo("someSalt")
    }

    "retrieve admin by login" in new WithInMemoryDB {
      await(AdminPostgreDAO.create(AdminCreate("firstName", "lastName", "admin@domain.com", DateTime.now), "login", "pwd", "someSalt"))
      await {
        AdminPostgreDAO.byLogin("login")
      } must beSome
    }

    "return None if the admin login is unknown" in new WithInMemoryDB {
      await {
        AdminPostgreDAO.byLogin("login")
      } must beNone
    }

    "authenticate an admin with its login and password" in new WithInMemoryDB {
      await(AdminPostgreDAO.create(AdminCreate("firstName", "lastName", "admin@domain.com", DateTime.now), "login", "pwd", "someSalt"))
      await {
        AdminPostgreDAO.authenticate("login", "pwd")
      } must beSome
    }

    // "retrieve by id" in new WithInMemoryDB {
    //   val admin = await(AdminPostgreDAO.create(AdminCreate("firstName", "lastName", "admin@domain.com", DateTime.now), "login", "pwd", "someSalt"))
    //   await {
    //     AdminPostgreDAO.byId(admin.id)
    //   } must beSome
    // }

    // "update the user" in new WithInMemoryDB {
    //   val user = await(UserPostgreDAO.create(UserCreate("firstName", "lastName", "user@domain.com", DateTime.now), "login", "pwd", "someSalt"))
    //   val newUser = await(UserPostgreDAO.update(user, UserUpdate("newFirstName", "newLastName", "user@newdomain.com")))
    //   val readUser = await(UserPostgreDAO.byId(user.id)).get

    //   readUser.id.value must be equalTo(user.id.value)
    //   readUser.firstName must be equalTo("newFirstName")
    //   readUser.lastName must be equalTo("newLastName")
    //   readUser.email must be equalTo("user@newdomain.com")
    //   readUser.creation must be equalTo(user.creation)
    // }
  }
}
