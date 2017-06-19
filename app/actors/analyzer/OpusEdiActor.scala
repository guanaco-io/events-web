package actors.analyzer

import actors.MessageTypeIdentifier.{Enrich, Initialize}
import akka.actor.{Actor, ActorLogging, Props}
import be.anova.guanaco.events.MessageProcessingEvent
import model.{BusinessContext, BusinessEntity, Message}

/**
  * Created by gertv on 6/14/17.
  */
class OpusEdiActor() extends Actor with ActorLogging {

  override def receive: Receive = {
    case Initialize(sender) => {
      sender ! Enrich(Initialization)
    }
    case MessageProcessingEvent(_, _, _, headers, _, _) => {
      headers.get("CamelFileName") foreach { file =>
        val entity = BusinessEntity("File", file.toString)
        sender ! Enrich(input => {
          val context = input.business getOrElse BusinessContext("message", s"EDI -> XML -> Normalized XML")
          Some(input.copy(business = Some(context.copy(entity = Some(entity)))))
        })
      }
    }
  }

  val Initialization: Message => Option[Message] = (input: Message) => {
    val context = BusinessContext("message", s"EDI -> XML -> Normalized XML")
    Some(input.copy(business = Some(context)))
  }
}

object OpusEdiActor {

  def props() = Props(classOf[OpusEdiActor])

}


