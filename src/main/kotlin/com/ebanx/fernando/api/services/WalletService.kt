package com.ebanx.fernando.api.services

import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventResponse

interface WalletService {
    fun reset()
    fun sendEvent(event: Event): EventResponse
    fun getBalanceByAccountId(accountId: String): Int
}