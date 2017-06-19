module Activities exposing (..)

import Html exposing (program, text, Html)
import Html.Attributes exposing (class)
import Html
import Http
import Json.Decode as Decode

type alias BusinessActivity = {
     timestamp: String,
     description: String,
     status: String,
     from: Maybe String,
     to: Maybe String
  }

type alias Model = List BusinessActivity

type Msg =
    ActivitiesLoaded (Result Http.Error (List BusinessActivity))

init: (Model, Cmd Msg)
init =
    ( [], Http.send ActivitiesLoaded getActivities )

getActivities: Http.Request (List BusinessActivity)
getActivities =
    Http.get "/api/activity/recent" decodeActivities

decodeActivities: Decode.Decoder (List BusinessActivity)
decodeActivities =
    Decode.list decodeActivity


decodeActivity: Decode.Decoder BusinessActivity
decodeActivity =
    Decode.map5 BusinessActivity
        (Decode.field "timestamp" Decode.string)
        (Decode.field "description" Decode.string)
        (Decode.field "status" Decode.string)
        (Decode.field "from" (Decode.maybe Decode.string))
        (Decode.field "to" (Decode.maybe Decode.string))

update: Msg -> Model -> (Model, Cmd msg)
update msg model =
    case msg of
        ActivitiesLoaded(Ok activities) -> (activities, Cmd.none)
        ActivitiesLoaded(Err _) -> ([], Cmd.none)


subscriptions: Model -> Sub Msg
subscriptions model =
    Sub.none

view: Model -> Html msg
view model =
    case model of
        [] -> Html.text("Loading activities ...")
        activities ->
            Html.table [ Html.Attributes.class "table" ] [
                Html.tbody []
                    (List.map viewActivity activities)
            ]

viewActivity: BusinessActivity -> Html msg
viewActivity activity =
    Html.tr [ classForActivity activity ] [
        Html.td [] [Html.text activity.timestamp],
        Html.td [] [Html.text (Maybe.withDefault "/" activity.from)],
        Html.td [] [Html.text (Maybe.withDefault "/" activity.to)],
        Html.td [] [Html.text activity.description]
    ]

classForActivity: BusinessActivity -> Html.Attribute msg
classForActivity activity =
    case activity.status of
        "success" -> Html.Attributes.class "success"
        _ -> Html.Attributes.class "neutral"


main : Program Never Model Msg
main =
    program {
        init = init,
        view = view,
        update = update,
        subscriptions = subscriptions
    }
