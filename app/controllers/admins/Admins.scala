package controllers.admins

import scala.concurrent.Future

import org.joda.time._

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import ybr.playground.log._

import models._
import models.requests._
import daos._
import services._

object Admins extends AdminController with Logger {
  def home() = WithAdmin { implicit request =>
    Ok(views.html.admins.home())
  }

  def all = WithAdmin.async { implicit request =>
    adminService.all map { admins =>
      Ok(views.html.admins.admins(admins))
    }
  }

  def details(id: Id) = TODO

  def default() = Action.async {
    log.info("Existing admin ?")
    for {
      count <- adminService.all.map(_.length)
      result <- {
        log.info(s"Found ${count} admin(s)")
        count match {
          case 0 => adminService.create(AdminCreate("admin", "admin", "admin@domain.com", org.joda.time.DateTime.now), "admin", "changeme") map { _ =>
            Created("Default admin created")
          }
          case _ => Future.successful(Ok(s"Found ${count} admin(s)"))
        }
      }
    } yield result
  }
}
