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

package services

import javax.inject.{Inject, Singleton}

import model.{BusinessMessage, Message}
import play.api.libs.json.Json
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.play.json.collection.JSONCollection

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by gertv on 5/29/17.
  */
@Singleton
class BusinessActivityComponent() {

  var data = mutable.Buffer.empty[BusinessMessage]

  def store(message: BusinessMessage): Unit = {
    data += message
  }

}
