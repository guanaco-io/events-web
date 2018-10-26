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
