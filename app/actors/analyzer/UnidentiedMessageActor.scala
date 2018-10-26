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
