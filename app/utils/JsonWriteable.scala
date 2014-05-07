package utils

import play.api.http.Writeable
import play.api.libs.json._

// TODO move to playground
trait JsonWriteable {
  implicit def writeableFromJsonWrites[A](implicit write: Writes[A]): Writeable[A] = Writeable[A]({ (a: A) =>
    Writeable.writeableOf_JsValue.transform(write.writes(a))
  }, Some("application/json"))
}

