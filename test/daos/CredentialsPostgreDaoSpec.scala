package daos

import scala.concurrent._

import org.specs2.mutable._

import models.exceptions._

object CredentialsPostgreDaoSpec extends Specification with DaoSpec {
  "CredentialsPostgreDAO" should {
    "create an account" in new WithInMemoryDB {
      val id = await(CredentialsPostgreDAO.create("user@domain.com", "pwd", "someSalt").commit)
      id.value > 0 must beTrue
    }

    "not create an account when the login already exists" in new WithInMemoryDB {
      await {
        CredentialsPostgreDAO.create("user@domain.com", "pwd", "someSalt").commit
      }
      await {
        CredentialsPostgreDAO.create("user@domain.com", "otherPwd", "someOtherSalt").commit
      } must throwAn[AccountAlreadyExistsException]
    }
  }
}
