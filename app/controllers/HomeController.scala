package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def logging = Action { implicit request =>
    Ok(views.html.logging())
  }

  def messages() = Action { implicit request =>
    Ok(views.html.messages())
  }

  def message(id: String) = Action { implicit request =>
    Ok(views.html.message(id))
  }

  def activities() = Action { implicit request =>
    Ok(views.html.activities())
  }
}
