package com.ebanx.fernando.api.formatters

import com.ebanx.fernando.api.dtos.events.EventType
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class EventTypeDeserializer(vc: Class<*>? = null) : StdDeserializer<EventType>(vc) {
    override fun deserialize(parser: JsonParser?, context: DeserializationContext?): EventType =
        EventType.valueOf(parser?.text?.uppercase() ?: throw IllegalArgumentException())
}