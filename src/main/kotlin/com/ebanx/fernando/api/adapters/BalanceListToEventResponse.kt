package com.ebanx.fernando.api.adapters

import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventResponse

interface EventResponseBy<T:EventResponse> {
    fun createBy(event: Event, balances: Set<Balance>): T?
}