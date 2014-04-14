package utils.credentials

import org.specs2.mutable._

object PasswordHasherSha512ToBase64Spec extends Specification {
  "PasswordHasherSha512ToBase64" should {
    "hash password" in {
      val password = "my!Secret;Password?123§"
      val hashedPassword = PasswordHasherSha512ToBase64.hashPassword(password, "salt")
      hashedPassword must not equalTo(password)
    }

    "hash password with a length of 88" in {
      val hashedPassword = PasswordHasherSha512ToBase64.hashPassword("anyPwd", "salt")
      hashedPassword.length must equalTo(88)
    }

    "hash the same password differently with different salt" in {
      val password = "my!Secret;Password?123§"
      val salt0 = "salt0"
      val salt1 = "salt1"

      val hashed0 = PasswordHasherSha512ToBase64.hashPassword(password, salt0)
      val hashed1 = PasswordHasherSha512ToBase64.hashPassword(password, salt1)

      hashed0 must not equalTo(hashed1)
    }

    "hash the same password with same salt the same way" in {
      val password = "my!Secret;Password?123§"
      val salt = "salt"

      val hashed0 = PasswordHasherSha512ToBase64.hashPassword(password, salt)
      val hashed1 = PasswordHasherSha512ToBase64.hashPassword(password, salt)

      hashed0 must equalTo(hashed1)
    }
  }
}
