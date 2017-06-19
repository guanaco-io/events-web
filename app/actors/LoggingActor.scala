package actors

import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Named}

import actors.EventsActor.{Subscribe, Unsubscribe}
import actors.LoggingActor.{RecentLogging, RecentLoggingRequest}
import akka.actor.{Actor, ActorRef}
import be.anova.guanaco.events.LogEvent
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
