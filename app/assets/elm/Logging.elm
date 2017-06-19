module Logging exposing (..)

import Html exposing (program, text, Html)
import Html.Attributes exposing (class)
import Html
import Http
import Json.Decode as Decode
import WebSocket

type alias Logging = {
    timestamp: String,
    level: String,
    message: String,
    link: Maybe String
  }

type alias Model = List Logging

type Msg =
    LoggingLoaded (Result Http.Error (List Logging))
    | NewLogging (Result String Logging)

init: (Model, Cmd Msg)
init =
    ( [], Http.send LoggingLoaded (getLogging))

getLogging: Http.Request (List Logging)
getLogging =
    Http.get ("/api/logging/recent") decodeLoggingList

decodeLoggingList: Decode.Decoder (List Logging)
decodeLoggingList =
    Decode.list decodeLogging

decodeLogging: Decode.Decoder Logging
decodeLogging =
    Decode.map4 Logging
       (Decode.field "timestamp" Decode.string)
       (Decode.field "level" Decode.string)
       (Decode.field "message" Decode.string)
       (Decode.maybe (Decode.field "link" (
         Decode.field "id" Decode.string
       )))



update: Msg -> Model -> (Model, Cmd msg)
update msg model =
    case msg of
        LoggingLoaded(Ok list) -> (list, Cmd.none)
        LoggingLoaded(Err _) -> ([], Cmd.none)
        NewLogging(Ok logging) -> (logging :: model, Cmd.none)
        NewLogging(Err _) -> ([], Cmd.none)


subscriptions: Model -> Sub Msg
subscriptions model =
    WebSocket.listen "ws://localhost:9000/api/logging/updates" subscriptionMessage

subscriptionMessage: String -> Msg
subscriptionMessage string =
    NewLogging (Decode.decodeString decodeLogging string)


view: Model -> Html msg
view model =
    if (List.length model > 0) then
        Html.table [ Html.Attributes.class "table" ] [
            Html.tbody []
                (List.map viewLogging model)
            ]
    else
        Html.text "No logging yet"


viewLogging: Logging -> Html msg
viewLogging logging =
    case logging.link of
        Nothing ->
            Html.tr [ classForLogging logging ] [
                Html.td [] [Html.text logging.timestamp],
                Html.td [] [Html.text logging.level],
                Html.td [] [Html.text logging.message],
                Html.td [] [Html.text "/"]
            ]
        Just link ->
            Html.tr [ classForLogging logging ] [
                Html.td [] [Html.text logging.timestamp],
                Html.td [] [Html.text logging.level],
                Html.td [] [Html.text logging.message],
                Html.td [] [Html.a [Html.Attributes.href ("/messages/" ++ link)] [Html.text link]]
            ]




classForLogging: Logging -> Html.Attribute msg
classForLogging logging =
    case logging.level of
        "WARN" -> Html.Attributes.class "warning"
        "ERROR" -> Html.Attributes.class "danger"
        _ -> Html.Attributes.class "neutral"


main : Program Never Model Msg
main =
    program {
        init = init,
        view = view,
        update = update,
        subscriptions = subscriptions
    }
