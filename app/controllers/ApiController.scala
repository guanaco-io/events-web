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
