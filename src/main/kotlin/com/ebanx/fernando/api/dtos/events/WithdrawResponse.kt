package com.ebanx.fernando.api.dtos.events

import com.ebanx.fernando.api.dtos.Balance
import com.fasterxml.jackson.annotation.JsonProperty

data class WithdrawResponse(@JsonProperty val origin: Balance) : EventResponse
