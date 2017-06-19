package model
import java.time.ZonedDateTime

/**
  * Created by gertv on 5/27/17.
  */
case class Message(id: String, timestamp: Option[ZonedDateTime], logging: Seq[Logging], status: String, business: Option[BusinessContext] = None) {

}

case class BusinessContext(processType: String, description: String, entity: Option[BusinessEntity] = None, related: Seq[BusinessEntity] = Seq())

case class BusinessEntity(name: String, id: String)


