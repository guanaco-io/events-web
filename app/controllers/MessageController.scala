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

package controllers

import javax.inject.{Inject, Named, Singleton}

import actors.LoggingActor.{RecentLogging, RecentLoggingRequest}
import actors.CamelMessagesActor
import actors.CamelMessagesActor._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Action, Controller, WebSocket}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import controllers.RecentMessages.{GetRecent, Recent}
import model.Message
import play.api.libs.streams.ActorFlow
import services.MessageComponent

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by gertv on 5/28/17.
  */
@Singleton
class MessageController @Inject()(@Named("messages") messages: ActorRef, @Named("events") events: ActorRef, service: MessageComponent)(implicit ec: ExecutionContext, system: ActorSystem) extends Controller {

  implicit val materializer = ActorMaterializer()

  implicit val timeout: Timeout = 5 seconds
  implicit val recents = system.actorOf(RecentMessages.props(messages), "recent_messages")

  def recent() = Action.async {
    (recents ? GetRecent()).mapTo[Recent] map { result =>
      Ok(Json.toJson(result.messages))
    }
  }

  def get(id: String) = Action.async {
    service.get(id) map { result =>
      result match {
        case Some(message) => Ok(Json.toJson(message))
        case None => NotFound(s"Message with id ${id} not found")
      }
    }
  }

  def updates(id: String) = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef(out => MessageUpdatesActor.props(messages, out, id))
  }

  def updatesFull() = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef(out => MessageUpdatesActor.props(messages, out))
  }



}

class MessageUpdatesActor(messages: ActorRef, out: ActorRef, id: Option[String]) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info(s"Subscribing ${self} to message updates")
    messages ! CamelMessagesActor.Subscribe()
  }

  override def postStop(): Unit = messages ! CamelMessagesActor.Unsubscribe()

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    reason.printStackTrace()
    print(message )
  }

  override def receive: Receive = id match {
    case Some(id) => {
      log.info(s"Tracking changes for ${id}")
      filtered(id)
    }
    case None => {
      log.info("Tracking all message changes")
      full()
    }
  }

  def full(): Receive = {
    case MessageState(id, message) => {
      out ! Json.toJson(message)
    }
  }

  def filtered(id: String): Receive = {
    case MessageState(id, message) => {
      out ! Json.toJson(message)
    }
  }
}

object MessageUpdatesActor {

  def props(message: ActorRef, out: ActorRef, id: String) = Props(classOf[MessageUpdatesActor], message, out, Some(id))
  def props(message: ActorRef, out: ActorRef) = Props(classOf[MessageUpdatesActor], message, out, None)
}

class RecentMessages(messages: ActorRef) extends Actor with ActorLogging {



  override def preStart(): Unit = messages ! Subscribe()

  override def postStop(): Unit = messages ! Unsubscribe()

  override def receive: Receive = state(Map.empty[String, Message])



  def state(recent: Map[String, Message]): Receive = {
    case MessageState(id, message) => {
      val updated = recent.updated(id, message)
      context become state(trim(updated))
    }
    case GetRecent() => {
      log.info(s"Getting recent exchanges from ${sender}")
      sender ! Recent((recent.toSeq map { case (key, value) => value }).reverse)
    }
  }

  def trim(messages: Map[String, Message]): Map[String, Message] =
    if (messages.size <= 10) messages
    else {
      val sorted = messages.toSeq sortWith (messageTimestamp)
      val (id, _) = sorted.head
      trim(messages - id)
    }


  val messageTimestamp: ((String, Message), (String, Message)) => Boolean = (left, right) => {
    val (_, leftmessage) = left
    val (_, rightmessage) = right

    (leftmessage.timestamp, rightmessage.timestamp) match {
      case (Some(leftt), Some(rightt)) => leftt.isBefore(rightt)
      case (None, Some(_)) => true
      case _ => false
    }
  }


}

object RecentMessages {

  def props(messages: ActorRef) = Props(classOf[RecentMessages], messages)

  case class GetRecent()
  case class Recent(messages: Seq[Message])

}