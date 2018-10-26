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

import actors.streams.{KafkaLoggingStream, KafkaMessagesStream}
import akka.actor.{Actor, ActorLogging, ActorRef}
import io.guanaco.events.messages.{Event, LogEvent, MessageEvent}
import com.google.inject.Inject
import play.api.Configuration

/**
  * Created by gertv on 5/24/17.
  */
class EventsActor @Inject()(configuration: Configuration) extends Actor with ActorLogging {

  import EventsActor._

  override def preStart(): Unit = {
    val config = configuration.getConfig("guanaco") getOrElse missingConfiguration()
    config.getConfig("events.sources") map { sourcesConfig =>
      sourcesConfig.getConfig("kafka") map { kafkaConfig =>
        log.info("Initializing Kafka event stream")
        new KafkaLoggingStream(kafkaConfig, context.system, self)
        new KafkaMessagesStream(kafkaConfig, context.system, self)
      }
    }
  }

  override def receive: Receive = dispatching(Map.empty[ActorRef, SubscriptionFilter])

  def dispatching(filters: Map[ActorRef, SubscriptionFilter]): Receive = {
    case Subscribe(filter) => {
      log.info(s"Adding listener ${sender()}")
      context become(dispatching(filters + (sender() -> filter)))
    }
    case Unsubscribe() => {
      log.info(s"Removing listener ${sender()}")
      context become dispatching(filters.filterKeys(actor => actor != sender()))
    }
    case event: Event => {
      println(s"Dispatching ${event}")
      for {(actor, filter) <- filters}
        if (filter(event)) actor ! event
    }
    case value => throw new IllegalArgumentException(s"${value} of type ${value.getClass} is not supported")
  }
}

object EventsActor {

  class MissingConfigurationException extends RuntimeException("Guanaco configuration is missing")

  def missingConfiguration(): Nothing = throw new MissingConfigurationException

  type SubscriptionFilter = Event => Boolean

  val LogEvents: SubscriptionFilter = (event: Event) => event.isInstanceOf[LogEvent]
  val MessageEvents: SubscriptionFilter = (event: Event) => event.isInstanceOf[MessageEvent]
  val AllEvents: SubscriptionFilter = (event: Event) => true

  case class Subscribe(filter: SubscriptionFilter)
  case class Unsubscribe()

}
