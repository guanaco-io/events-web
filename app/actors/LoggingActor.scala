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

import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Named}

import actors.EventsActor.{Subscribe, Unsubscribe}
import actors.LoggingActor.{RecentLogging, RecentLoggingRequest}
import akka.actor.{Actor, ActorRef}
import io.guanaco.events.messages.LogEvent
import model.Logging
import model.Logging.MessageLink

import scala.collection.mutable

/**
  * Created by gertv on 5/25/17.
  */
class LoggingActor @Inject() (@Named("events") events: ActorRef) extends Actor {

  import LoggingActor._

  val buffer = mutable.Queue.empty[Logging]


  override def preStart(): Unit = {
    events ! Subscribe(EventsActor.LogEvents)
  }


  override def postStop(): Unit = {
    events ! Unsubscribe()
  }

  override def receive: Receive = {
    case event: LogEvent => {
      buffer enqueue toLogging(event)
      while (buffer.size > 1000) {
        buffer.dequeue()
      }
    }
    case RecentLoggingRequest() => {
      sender ! RecentLogging( buffer.reverse)
    }
}

}

object LoggingActor {

  case class RecentLoggingRequest()
  case class RecentLogging(events: Seq[Logging])

  def toLogging(event: LogEvent): Logging = {
    val link = event.mdc.get("camel.breadcrumbId") map { id => MessageLink(id.toString) }
    Logging(event.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), event.level, event.message, link)
  }

}
