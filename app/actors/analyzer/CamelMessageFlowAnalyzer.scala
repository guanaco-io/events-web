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
import io.guanaco.events.messages.{MessageCompletedEvent, MessageEvent, MessageFailedEvent, MessageProcessingEvent}
import io.guanaco.events.messages.MessageEvent.Identification
import model.Message

/**
  * Created by gertv on 6/14/17.
  */
class CamelMessageFlowAnalyzer(breadcrumb: String) extends Actor with ActorLogging {


  override def postStop(): Unit =
    log.info(s"Stop tracking ${breadcrumb}")

  override def receive: Receive = processing(Set())


  def processing(pending: Set[String]): Receive = {
    log.info(s"Processing ${breadcrumb} - ${pending.mkString(",")} pending")
    context.parent ! Enrich(updateStatus("processing"))

    {
      case MessageProcessingEvent(_, Identification(id, _), _, _, _, _) =>
        if (!pending.contains(id)) {
          context become processing(pending + id)
        }
      case MessageCompletedEvent(_, Identification(id, _), _, _, _, _) =>
        val remaining = pending - id
        if (remaining.isEmpty) {
          context become done()
        } else {
          context become processing(remaining)
        }
      case MessageFailedEvent(_, Identification(id, _), _, _, _, _) =>
        val remaining = pending - id
        if (remaining.isEmpty) {
          context become failed()
        } else {
          context become failed(remaining)
        }
      case _ => // ignore everything else
    }
  }

  def failed(pending: Set[String] = Set()): Receive = {
    log.info(s"Marking ${breadcrumb} as failed - ${pending.mkString(",")} pending")
    context.parent ! Enrich(updateStatus("failed"))

    {
      case MessageProcessingEvent(_, Identification(id, _), _, _, _, _) =>
        if (!pending.contains(id)) {
          context become failed(pending + id)
        }
      case MessageFailedEvent(_, Identification(id, _), _, _, _, _) =>
        context become failed(pending)
    }
  }

  def done(): Receive = {
    log.info(s"Marking ${breadcrumb} as done")
    context.parent ! Enrich(updateStatus("done"))

    {
      case MessageProcessingEvent(_, Identification(id, _), _, _, _, _) =>
        context become processing(Set(id))
      case MessageFailedEvent(_, Identification(id, _), _, _, _, _) =>
        context become failed()
    }
  }

  def updateStatus(status: String): Message => Option[Message] = (message: Message) => {
    log.info(s"Updating status of ${message.id} to ${status}")
    (message.status, status) match {
      case ("done", "processing") => None
      case ("done", _) => Some(message.copy(status = status))
      case ("failed", _) => None
      case (previous, next) if previous == next => None
      case (_, _) => Some(message.copy(status = status))
    }
  }
}

object CamelMessageFlowAnalyzer {

  def props(breadcrumb: String) = Props(classOf[CamelMessageFlowAnalyzer], breadcrumb)

}
