module Domain exposing (..)

import Json.Decode as Decode

type alias Logging = {
    timestamp: String,
    level: String,
    message: String
  }

type alias BusinessEntity = {
    name: String,
    value: String
  }

type alias Business = {
    processType: Maybe String,
    description: Maybe String,
    entity: Maybe BusinessEntity,
    related: List BusinessEntity
  }

type alias Message = {
     id: String,
     timestamp: Maybe String,
     logging: List Logging,
     status: String,
     business: Maybe Business
  }

decodeMessage: Decode.Decoder Message
decodeMessage =
    Decode.map5 Message
        (Decode.field "id" Decode.string)
        (Decode.maybe (Decode.field "timestamp" Decode.string))
        (Decode.field "logging" decodeLoggingList)
        (Decode.field "status" Decode.string)
        (Decode.maybe (Decode.field "business" decodeBusiness))

decodeBusiness: Decode.Decoder Business
decodeBusiness =
    Decode.map4 Business
        (Decode.maybe (Decode.field "processType" Decode.string))
        (Decode.maybe (Decode.field "description" Decode.string))
        (Decode.maybe (Decode.field "entity" decodeEntity))
        (Decode.field "related" (Decode.list decodeEntity))

decodeEntity: Decode.Decoder BusinessEntity
decodeEntity =
    Decode.map2 BusinessEntity
        (Decode.field "name" Decode.string)
        (Decode.field "id" Decode.string)

decodeLoggingList: Decode.Decoder (List Logging)
decodeLoggingList =
    Decode.list decodeLogging

decodeLogging: Decode.Decoder Logging
decodeLogging =
    Decode.map3 Logging
       (Decode.field "timestamp" Decode.string)
       (Decode.field "level" Decode.string)
       (Decode.field "message" Decode.string)