import model.Logging.MessageLink
import play.api.libs.json.{Format, Json, OFormat}

/**
  * Created by gertv on 5/23/17.
  */
package object model {

  implicit val relatedToMessage = Json.format[MessageLink]
  implicit val loggingFormat = Json.format[Logging]

  implicit val businessEntity = Json.format[BusinessEntity]
  implicit val businessContext = Json.format[BusinessContext]
  implicit val messageFormat = Json.format[Message]

  implicit val businessMessage = Json.format[BusinessMessage]

}
