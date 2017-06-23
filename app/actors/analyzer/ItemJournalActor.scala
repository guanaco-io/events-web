package actors.analyzer

import actors.MessageTypeIdentifier.{Enrich, Initialize}
import akka.actor.{Actor, ActorLogging, Props}
import model.{BusinessContext, Message}

/**
  * Created by gertv on 6/19/17.
  */
class ItemJournalActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case Initialize(sender) => {
      sender ! Enrich(DefaultEnricher)
    }
  }

  val DefaultEnricher: Message => Option[Message] = (message) => {
    message.business match {
      case Some(context) if context.description != "Item Journal" =>
        Some(message.copy(business = Some(BusinessContext("message", "Item Journal", context.entity, context.related))))
      case None => {
        Some(message.copy(business = Some(BusinessContext("message", "Item Journal"))))
      }
      case _ => None
    }
  }

}

object ItemJournalActor {

  def props() = Props(classOf[ItemJournalActor])

}
