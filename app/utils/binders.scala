package utils

import play.api.mvc.{ QueryStringBindable, PathBindable }

import models._

package object binders {
  implicit val bindersIdProvider = new IdProvider[Long] {
    def toId(l: Long) = new Id {
      val value = l.toString
    }
  }

  implicit object queryStringBindableId extends QueryStringBindable.Parsing[Id](
    s => Id(java.lang.Long.parseLong(s)), _.value.toString, (key: String, e: Exception) => s"Cannot parse parameter $key as Id: $e"
  )

  implicit object pathBindableId extends PathBindable.Parsing[Id](
    s => Id(java.lang.Long.parseLong(s)), _.value.toString, (key: String, e: Exception) => s"Cannot parse parameter $key as Id: $e"
  )

}
