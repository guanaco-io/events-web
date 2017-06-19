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
