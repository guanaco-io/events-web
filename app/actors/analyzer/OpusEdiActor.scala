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

import actors.MessageTypeIdentifier.{Enrich, Initialize}
import akka.actor.{Actor, ActorLogging, Props}
import io.guanaco.events.messages.MessageProcessingEvent
import model.{BusinessContext, BusinessEntity, Message}

/**
  * Created by gertv on 6/14/17.
  */
class OpusEdiActor() extends Actor with ActorLogging {

  override def receive: Receive = {
    case Initialize(sender) => {
      sender ! Enrich(Initialization)
    }
    case MessageProcessingEvent(_, _, _, headers, _, _) => {
      headers.get("CamelFileName") foreach { file =>
        val entity = BusinessEntity("File", file.toString)
        sender ! Enrich(input => {
          val context = input.business getOrElse BusinessContext("message", s"EDI -> XML -> Normalized XML")
          Some(input.copy(business = Some(context.copy(entity = Some(entity)))))
        })
      }
    }
  }

  val Initialization: Message => Option[Message] = (input: Message) => {
    val context = BusinessContext("message", s"EDI -> XML -> Normalized XML")
    Some(input.copy(business = Some(context)))
  }
}

object OpusEdiActor {

  def props() = Props(classOf[OpusEdiActor])

}


