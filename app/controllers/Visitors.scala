package controllers

import play.api._
import play.api.mvc._

import utils._

object Visitors extends Controller {
  def home = Action(Ok(views.html.users.home()))
}
