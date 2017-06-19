package actors.analyzer

import actors.MessageTypeIdentifier.{Enrich, Initialize}
import akka.actor.{Actor, ActorLogging, Props}
import model.{BusinessContext, Message}

/**
  * Created by gertv on 6/14/17.
  */
class ScheduleActor(mode: String, value: String) extends Actor with ActorLogging {

  override def receive: Receive = {
    case Initialize(sender) => {
      sender ! Enrich(Initialization)
    }
  }

  val Initialization: Message => Option[Message] = (input: Message) => {
    val context = BusinessContext("scheduled", s"${mode} updates - ${value}s")
    Some(input.copy(business = Some(context)))
  }
}

object ScheduleActor {

  def props(value: String, mode: String) = Props(classOf[ScheduleActor], mode, value)
  def incremental(value: String): Props = props(value, "Incremental")
  def full(value: String): Props = props(value, "Full")

}
