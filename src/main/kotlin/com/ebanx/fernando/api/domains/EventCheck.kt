package com.ebanx.fernando.api.domains

import com.ebanx.fernando.api.dtos.events.EventType

interface EventCheck {
    fun getType(): EventType
}