package daos

import scala.concurrent._

import org.joda.time._

import org.specs2.mutable._

import models.requests._
import models.exceptions._

// TODO complete tests
object UserPostgreDaoSpec extends Specification with DaoSpec {
  "UserPostgreDAO" should {
    "create an account" in new WithInMemoryDB {
      val user = await(UserPostgreDAO.create(UserCreate("firstName", "lastName", "user@domain.com", true, DateTime.now), "login", "pwd", "someSalt"))
      user.id.value.length > 0 must beTrue
    }

    "not create an account when the login already exists" in new WithInMemoryDB {
      await {
        UserPostgreDAO.create(UserCreate("firstName0", "lastName0", "user0@domain.com", true, DateTime.now),"login",  "pwd", "someSalt")
      }
      await {
        UserPostgreDAO.create(UserCreate("firstName1", "lastName1", "user1@domain.com", true, DateTime.now),"login",  "otherPwd", "someOtherSalt")
      } must throwAn[AccountAlreadyExistsException]
    }

    "retrieve salt" in new WithInMemoryDB {
      await(UserPostgreDAO.create(UserCreate("firstName", "lastName", "user@domain.com", true, DateTime.now), "login", "pwd", "someSalt"))
      await {
        UserPostgreDAO.salt("login")
      }.get must be equalTo("someSalt")
    }

    "retrieve user by login" in new WithInMemoryDB {
      await(UserPostgreDAO.create(UserCreate("firstName", "lastName", "user@domain.com", true, DateTime.now), "login", "pwd", "someSalt"))
      await {
        UserPostgreDAO.byLogin("login")
      } must beSome
    }

    "retrieve user by id" in new WithInMemoryDB {
      val user = await(UserPostgreDAO.create(UserCreate("firstName", "lastName", "user@domain.com", true, DateTime.now), "login", "pwd", "someSalt"))
      await {
        UserPostgreDAO.byId(user.id)
      } must beSome
    }

    "return None if the user login is unknown" in new WithInMemoryDB {
      await {
        UserPostgreDAO.byLogin("login")
      } must beNone
    }

    "authenticate a user with its login and password" in new WithInMemoryDB {
      await(UserPostgreDAO.create(UserCreate("firstName", "lastName", "user@domain.com", true, DateTime.now), "login", "pwd", "someSalt"))
      await {
        UserPostgreDAO.authenticate("login", "pwd")
      } must beSome
    }

    "retrieve by id" in new WithInMemoryDB {
      val user = await(UserPostgreDAO.create(UserCreate("firstName", "lastName", "user@domain.com", true, DateTime.now), "login", "pwd", "someSalt"))
      await {
        UserPostgreDAO.byId(user.id)
      } must beSome
    }

    "update the user" in new WithInMemoryDB {
      val user = await(UserPostgreDAO.create(UserCreate("firstName", "lastName", "user@domain.com", true, DateTime.now), "login", "pwd", "someSalt"))
      val newUser = await(UserPostgreDAO.update(user, UserUpdate("newFirstName", "newLastName", "user@newdomain.com")))
      val readUser = await(UserPostgreDAO.byId(user.id)).get

      readUser.id.value must be equalTo(user.id.value)
      readUser.firstName must be equalTo("newFirstName")
      readUser.lastName must be equalTo("newLastName")
      readUser.email must be equalTo("user@newdomain.com")
      readUser.creation must be equalTo(user.creation)
    }
  }
}
