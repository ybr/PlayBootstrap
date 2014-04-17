package utils

import org.joda.time._

import anorm.SqlParser

import models._

object SqlParsers {
  def id(columnName: String)(implicit provider: IdProvider[Long]) = SqlParser.long(columnName).map(Id(_))

  object joda {
    def date(columnName: String) = SqlParser.date(columnName).map(new DateTime(_))
  }
}
