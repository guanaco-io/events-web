module Messages exposing (..)

import Html exposing (program, text, Html)
import Html.Attributes exposing (class)
import Html
import Http
import Json.Decode as Decode
import Domain exposing (..)
import WebSocket
import Debug

type alias Model = List Message

type Msg =
    MessagesLoaded (Result Http.Error (List Message))
    | NewMessage (Result String Message)

init: (Model, Cmd Msg)
init =
    ( [], Http.send MessagesLoaded (getMessages))

getMessages: Http.Request (List Message)
getMessages =
    Http.get ("/api/messages/recent") (Decode.list decodeMessage)

update: Msg -> Model -> (Model, Cmd msg)
update msg model =
    case msg of
        MessagesLoaded(Ok list) -> (list, Cmd.none)
        MessagesLoaded(Err msg) ->
            Debug.log(toString msg)
            (model, Cmd.none)
        NewMessage(Ok msg) ->
            let
                filtered = List.filter (filterMessageById msg.id) model
                result = List.take 10 (msg :: filtered)
            in
                (result, Cmd.none)
        NewMessage(Err msg) ->
            Debug.log(toString msg)
            (model, Cmd.none)


filterMessageById: String -> Message -> Bool
filterMessageById id message =
    message.id /= id

subscriptions: Model -> Sub Msg
subscriptions model =
    if (List.isEmpty model) then
        Sub.none
    else
        WebSocket.listen "ws://localhost:9000/api/messages/updates" subscriptionMessage

subscriptionMessage: String -> Msg
subscriptionMessage string =
    NewMessage (Decode.decodeString decodeMessage string)

view: Model -> Html msg
view model =
    if (List.length model > 0) then
        Html.table [ Html.Attributes.class "table", Html.Attributes.class "table-condensed" ] [
            Html.tbody []
                (List.map viewMessage model)
            ]
    else
        Html.text "No messages yet"


viewMessage: Message -> Html msg
viewMessage message =
        Html.tr [ classForLogging message ] [
            Html.td [] [
                Html.a [ Html.Attributes.href ("/messages/" ++ message.id) ] [
                    Html.span [
                        Html.Attributes.class "glyphicon",
                        Html.Attributes.attribute "aria-hidden" "true",
                        glyphiconForMessage message
                    ] []
                ]
            ],
            Html.td [] [
                case message.timestamp of
                    Just timestamp -> Html.text timestamp
                    Nothing -> Html.text "n/a"
            ],
            Html.td [] [
                case message.business of
                    Just business ->
                        case business.entity of
                            Just entity -> Html.text (entity.name ++ ": " ++ entity.value)
                            Nothing ->
                                case business.description of
                                    Just description -> Html.text description
                                    Nothing -> Html.text "n/a"
                    Nothing ->
                        Html.text "n/a"
            ],
            Html.td [] [Html.text message.id],
            Html.td [] [Html.text "/"]
        ]

glyphiconForMessage: Message -> Html.Attribute msg
glyphiconForMessage message =
    case message.business of
        Just business ->
            case business.processType of
                Just "scheduled" -> Html.Attributes.class "glyphicon-time"
                Just "message" -> Html.Attributes.class "glyphicon-envelope"
                _ -> Html.Attributes.class "glyphicon-check"
        Nothing -> Html.Attributes.class "glyphicon-question-sign"

classForLogging: Message -> Html.Attribute msg
classForLogging message =
    case message.status of
        "done" -> Html.Attributes.class "success"
        "failed" -> Html.Attributes.class "danger"
        "processing" -> Html.Attributes.class "neutral"
        _ -> Html.Attributes.class "warning"


main : Program Never Model Msg
main =
    program {
        init = init,
        view = view,
        update = update,
        subscriptions = subscriptions
    }
