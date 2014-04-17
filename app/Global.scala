import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.mvc.Results._

import models._

object Global extends GlobalSettings {
  override def onError(req: RequestHeader, x: Throwable) = {
    Future.successful(InternalServerError(views.html.visitors.error()(None, Flash())))
  }
}
