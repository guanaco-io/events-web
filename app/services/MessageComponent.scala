package services

import javax.inject.{Inject, Singleton}

import model.Message
import play.api.libs.json.{JsObject, JsString, Json}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by gertv on 5/29/17.
  */
@Singleton
class MessageComponent @Inject() (val reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext) extends MongoController with ReactiveMongoComponents {


  val collection = database map { db => db.collection[JSONCollection]("messages") }
  collection flatMap { collection =>
    collection.indexesManager.ensure(Index(Seq("id" -> Ascending), unique = true))
  } onSuccess({ case boolean => s"Index created: ${boolean}"})

  implicit val messageKeyFormat = Json.format[MessageKey]

  def store(message: Message): Unit = {
    collection map { collection =>
      val key = MessageKey(message.id)
      collection.update(key, message, upsert = true)
    }
  }

  def delete(id: String): Unit = {
    collection map { collection =>
      collection.remove(MessageKey(id))
    }
  }

  def get(id: String): Future[Option[Message]] =
    collection flatMap { collection =>
      val key = MessageKey(id)
      collection.find(key).one[Message]
    }

  case class MessageKey(id: String)

}
