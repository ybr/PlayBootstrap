package utils

import ybr.playground.views.{ Formatters => PgFormatters }
import ybr.playground.views.Formatter

import models._

object Formatters {
  val pg = PgFormatters

  def id() = new Formatter[Id] {
    def apply(id: Id) = id.value.toString
  }
}
