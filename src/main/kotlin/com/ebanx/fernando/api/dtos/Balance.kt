package com.ebanx.fernando.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class Balance(@JsonProperty("id") val accountId: String, @JsonProperty val balance: Int)
