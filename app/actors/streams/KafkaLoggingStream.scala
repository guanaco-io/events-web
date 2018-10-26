package actors.streams

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import io.guanaco.events.messages.LogEvent
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import play.api.Configuration

/**
  * Created by gertv on 5/25/17.
  */
class KafkaLoggingStream(config: Configuration, implicit val system: ActorSystem, val dest: ActorRef) {

  val bootstrapServer = config.getString("bootstrapServer") getOrElse (throw new IllegalStateException("bootstrapServer config is missing"))

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrapServer)
    .withGroupId("test1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  implicit val materializer = ActorMaterializer()

  import io.guanaco.events.messages.Events._
  import spray.json._

  val done =
    Consumer.plainSource(consumerSettings, Subscriptions.topics("logging"))
      .map { record: ConsumerRecord[String, String] =>
        try {
          val json = record.value().parseJson
          Some(json.convertTo[LogEvent])
        } catch {
          case e: Exception => None
        }
      }
      .filter(_.isDefined)
      .map(_.get)
      .runWith(Sink.actorRef(dest, NotUsed))


}
