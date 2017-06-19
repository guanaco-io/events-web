package actors

import javax.inject.{Inject, Named}

import actors.EventsActor.{Subscribe, Unsubscribe}
import akka.actor.{Actor, ActorRef}
import be.anova.guanaco.events.{LogEvent, MessageEvent}
import model.BusinessMessage
import services.{BusinessActivityComponent, MessageComponent}

/**
  * Created by gertv on 5/31/17.
  */
class BusinessActivitiesActor @Inject()(@Named("events") events: ActorRef, service: BusinessActivityComponent)  extends Actor {

  override def preStart(): Unit = events ! Subscribe(EventsActor.AllEvents)

  override def postStop(): Unit = events ! Unsubscribe()

  override def receive: Receive = track(Map.empty[String, ActorRef])

  def track(activities: Map[String, ActorRef]): Receive = {
    def dispatch(id: String, message: AnyRef) = activities.get(id) match {
      case Some(ref) => ref ! message
      case None => {
        val ref = context.actorOf(BusinessActivityActor.props(id))
        ref ! message
        context become track(activities + (id -> ref))
      }
    }

    {
      case me: MessageEvent => {
        val id = me.id.breadcrumb getOrElse me.id.id
        dispatch(id, me)
      }
      case le: LogEvent => {
        val id = (le.mdc.get("camel.breadcrumbId") orElse le.mdc.get("camel.exchangeId")) map(_.asInstanceOf[String])
        id foreach { id => dispatch(id, le) }
      }
      case result: BusinessMessage => {
        service.store(result)
      }
    }
  }
}
