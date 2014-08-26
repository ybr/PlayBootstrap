package utils.credentials

import java.security.MessageDigest

import org.apache.commons.codec.binary.Base64

import playground.models._

trait PasswordHasher {
  def hashPassword(password: Password, salt: String): String
}

object PasswordHasherSha512ToBase64 {
  private val digester = MessageDigest.getInstance("SHA-512")

  def hashPassword(password: Password, salt: String): String = {
    val bytes = digester.digest(password.neverLog(_ + salt).getBytes)
    Base64.encodeBase64String(bytes)
  }
}
