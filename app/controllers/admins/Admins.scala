package controllers.admins

import org.joda.time._

import play.api.mvc._

import models._

object Admins extends Controller {
  def home() = Action { implicit request =>
    implicit val postgreIdProvider = new IdProvider[Long] {
      def toId(l: Long) = new Id {
        val value = l.toString
      }
    }

    implicit val admin = Admin(Id(0L), "yohann", "brédoux", "ybr@ybr.fr", DateTime.now)
    Ok(views.html.admins.home())
  }

  def signout = TODO

  def search = TODO
}
