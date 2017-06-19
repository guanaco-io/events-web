package model

import model.Logging.MessageLink

/**
  * Created by gertv on 5/23/17.
  */
case class Logging(timestamp: String, level: String, message: String, link: Option[MessageLink] ) {

}

object Logging {

  case class MessageLink(id: String)

}
