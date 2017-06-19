package actors

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import services.MessageComponent

/**
  * Created by gertv on 5/25/17.
  */
class GuanacoModule extends AbstractModule with AkkaGuiceSupport {

  def configure = {
    bindActor[EventsActor]("events")
    bindActor[LoggingActor]("logging")
    bindActor[CamelMessagesActor]("messages")
  }

}
