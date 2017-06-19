package actors.analyzer

import java.time.format.DateTimeFormatter

import actors.MessageTypeIdentifier.Enrich
import akka.actor.{Actor, Props}
import be.anova.guanaco.events.LogEvent
import model.{Logging, Message}
import model.Logging.MessageLink

/**
  * Created by gertv on 6/15/17.
  */
class   CamelLogMessageConverter extends Actor {

  import CamelLogMessageConverter.toLogging

  override def receive: Receive = {
    case log: LogEvent =>
      sender ! enrichWith(log)
  }

  def enrichWith(event: LogEvent): Enrich =
    Enrich(addLogging(toLogging(event)))

  def addLogging(logging:Logging): Message => Option[Message] =
    (input: Message) =>
      Some(input.copy(logging = input.logging :+ logging))


}

object CamelLogMessageConverter {

  def props() = Props(classOf[CamelLogMessageConverter])

  def toLogging(event: LogEvent): Logging = {
    Logging(event.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), event.level, event.message, None)
  }

}
