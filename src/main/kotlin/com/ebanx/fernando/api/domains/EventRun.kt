package com.ebanx.fernando.api.domains

import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event

interface EventRun {
    fun execute(event: Event): Set<Balance>?
}