package utils.credentials

trait SaltGenerator {
  def generateSalt(): String
}

object SaltGeneratorUUID extends SaltGenerator {
  def generateSalt = java.util.UUID.randomUUID.toString
}
