/*
 * Copyright 2018 - anova r&d bvba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
