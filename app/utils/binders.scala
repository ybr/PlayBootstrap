package utils

import play.api.mvc.{ QueryStringBindable, PathBindable }

import playground.models._

package object binders {
  implicit val bindersIdProvider = new IdProvider[String] {
    def toId(s: String) = new Id {
      val value = s
    }
  }

  implicit object queryStringBindableId extends QueryStringBindable.Parsing[Id](
    s => Id(s), _.value.toString, (key: String, e: Exception) => s"Cannot parse parameter $key as Id: $e"
  )

  implicit object pathBindableId extends PathBindable.Parsing[Id](
    s => Id(s), _.value.toString, (key: String, e: Exception) => s"Cannot parse parameter $key as Id: $e"
  )

}
