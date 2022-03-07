package com.ebanx.fernando.api.adapters

import com.ebanx.fernando.api.domains.EventCheck
import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.DepositResponse
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import org.springframework.stereotype.Component

@Component
class DepositResponseBy: EventResponseBy<DepositResponse>, EventCheck {
    override fun getType(): EventType = EventType.DEPOSIT

    override fun createBy(event: Event, balances: Set<Balance>): DepositResponse? {
        return balances.find { it.accountId == event.destination }?.let { DepositResponse(it) }
    }
}