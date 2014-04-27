package daos

import scala.concurrent._

import org.specs2.mutable._

import models.requests._
import models.exceptions._

object AdminPostgreDaoSpec extends Specification with DaoSpec {
  "AdminPostgreDAO" should {
    "create an account" in new WithInMemoryDB {
      val admin = await(AdminPostgreDAO.create(Fixtures.adminCreate0, "login", "pwd", "someSalt"))
      admin.id.value.length > 0 must beTrue
    }

    "not create an account when the login already exists" in new WithInMemoryDB {
      await {
        AdminPostgreDAO.create(Fixtures.adminCreate0, "login",  "pwd", "someSalt")
      }
      await {
        AdminPostgreDAO.create(Fixtures.adminCreate1,"login",  "otherPwd", "someOtherSalt")
      } must throwAn[AccountAlreadyExistsException]
    }

    "retrieve salt" in new WithInMemoryDB {
      await(AdminPostgreDAO.create(Fixtures.adminCreate0, "login", "pwd", "someSalt"))
      await {
        AdminPostgreDAO.salt("login")
      }.get must be equalTo("someSalt")
    }

    "retrieve admin by login" in new WithInMemoryDB {
      await(AdminPostgreDAO.create(Fixtures.adminCreate0, "login", "pwd", "someSalt"))
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
      await(AdminPostgreDAO.create(Fixtures.adminCreate0, "login", "pwd", "someSalt"))
      await {
        AdminPostgreDAO.authenticate("login", "pwd")
      } must beSome
    }

    "retrieve by id" in new WithInMemoryDB {
      val admin = await(AdminPostgreDAO.create(Fixtures.adminCreate0, "login", "pwd", "someSalt"))
      await {
        AdminPostgreDAO.byId(admin.id)
      } must beSome
    }

    "retrieve all admins" in new WithInMemoryDB {
      await(AdminPostgreDAO.create(Fixtures.adminCreate0, "login", "pwd", "someSalt"))
      await(AdminPostgreDAO.create(Fixtures.adminCreate1, "login1", "pwd", "someSalt"))
      val admins = await(AdminPostgreDAO.all)
      admins.length must be equalTo(2)
    }

    "update an admin" in new WithInMemoryDB {
      val user = await(AdminPostgreDAO.create(Fixtures.adminCreate0, "login", "pwd", "someSalt"))
      await(AdminPostgreDAO.update(user, Fixtures.adminUpdate1))
      val readUser = await(AdminPostgreDAO.byId(user.id)).get

      readUser.id.value must be equalTo(user.id.value)
      readUser.firstName must be equalTo("firstName1")
      readUser.lastName must be equalTo("lastName1")
      readUser.email must be equalTo("admin1@domain.com")
      readUser.creation must be equalTo(user.creation)
    }
  }
}
