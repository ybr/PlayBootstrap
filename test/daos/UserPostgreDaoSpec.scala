package daos

import scala.concurrent._

import org.joda.time._

import org.specs2.mutable._

import models.requests._
import models.exceptions._

object UserPostgreDaoSpec extends Specification with DaoSpec {
  "UserPostgreDAO" should {
    "create an account" in new WithInMemoryDB {
      val user = await(UserPostgreDAO.create(Fixtures.userCreate0, "login", "pwd", "someSalt"))
      user.id.value.length > 0 must beTrue
    }

    "not create an account when the login already exists" in new WithInMemoryDB {
      await {
        UserPostgreDAO.create(Fixtures.userCreate0, "login",  "pwd", "someSalt")
      }
      await {
        UserPostgreDAO.create(Fixtures.userCreate1, "login",  "otherPwd", "someOtherSalt")
      } must throwAn[AccountAlreadyExistsException]
    }

    "retrieve salt" in new WithInMemoryDB {
      await(UserPostgreDAO.create(Fixtures.userCreate0, "login", "pwd", "someSalt"))
      await {
        UserPostgreDAO.salt("login")
      }.get must be equalTo("someSalt")
    }

    "retrieve user by login" in new WithInMemoryDB {
      await(UserPostgreDAO.create(Fixtures.userCreate0, "login", "pwd", "someSalt"))
      await {
        UserPostgreDAO.byLogin("login")
      } must beSome
    }

    "retrieve user by id" in new WithInMemoryDB {
      val user = await(UserPostgreDAO.create(Fixtures.userCreate0, "login", "pwd", "someSalt"))
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
      await(UserPostgreDAO.create(Fixtures.userCreate0, "login", "pwd", "someSalt"))
      await {
        UserPostgreDAO.authenticate("login", "pwd")
      } must beSome
    }

    "retrieve by id" in new WithInMemoryDB {
      val user = await(UserPostgreDAO.create(Fixtures.userCreate0, "login", "pwd", "someSalt"))
      await {
        UserPostgreDAO.byId(user.id)
      } must beSome
    }

    "retrieve all users" in new WithInMemoryDB {
      await(UserPostgreDAO.create(Fixtures.userCreate0, "login", "pwd", "someSalt"))
      await(UserPostgreDAO.create(Fixtures.userCreate1, "login1", "pwd", "someSalt"))
      val users = await(UserPostgreDAO.all)
      users.length must be equalTo(2)
    }

    "update the user" in new WithInMemoryDB {
      val user = await(UserPostgreDAO.create(Fixtures.userCreate0, "login", "pwd", "someSalt"))
      val newUser = await(UserPostgreDAO.update(user, Fixtures.userUpdate1))
      val readUser = await(UserPostgreDAO.byId(user.id)).get

      readUser.id.value must be equalTo(user.id.value)
      readUser.firstName must be equalTo("firstName1")
      readUser.lastName must be equalTo("lastName1")
      readUser.email must be equalTo("user1@domain.com")
      readUser.creation must be equalTo(user.creation)
    }
  }
}
