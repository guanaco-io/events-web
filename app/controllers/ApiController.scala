package controllers

import java.time.LocalDateTime

import model.Logging
import model.Logging.MessageLink
import play.api.mvc._
import play.api.libs.json.Json
import play.api.mvc.Action

/**
  * Created by gertv on 5/23/17.
  */
class ApiController extends Controller {

  def logging(id: String) = Action { request =>
    println(s"fetching ${id}")
    val logs = Seq(Logging("q&", "info", "test", None), Logging("kl", "debug", "another line", Some(MessageLink("link-to-message"))))
    Ok(Json.toJson(logs))
  }

}
