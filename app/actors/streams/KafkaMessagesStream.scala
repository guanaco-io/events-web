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

package actors.streams

import akka.NotUsed
import akka.actor.{ActorLogging, ActorRef, ActorSystem}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import io.guanaco.events.messages.{LogEvent, MessageEvent}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.Configuration

/**
  * Created by gertv on 5/25/17.
  */
class KafkaMessagesStream(config: Configuration, implicit val system: ActorSystem, val dest: ActorRef) {

  val bootstrapServer = config.getString("bootstrapServer") getOrElse (throw new IllegalStateException("bootstrapServer config is missing"))

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrapServer)
    .withGroupId("test1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  implicit val materializer = ActorMaterializer()

  import io.guanaco.events.messages.Events._
  import spray.json._

  val done =
    Consumer.plainSource(consumerSettings, Subscriptions.topics("messages"))
      .map { message =>
        try {
          val json = message.value().parseJson
          println(message)
          Some(json.convertTo[MessageEvent])
        } catch {
          case e: Exception => None
        }
      }
      .filter(_.isDefined)
      .map(_.get )
      .runWith(Sink.actorRef(dest, NotUsed))


}
