package actors.analyzer

import actors.MessageTypeIdentifier.Enrich
import akka.actor.{Actor, ActorLogging, Props}
import be.anova.guanaco.events.MessageEvent.Identification
import be.anova.guanaco.events.{MessageCompletedEvent, MessageEvent, MessageFailedEvent, MessageProcessingEvent}
import model.Message

/**
  * Created by gertv on 6/14/17.
  */
class CamelMessageTimestampAnalyzer() extends Actor with ActorLogging {

  override def receive: Receive = {
    case me: MessageEvent =>
      context.parent ! Enrich(input => {
        log.info(s"Setting timestamp on ${input.id}")
        input.timestamp match {
          case Some(value) if value.isAfter(me.timestamp) =>
            Some(input.copy(timestamp = Some(me.timestamp)))
          case None =>
            Some(input.copy(timestamp = Some(me.timestamp)))
          case _ =>
            None
        }
      })
  }
}

object CamelMessageTimestampAnalyzer {

  def props() = Props(classOf[CamelMessageTimestampAnalyzer])

}


