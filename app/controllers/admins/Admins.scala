package controllers.admins

import scala.concurrent.Future

import org.joda.time._

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._
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
      Ok(views.html.admins.adminsTable(admins))
    }
  }

  val activeForm = Form(single(
    "active" -> boolean
  ))

  def details(id: Id) = WithAdmin.async { implicit request =>
    adminService.byId(id).map {
      case Some(admin) => Ok(views.html.admins.adminDetails(activeForm.fill(admin.active), admin))
      case None => NotFound(views.html.admins.notfound("The admin can not be found"))
    }
  }

  // TODO eventually use a monad transformer
  def update(id: Id) = WithAdmin.async { implicit request =>
    adminService.byId(id) flatMap {
      case Some(admin) => activeForm.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest(views.html.admins.adminDetails(formWithErrors, admin))),
        active => adminService.update(admin, AdminUpdate(admin).copy(active = active)) map {
          case Some(updatedAdmin) => Redirect(controllers.admins.routes.Admins.all).flashing("success" -> Messages("flash.admin.admins.update", updatedAdmin.firstName, updatedAdmin.lastName))
          case None => NotFound(views.html.admins.notfound("The admin can not be found"))
        }
      )
      case None => Future.successful(NotFound(views.html.admins.notfound("The admin can not be found")))
    }
  }

  def default() = Action.async {
    log.info("Existing admin ?")
    for {
      count <- adminService.all.map(_.length)
      result <- {
        log.info(s"Found ${count} admin(s)")
        count match {
          case 0 => adminService.create(AdminCreate("admin", "admin", "admin@domain.com", true, DateTime.now), "admin", Password("changeme")) map { _ =>
            Created("Default admin created")
          }
          case _ => Future.successful(Ok(s"Found ${count} admin(s)"))
        }
      }
    } yield result
  }
}
