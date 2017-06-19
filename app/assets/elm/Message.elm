module Message exposing (..)

import Html exposing (programWithFlags, text, Html)
import Html.Attributes exposing (class)
import Html
import Http
import Json.Decode as Decode
import WebSocket
import Domain exposing (..)

type alias Flags = {
    id : String
  }


type alias Model = {
    id: String,
    message: Maybe Message
  }

type Msg =
    MessageLoaded (Result Http.Error (Maybe Message))
    | MessageUpdated (Result String Message)

init: Flags -> (Model, Cmd Msg)
init flags =
    let model = {
        id = flags.id,
        message = Nothing
    }
    in
    ( model, Http.send MessageLoaded (getModel flags.id))

getModel: String -> Http.Request (Maybe Message)
getModel id =
    Http.get ("/api/messages/" ++ id) (Decode.maybe decodeMessage)

update: Msg -> Model -> (Model, Cmd msg)
update msg model =
    case msg of
        MessageLoaded(Ok message) -> ({ model | message = message }, Cmd.none)
        MessageLoaded(Err _) -> (model, Cmd.none)
        MessageUpdated(Ok message) -> ({ model | message = Just message }, Cmd.none)
        MessageUpdated(Err _) -> (model, Cmd.none)


subscriptions: Model -> Sub Msg
subscriptions model =
    WebSocket.listen ("ws://localhost:9000/api/messages/" ++ model.id ++ "/updates") subscriptionModel

subscriptionModel: String -> Msg
subscriptionModel string =
    MessageUpdated (Decode.decodeString decodeMessage string)


view: Model -> Html msg
view model =
    case model.message of
        Nothing -> Html.text("Loading message...")
        Just message ->
            if (List.length message.logging > 0) then
                Html.div [] [
                    Html.text message.status,
                    Html.table [ Html.Attributes.class "table" ] [
                        Html.tbody []
                            (List.map viewLogging message.logging)
                    ]
                ]
            else
                Html.text("No logging yet")

viewLogging: Logging -> Html msg
viewLogging logging =
    Html.tr [ classForLogging logging ] [
        Html.td [] [Html.text logging.timestamp],
        Html.td [] [Html.text logging.level],
        Html.td [] [Html.pre [] [Html.text logging.message]]
        ]

classForLogging: Logging -> Html.Attribute msg
classForLogging logging =
    case logging.level of
        "WARN" -> Html.Attributes.class "warning"
        "ERROR" -> Html.Attributes.class "danger"
        _ -> Html.Attributes.class "neutral"


main : Program Flags Model Msg
main =
    programWithFlags {
        init = init,
        view = view,
        update = update,
        subscriptions = subscriptions
    }
