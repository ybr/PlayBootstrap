package controllers.admins

import scala.concurrent.Future

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import models._
import services._
import utils._

class AdminRequest[A](val admin: Admin, request: Request[A]) extends WrappedRequest[A](request)

trait AdminController { self: Controller =>
  def adminService = AdminService

  implicit def me[A](implicit request: AdminRequest[A]): Admin = request.admin

  object WithAdmin extends ActionBuilder[AdminRequest] {
    def invokeBlock[A](request: Request[A], block: AdminRequest[A] => Future[SimpleResult]) = {
      request.session.get("admin") match {
        case Some(login) => adminService.byLogin(login).map(_.filter(_.active)) flatMap {
          case Some(admin) => block(new AdminRequest(admin, request))
          case None => Future.successful(Forbidden)
        }
        case None => Future.successful(Unauthorized(views.html.admins.signin(Authentication.signinForm)(flash(request), lang(request))))
      }
    }
  }
}
