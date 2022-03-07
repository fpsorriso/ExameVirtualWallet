package com.ebanx.fernando.api.adapters

import com.ebanx.fernando.api.domains.EventCheck
import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import com.ebanx.fernando.api.dtos.events.WithdrawResponse
import org.springframework.stereotype.Component

@Component
class WithdrawResponseBy : EventResponseBy<WithdrawResponse>, EventCheck {
    override fun getType(): EventType = EventType.WITHDRAW

    override fun createBy(event: Event, balances: Set<Balance>): WithdrawResponse? {
        return balances.find { it.accountId == event.origin }?.let { WithdrawResponse(it) }
    }
}