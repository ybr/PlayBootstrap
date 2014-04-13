package daos

import scala.concurrent._

import org.specs2.mutable._

import models.exceptions._

object CredentialsPostgreDaoSpec extends Specification with DaoSpec {
  "CredentialsPostgreDAO" should {
    "create an account" in new WithInMemoryDB {
      val login = await(CredentialsPostgreDAO.create("user@domain.com", "pwd", "someSalt"))
      login must equalTo("user@domain.com")
    }

    "not create an account when the login already exists" in new WithInMemoryDB {
      await {
        CredentialsPostgreDAO.create("user@domain.com", "pwd", "someSalt")
      }
      await {
        CredentialsPostgreDAO.create("user@domain.com", "otherPwd", "someOtherSalt")
      } must throwAn[AccountAlreadyExistsException]
    }
  }
}
