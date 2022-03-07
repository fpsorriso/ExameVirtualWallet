package com.ebanx.fernando.api.domains

import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import org.springframework.stereotype.Component

@Component
class Transfer(val withdraw: Withdraw, val deposit: Deposit) : EventRun, EventCheck {
    override fun getType() = EventType.TRANSFER

    override fun execute(event: Event): Set<Balance>? {
        return transfer(event)
    }

    private fun transfer(event: Event): Set<Balance> {
        return if (!event.origin.isNullOrEmpty() && !event.destination.isNullOrEmpty()) {
            withdraw.execute(event)!! union deposit.execute(event)!!

        } else setOf()
    }
}