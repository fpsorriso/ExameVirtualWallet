package com.ebanx.fernando.api.adapters

import com.ebanx.fernando.api.domains.EventCheck
import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import com.ebanx.fernando.api.dtos.events.TransferResponse
import org.springframework.stereotype.Component

@Component
class TransferResponseBy : EventResponseBy<TransferResponse>, EventCheck {
    override fun getType(): EventType = EventType.TRANSFER

    override fun createBy(event: Event, balances: Set<Balance>): TransferResponse? {
        val origin = balances.find { it.accountId == event.origin }
        val destination = balances.find { it.accountId == event.destination }

        return TransferResponse(checkNotNull(origin), checkNotNull(destination))
    }
}