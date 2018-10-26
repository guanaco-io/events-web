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

package actors.analyzer

import java.time.format.DateTimeFormatter

import actors.MessageTypeIdentifier.Enrich
import akka.actor.{Actor, Props}
import io.guanaco.events.messages.LogEvent
import model.{Logging, Message}
import model.Logging.MessageLink

/**
  * Created by gertv on 6/15/17.
  */
class   CamelLogMessageConverter extends Actor {

  import CamelLogMessageConverter.toLogging

  override def receive: Receive = {
    case log: LogEvent =>
      sender ! enrichWith(log)
  }

  def enrichWith(event: LogEvent): Enrich =
    Enrich(addLogging(toLogging(event)))

  def addLogging(logging:Logging): Message => Option[Message] =
    (input: Message) =>
      Some(input.copy(logging = input.logging :+ logging))


}

object CamelLogMessageConverter {

  def props() = Props(classOf[CamelLogMessageConverter])

  def toLogging(event: LogEvent): Logging = {
    Logging(event.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), event.level, event.message, None)
  }

}
