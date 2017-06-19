package actors

import actors.CamelMessagesActor.{MessageState}
import actors.analyzer.{CamelLogMessageConverter, CamelMessageFlowAnalyzer, CamelMessageTimestampAnalyzer}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.server.RouteResult.Complete
import be.anova.guanaco.events.MessageEvent.Body
import be.anova.guanaco.events._
import model.{BusinessContext, Message}

/**
  * Created by gertv on 5/27/17.
  */
class CamelMessageActor (val id: String, identifier: ActorRef) extends Actor with ActorLogging {

  import LoggingActor.toLogging
  import MessageTypeIdentifier._

  val status = context.actorOf(CamelMessageFlowAnalyzer.props(id), "status")
  val logging = context.actorOf(CamelLogMessageConverter.props(), "logging")
  val timestamp = context.actorOf(CamelMessageTimestampAnalyzer.props(), "timestamp")

  override def receive: Receive = analyzing(Message(id, None, Seq(), "processing"), Seq(), Set(status, logging, timestamp, identifier), noop)

  def analyzing(message: Message, events: Seq[Event], analyzers: Set[ActorRef], publisher: Message => Boolean): Receive = {
    log.info(s"Analyzing ${id} with ${analyzers.size} analyzers and ${events.size} cached events")

    {
      case event: Event => {
        analyzers foreach { analyzer => analyzer ! event}
        context become analyzing(message, event +: events, analyzers, publisher)
      }
      case Analyzer(ref, replaces) => {
        events foreach { event => ref ! event }
        val next = replaces match {
          case Some(replace) => {
            log.info(s"${ref} replaces ${replace}")
            analyzers - replace + ref
          }
          case None => analyzers + ref
        }
        context become analyzing(message, events, next, publisher)
      }
      case Enrich(enricher) => {
        enricher(message) map { enriched =>
          log.info(s"Message ${id} enriched by ${sender   }")
          if (publisher(enriched)) {
            context.parent ! MessageState(id, enriched)
          }
          context become analyzing(enriched, events, analyzers, publisher)
        }
      }
      case Ignore() => {
        context become ignore
      }
      case Publish(publisher) => {
        log.info(s"Start publishing message state for ${id}")
        if (publisher(message)) {
          context.parent ! MessageState(id, message)
        }
        context become analyzing(message, events, analyzers, publisher)
      }
    }
  }

  def ignore: Receive = {
    log.info(s"Ignore exchange ${id}")
    context stop status
    context stop logging

    {
      case _ => // ignore everything else
    }
  }

  val noop: Message => Boolean = (message) => false

  def initialize(me: MessageEvent): Option[BusinessContext] = (me.routing.context, me.routing.from, me.body) match {
    case ("navisionItemRoutes", _, Some(Body("be.e5mode.smx.scheduler.camel.CamelTaskItem$Incremental"))) =>
      Some(BusinessContext("scheduledTask", "Navision : Incremental Item Export"))
    case ("vendorRouteBuilder", _, Some(Body("be.e5mode.smx.scheduler.camel.CamelTaskItem$Incremental"))) =>
      Some(BusinessContext("scheduledTask", "Navision : Incremental Vendor Export"))
    case ("navisionAccountRoutes", _, Some(Body("be.e5mode.smx.scheduler.camel.CamelTaskItem$Incremental"))) =>
      Some(BusinessContext("scheduledTask", "Navision : Incremental Account Export"))
    case ("navisionCustomerRoutes", _, Some(Body("be.e5mode.smx.scheduler.camel.CamelTaskItem$Incremental"))) =>
      Some(BusinessContext("scheduledTask", "Navision : Incremental Customer Export"))
    case ("navisionItemRoutes", _, Some(Body("be.e5mode.smx.scheduler.camel.CamelTaskItem$Full"))) =>
      Some(BusinessContext("scheduledTask", "Navision : Full Item Export"))
    case ("vendorRouteBuilder", _, Some(Body("be.e5mode.smx.scheduler.camel.CamelTaskItem$Full"))) =>
      Some(BusinessContext("scheduledTask", "Navision : Full Vendor Export"))
    case ("navisionAccountRoutes", _, Some(Body("be.e5mode.smx.scheduler.camel.CamelTaskItem$Full"))) =>
      Some(BusinessContext("scheduledTask", "Navision : Full Account Export"))
    case ("navisionCustomerRoutes", _, Some(Body("be.e5mode.smx.scheduler.camel.CamelTaskItem$Full"))) =>
      Some(BusinessContext("scheduledTask", "Navision : Full Customer Export"))
    case ("ibmiRestCamelContext", Some("Endpoint[direct://articles.from.ibmi]"), _) =>
      Some(BusinessContext("message", "IBM i - Item update"))
    case ("ibmiRestCamelContext", Some("Endpoint[direct://receptions.from.ibmi]"), _) =>
      Some(BusinessContext("message", "IBM i - Reception"))
    case (ctx, rte, body) => {
      log.info(s"No match found for ${ctx}, ${rte} and ${body} - not initializing business context just yet")
      None
    }
  }
}

object CamelMessageActor {

  def props(id: String, identifier: ActorRef) = Props(classOf[CamelMessageActor], id, identifier)

}
