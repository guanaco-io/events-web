/*
 * Copyright 2018 - anova r&d bvba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package actors

import actors.MessageTypeIdentifier._
import actors.analyzer.{ItemJournalActor, OpusEdiActor, ScheduleActor, UnidentiedMessageActor}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import io.guanaco.events.messages.MessageEvent.Routing
import io.guanaco.events.messages._
import model.Message

/**
  * Created by gertv on 6/8/17.
  */
class MessageTypeIdentifier extends Actor with ActorLogging {


  val incrementalItems = context.actorOf(ScheduleActor.incremental("item"))
  val incrementalVendors = context.actorOf(ScheduleActor.incremental("vendor"))
  val incrementalAccounts = context.actorOf(ScheduleActor.incremental("account"))
  val incrementalCustomers = context.actorOf(ScheduleActor.incremental("customer"))
  val fullItems = context.actorOf(ScheduleActor.full("item"))
  val fullVendors = context.actorOf(ScheduleActor.full("vendor"))
  val fullAccounts = context.actorOf(ScheduleActor.full("account"))
  val fullCustomers = context.actorOf(ScheduleActor.full("customer"))
  val opus = context.actorOf(OpusEdiActor.props())
  val itemJournal = context.actorOf(ItemJournalActor.props())
  val unidentified = context.actorOf(UnidentiedMessageActor.props())


  override def receive: Receive = {
    case ByContext("alerta") =>
      sender() ! Ignore()
    case ByRouteAndType(route, "be.e5mode.smx.scheduler.camel.CamelTaskItem$Incremental") => {
      sender ! Publish()
      val analyzer = route match {
        case "navisionItemRoutes" => Some(incrementalItems)
        case "vendorRouteBuilder" => Some(incrementalVendors)
        case "navisionAccountRoutes" => Some(incrementalAccounts)
        case "navisionCustomerRoutes" => Some(incrementalCustomers)
      }
      analyzer map { ref =>
        sender ! Publish()
        sender ! Analyzer(ref, Some(self))
        ref ! Initialize(sender)
      }
    }
    case ByRouteAndType(route, "be.e5mode.smx.scheduler.camel.CamelTaskItem$Full") => {
      sender ! Publish()
      val analyzer = route match {
        case "navisionItemRoutes" => Some(fullItems)
        case "vendorRouteBuilder" => Some(fullVendors)
        case "navisionAccountRoutes" => Some(fullAccounts)
        case "navisionCustomerRoutes" => Some(fullCustomers)
      }
      analyzer map { ref =>
        sender ! Publish()
        sender ! Analyzer(ref, Some(self))
        ref ! Initialize(sender)
      }
    }
    case ByContext("navisionItemJournalRoutes") =>
      sender ! Publish()
      itemJournal ! Initialize(sender)
      sender ! Analyzer(itemJournal, Some(self))
    case event @ ByContext("ediOpusContext") => {
      log.info(s"Event identified as Opus - ${event}")
      sender ! Publish()
      opus ! Initialize(sender)
      sender ! Analyzer(opus, Some(self))
    }
    case log: LogEvent =>
      // ignoring the log events
    case event => {
      log.info(s"Unidentified event ${event}")
      sender ! Publish()
      unidentified ! Initialize(sender)
      sender ! Analyzer(unidentified, Some(self))
    }
  }

  /*
    case ("ibmiRestCamelContext", Some("Endpoint[direct://articles.from.ibmi]"), _) =>
      Some(BusinessContext("message", "IBM i - Item update"))
    case ("ibmiRestCamelContext", Some("Endpoint[direct://receptions.from.ibmi]"), _) =>
      Some(BusinessContext("message", "IBM i - Reception"))

   */
}

object MessageTypeIdentifier {

  def props(): Props = Props(classOf[MessageTypeIdentifier])

  case class Initialize(sender: ActorRef)
  case class Analyzer(ref: ActorRef, replaces: Option[ActorRef] = None)
  case class Enrich(fn: Message => Option[Message])

  sealed trait ProcessingMode
  case class Ignore() extends ProcessingMode
  case class Publish(publisher: Message => Boolean = always) extends ProcessingMode

  private val always: Message => Boolean = (message) => true

  object ByContext {
    def unapply(arg: Event) = arg match {
      case me: MessageEvent => Some(me.routing.context)
      case _ => None
    }
  }

  object ByRouteAndType {
    def unapply(arg: Event) = arg match {
      case me: MessageEvent if me.body.isDefined => Some(me.routing.context, me.body.get.javaType)
      case _ => None
    }
  }

  object ByBodyType {
    def unapply(arg: Event) = arg match {
      case me: MessageEvent => me.body map { body => body.javaType }
      case _ => None
    }
  }

}
