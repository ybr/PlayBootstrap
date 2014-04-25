package utils

import play.api.data.Mapping

import models._

object Mappings {
  implicit class PasswordMapping(mapping: Mapping[String]) {
    def password = mapping.transform[Password](Password.apply, _.neverLog(identity))
  }
}
