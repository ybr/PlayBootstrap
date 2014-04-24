package utils

import org.joda.time._

import anorm.SqlParser

import ybr.playground.db.sql.{ SqlParsers => PgSqlParsers }

import models._

object SqlParsers {
  val pg = PgSqlParsers

  def id(columnName: String)(implicit provider: IdProvider[Long]) = SqlParser.long(columnName).map(Id(_))
}
