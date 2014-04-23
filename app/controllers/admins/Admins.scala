package controllers.admins

import scala.concurrent.Future

import org.joda.time._

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import ybr.log._

import models._
import models.requests._
import daos._
import services._

object Admins extends AdminController with Loggable {
  def home() = WithAdmin { implicit request =>
    implicit val postgreIdProvider = new IdProvider[Long] {
      def toId(l: Long) = new Id {
        val value = l.toString
      }
    }

    implicit val admin = Admin(Id(0L), "yohann", "brédoux", "ybr@ybr.fr", DateTime.now)
    Ok(views.html.admins.home())
  }

  def search = TODO

  def default() = Action.async {
    log.info("Existing admin ?")
    for {
      count <- adminService.count
      result <- {
        log.info(s"Found ${count} admin(s)")
        count match {
          case 0 => AdminService.create(AdminCreate("admin", "admin", "admin@domain.com", org.joda.time.DateTime.now), "admin", "changeme") map { _ =>
            Created("Default admin created")
          }
          case _ => Future.successful(Ok(s"Found ${count} admin(s)"))
        }
      }
    } yield result
  }
}
