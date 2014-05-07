package utils.credentials

import scala.util.Random

import org.apache.commons.codec.binary.Base64

import models.Password

trait PasswordGenerator {
  def generate(): Password
}

object RandomPassword extends PasswordGenerator {
  def generate = {
    val bytes = new Array[Byte](9)
    Random.nextBytes(bytes)
    Password(Base64.encodeBase64String(bytes))
  }
}
