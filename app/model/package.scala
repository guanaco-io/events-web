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

import model.Logging.MessageLink
import play.api.libs.json.{Format, Json, OFormat}

/**
  * Created by gertv on 5/23/17.
  */
package object model {

  implicit val relatedToMessage = Json.format[MessageLink]
  implicit val loggingFormat = Json.format[Logging]

  implicit val businessEntity = Json.format[BusinessEntity]
  implicit val businessContext = Json.format[BusinessContext]
  implicit val messageFormat = Json.format[Message]

  implicit val businessMessage = Json.format[BusinessMessage]

}
