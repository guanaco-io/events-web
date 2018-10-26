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

package model
import java.time.ZonedDateTime

/**
  * Created by gertv on 5/27/17.
  */
case class Message(id: String, timestamp: Option[ZonedDateTime], logging: Seq[Logging], status: String, business: Option[BusinessContext] = None) {

}

case class BusinessContext(processType: String, description: String, entity: Option[BusinessEntity] = None, related: Seq[BusinessEntity] = Seq())

case class BusinessEntity(name: String, id: String)


