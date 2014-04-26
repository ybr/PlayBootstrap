package utils

import ybr.playground.views.{Formattable => PgFormattable}
import ybr.playground.views.Formatter

import models._

object Formattable {
  val pg = PgFormattable

  implicit class IdFormattable(id: Id) {
    def |(formatter: Formatter[Id]) = formatter(id)
  }
}
