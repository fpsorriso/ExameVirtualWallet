package com.ebanx.fernando.api.dtos.events

import com.ebanx.fernando.api.dtos.Balance
import com.fasterxml.jackson.annotation.JsonProperty

data class TransferResponse(@JsonProperty val origin: Balance, @JsonProperty val destination: Balance) : EventResponse
