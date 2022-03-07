package com.ebanx.fernando.api.services

import com.ebanx.fernando.api.adapters.EventResponseBy
import com.ebanx.fernando.api.domains.EventCheck
import com.ebanx.fernando.api.domains.EventRun
import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventResponse
import java.util.Objects
import java.util.stream.Collectors.toSet
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service


@Service
class EventBus(applicationContext: ApplicationContext) : EventRun, EventResponseBy<EventResponse> {
    private val eventsStrategyBeans by lazy {
        getBeansThatIs(
            applicationContext,
            EventRun::class.java,
            EventCheck::class.java
        )
    }

    private val eventsResponseAdapterBeans by lazy {
        getBeansThatIs(
            applicationContext,
            EventResponseBy::class.java,
            EventCheck::class.java
        )
    }

    override fun execute(event: Event): Set<Balance>? {
        val strategy = eventsStrategyBeans
            .filterIsInstance(EventCheck::class.java)
            .find { it.getType() == event.type }

        return if (strategy is EventRun) {
            strategy.execute(event)
        } else {
            throw NotImplementedError("Not implemented Strategy to ${event.type}")
        }
    }

    override fun createBy(event: Event, balances: Set<Balance>): EventResponse? {
        val adapter = eventsResponseAdapterBeans
            .filterIsInstance(EventCheck::class.java)
            .find { it.getType() == event.type }

        return if (adapter is EventResponseBy<*>) {
            adapter.createBy(event, balances)
        } else {
            throw NotImplementedError("Not implemented Response adapter to ${event.type}")
        }
    }

    private fun getBeansThatIs(applicationContext: ApplicationContext, vararg types: Class<*>): Set<Any> {
        val beanNames = types.toSet().stream()
            .map(applicationContext::getBeanNamesForType)
            .filter(Objects::nonNull)
            .map(Array<String>::toSet)
            .reduce { current, next -> current.intersect(next) }
            .orElse(setOf())


        return beanNames.stream()
            .map(applicationContext::getBean)
            .collect(toSet())
    }
}