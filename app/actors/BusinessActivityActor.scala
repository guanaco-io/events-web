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

import actors.BusinessActivityActor.{Entity, Evidence, Process}
import actors.EventsActor.{Subscribe, Unsubscribe}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import io.guanaco.events.messages.{LogEvent, MessageEvent, MessageProcessingEvent}
import model.{BusinessMessage, Logging}
import services.BusinessActivityComponent

import scala.collection.mutable

/**
  * Created by gertv on 5/31/17.
  */
class BusinessActivityActor(id: String) extends Actor with ActorLogging {

  override def receive: Receive = analyze(Seq())

  def analyze(logging: Seq[Logging] = Seq()): Receive = {
    case me: MessageEvent if me.routing.context == "alerta" => {
      log.info("Ignoring CamelContext 'alerta' for BAM")
      context become done()
    }
    case _ => //ignore everything else
  }

  def done(): Receive = {
    case _ => //ignore everything else
  }

}

object BusinessActivityActor {

  def props(id: String) = Props(classOf[BusinessActivityActor], id)

  trait Evidence

  case class Process(name: String) extends Evidence
  case class Entity(name: String) extends Evidence
  case class Status(name: String) extends Evidence

}
