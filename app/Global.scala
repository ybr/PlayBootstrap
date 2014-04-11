import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.mvc.Results._

object Global extends GlobalSettings {
  override def onError(req: RequestHeader, x: Throwable) = {
    implicit val flash = Flash()
    Future.successful(InternalServerError(views.html.users.error()))
  }
}
