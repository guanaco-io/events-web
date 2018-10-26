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

package actors

import java.time.ZonedDateTime
import javax.inject.{Inject, Named}

import actors.EventsActor.{Subscribe, Unsubscribe}
import actors.CamelMessagesActor._
import akka.actor.{Actor, ActorLogging, ActorRef}
import io.guanaco.events.messages.{Event, LogEvent, MessageEvent}
import model.Message
import services.MessageComponent

/**
  * Created by gertv on 5/27/17.
  */
class CamelMessagesActor @Inject()(@Named("events") events: ActorRef, service: MessageComponent) extends Actor with ActorLogging {

  val identifier = context.actorOf(MessageTypeIdentifier.props(), "identifier")

  override def preStart(): Unit = events ! Subscribe(EventsActor.AllEvents)

  override def postStop(): Unit = events ! Unsubscribe()

  override def receive: Receive = tracking(Map.empty[String, ActorRef], Set.empty[ActorRef])

  def tracking(pending: Map[String, ActorRef], subscriptions: Set[ActorRef]): Receive = {

    {
      case me: MessageEvent => {
        val id = me.id.breadcrumb getOrElse me.id.id
        pending.get(id) match {
          case Some(target) => target ! me
          case None => {
            val target = context.actorOf(CamelMessageActor.props(id, identifier), id)
            target ! me
            context become tracking(pending + (id -> target), subscriptions)
          }
        }
      }
      case le: LogEvent => {
        val id = (le.mdc.get("camel.breadcrumbId") orElse le.mdc.get("camel.exchangeId")) map(_.asInstanceOf[String])

        id foreach { id: String => pending.get(id) match {
          case Some(target) => target ! le
          case None => {
            val target = context.actorOf(CamelMessageActor.props(id, identifier), id)
            target ! le
            context become tracking(pending + (id -> target), subscriptions)
          }
        }}
      }
      case MessageState(id, message) => {
        subscriptions foreach { subscription =>
          subscription ! MessageState(id, message)
        }
        log.info(s"Storing ${message}")
        service.store(message)
        context become tracking(pending, subscriptions)
      }
      case CamelMessagesActor.Subscribe() => {
        context become tracking(pending, subscriptions + sender)
      }
      case CamelMessagesActor.Unsubscribe() => {
        context become tracking(pending, subscriptions - sender)
      }
    }
  }

}

object CamelMessagesActor {

  case class MessageState(id: String, message: Message)

  // subscribe/unsubscribe to message state updates
  case class Subscribe()
  case class Unsubscribe()

}