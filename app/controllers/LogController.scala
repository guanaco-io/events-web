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

package controllers

import javax.inject.{Inject, Named}

import actors.EventsActor
import actors.EventsActor.{Subscribe, Unsubscribe}
import actors.LoggingActor.{RecentLogging, RecentLoggingRequest}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import model.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, _}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import io.guanaco.events.messages.LogEvent
import play.api.libs.streams.ActorFlow

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by gertv on 5/23/17.
  */
class LogController @Inject() (@Named("logging") logging: ActorRef, @Named("events") events: ActorRef)(implicit ec: ExecutionContext, system: ActorSystem) extends Controller {

  implicit val materializer = ActorMaterializer()

  implicit val timeout: Timeout = 5 seconds

  def recent() = Action.async { request =>
    (logging ? RecentLoggingRequest()).mapTo[RecentLogging] map { result =>
      result match {
        case RecentLogging(logs) => Ok(Json.toJson(logs))
      }
    }
  }

  def updates() = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => LogUpdatesActor.props(events, out))
  }



}

class LogUpdatesActor(events: ActorRef, out: ActorRef) extends Actor {

  override def preStart(): Unit = events ! Subscribe(EventsActor.LogEvents)

  override def postStop(): Unit = events ! Unsubscribe()

  override def receive: Receive = {
    case event: LogEvent =>
      import actors.LoggingActor._
      out ! Json.toJson(toLogging(event)).toString()
  }
}


object LogUpdatesActor {

  def props(events: ActorRef, out: ActorRef) = Props(classOf[LogUpdatesActor], events, out)

}


