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
