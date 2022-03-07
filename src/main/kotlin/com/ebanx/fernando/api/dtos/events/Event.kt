package com.ebanx.fernando.api.dtos.events

import com.ebanx.fernando.api.formatters.EventTypeDeserializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonIgnoreProperties(ignoreUnknown = true)
data class Event(
    @JsonProperty @JsonDeserialize(using = EventTypeDeserializer::class) val type: EventType,
    @JsonProperty val destination: String? = null,
    @JsonProperty val origin: String? = null,
    @JsonProperty val amount: Int
)
