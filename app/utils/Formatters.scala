package utils

import ybr.playground.views.{ Formatters => PgFormatters }
import ybr.playground.views.Formatter

object Formatters {
  val pg = PgFormatters

  def id() = new Formatter[models.Id] {
    def apply(id: models.Id) = id.value.toString
  }
}
