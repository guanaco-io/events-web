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
import model.{BusinessContext, Message}

/**
  * Created by gertv on 6/19/17.
  */
class ItemJournalActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case Initialize(sender) => {
      sender ! Enrich(DefaultEnricher)
    }
  }

  val DefaultEnricher: Message => Option[Message] = (message) => {
    message.business match {
      case Some(context) if context.description != "Item Journal" =>
        Some(message.copy(business = Some(BusinessContext("message", "Item Journal", context.entity, context.related))))
      case None => {
        Some(message.copy(business = Some(BusinessContext("message", "Item Journal"))))
      }
      case _ => None
    }
  }

}

object ItemJournalActor {

  def props() = Props(classOf[ItemJournalActor])

}
