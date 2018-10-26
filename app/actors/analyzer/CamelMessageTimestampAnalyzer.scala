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

import actors.MessageTypeIdentifier.Enrich
import akka.actor.{Actor, ActorLogging, Props}
import io.guanaco.events.messages.MessageEvent.Identification
import io.guanaco.events.messages.{MessageCompletedEvent, MessageEvent, MessageFailedEvent, MessageProcessingEvent}
import model.Message

/**
  * Created by gertv on 6/14/17.
  */
class CamelMessageTimestampAnalyzer() extends Actor with ActorLogging {

  override def receive: Receive = {
    case me: MessageEvent =>
      context.parent ! Enrich(input => {
        log.info(s"Setting timestamp on ${input.id}")
        input.timestamp match {
          case Some(value) if value.isAfter(me.timestamp) =>
            Some(input.copy(timestamp = Some(me.timestamp)))
          case None =>
            Some(input.copy(timestamp = Some(me.timestamp)))
          case _ =>
            None
        }
      })
  }
}

object CamelMessageTimestampAnalyzer {

  def props() = Props(classOf[CamelMessageTimestampAnalyzer])

}


