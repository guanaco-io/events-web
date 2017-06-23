package actors.analyzer

import actors.MessageTypeIdentifier.{Enrich, Initialize}
import akka.actor.{Actor, ActorLogging, Props}
import model.BusinessContext

/**
  * Created by gertv on 6/17/17.
  */
class UnidentiedMessageActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case Initialize(ref) => {
      ref ! Enrich(message => message.business match {
        case None => Some(message.copy(business = Some(BusinessContext("message", "Unidentif²ied message"))))
        case Some(_) => None
      })
    }
  }
}

object UnidentiedMessageActor {

  def props() = Props(classOf[UnidentiedMessageActor])

}
