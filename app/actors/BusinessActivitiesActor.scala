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

import javax.inject.{Inject, Named}

import actors.EventsActor.{Subscribe, Unsubscribe}
import akka.actor.{Actor, ActorRef}
import io.guanaco.events.messages.{LogEvent, MessageEvent}
import model.BusinessMessage
import services.{BusinessActivityComponent, MessageComponent}

/**
  * Created by gertv on 5/31/17.
  */
class BusinessActivitiesActor @Inject()(@Named("events") events: ActorRef, service: BusinessActivityComponent)  extends Actor {

  override def preStart(): Unit = events ! Subscribe(EventsActor.AllEvents)

  override def postStop(): Unit = events ! Unsubscribe()

  override def receive: Receive = track(Map.empty[String, ActorRef])

  def track(activities: Map[String, ActorRef]): Receive = {
    def dispatch(id: String, message: AnyRef) = activities.get(id) match {
      case Some(ref) => ref ! message
      case None => {
        val ref = context.actorOf(BusinessActivityActor.props(id))
        ref ! message
        context become track(activities + (id -> ref))
      }
    }

    {
      case me: MessageEvent => {
        val id = me.id.breadcrumb getOrElse me.id.id
        dispatch(id, me)
      }
      case le: LogEvent => {
        val id = (le.mdc.get("camel.breadcrumbId") orElse le.mdc.get("camel.exchangeId")) map(_.asInstanceOf[String])
        id foreach { id => dispatch(id, le) }
      }
      case result: BusinessMessage => {
        service.store(result)
      }
    }
  }
}
