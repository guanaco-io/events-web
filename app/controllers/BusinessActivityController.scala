package controllers

import java.time.ZonedDateTime
import javax.inject.Inject

import model.{BusinessMessage, Logging}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.BusinessActivityComponent

/**
  * Created by gertv on 5/31/17.
  */
class BusinessActivityController @Inject() (val service: BusinessActivityComponent) extends Controller {

  def recent() = Action { request =>
    Ok(Json.toJson(Seq(BusinessMessage(ZonedDateTime.now(), "uuid-1", "Business message 1", Seq(), Some("source"), Some("sink"), "success"))))
  }

}
