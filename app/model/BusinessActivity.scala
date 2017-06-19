package model

import java.time.ZonedDateTime

/**
  * Created by gertv on 5/31/17.
  */
trait BusinessActivity {

  val uuid: String
  val logging: Seq[Logging]
  val timestamp: ZonedDateTime

  val description: String
  val status: String

}

case class BusinessMessage(timestamp: ZonedDateTime, uuid: String, description: String, logging: Seq[Logging], from: Option[String], to: Option[String], status: String) extends BusinessActivity
