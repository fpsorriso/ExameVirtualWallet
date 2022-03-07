package com.ebanx.fernando.api.services

import com.ebanx.fernando.api.adapters.DepositResponseBy
import com.ebanx.fernando.api.adapters.EventResponseBy
import com.ebanx.fernando.api.domains.Deposit
import com.ebanx.fernando.api.domains.EventCheck
import com.ebanx.fernando.api.domains.EventRun
import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.DepositResponse
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.springframework.context.ApplicationContext

internal class EventBusTest {
    private val expectedEventResponseBeanName = DepositResponseBy::class.simpleName!!

    private val expectedEventsRun = arrayOf(EventType.DEPOSIT.name, EventBus::class.simpleName)

    private val expectedEventsType = arrayOf(
        EventType.DEPOSIT.name,
        EventType.TRANSFER.name,
        expectedEventResponseBeanName
    )

    private val expectedEventsResponse = arrayOf(expectedEventResponseBeanName)

    private val expectedAccountId = "100"

    private val expectedAmount = 10

    private val expectedDepositBalance = setOf(Balance(expectedAccountId, expectedAmount))

    private val expectedDepositResponse = DepositResponse(expectedDepositBalance.first())

    private val expectedEvent = Event(
        type = EventType.DEPOSIT,
        destination = expectedAccountId,
        amount = expectedAmount
    )

    private val expectedEventWithoutStrategy = Event(
        type = EventType.WITHDRAW,
        origin = expectedAccountId,
        amount = expectedAmount
    )

    private val expectedStrategyNotImplementedMessage =
        "Not implemented Strategy to ${expectedEventWithoutStrategy.type}"

    private val expectedDepositBeanMock = mock(Deposit::class.java)

    private val expectedDepositResponseByMock = mock(DepositResponseBy::class.java)

    private val applicationContextMock = mock(ApplicationContext::class.java)

    private val eventBus: EventBus by lazy { EventBus(applicationContextMock) }

    @BeforeEach
    internal fun setUp() {
        `when`(applicationContextMock.getBeanNamesForType(EventCheck::class.java)).thenReturn(expectedEventsType)
        `when`(applicationContextMock.getBeanNamesForType(EventRun::class.java)).thenReturn(expectedEventsRun)

        `when`(applicationContextMock.getBeanNamesForType(EventResponseBy::class.java)).thenReturn(
            expectedEventsResponse
        )

        `when`(applicationContextMock.getBean(EventType.DEPOSIT.name)).thenReturn(expectedDepositBeanMock)

        `when`(applicationContextMock.getBean(expectedEventResponseBeanName)).thenReturn(
            expectedDepositResponseByMock
        )

        `when`(expectedDepositBeanMock.execute(expectedEvent)).thenReturn(expectedDepositBalance)
        `when`(expectedDepositBeanMock.getType()).thenReturn(EventType.DEPOSIT)

        `when`(expectedDepositResponseByMock.getType()).thenReturn(EventType.DEPOSIT)
        `when`(expectedDepositResponseByMock.createBy(expectedEvent, expectedDepositBalance)).thenReturn(
            expectedDepositResponse
        )
    }

    @AfterEach
    internal fun tearDown() {
        verifyNoMoreInteractions(applicationContextMock, expectedDepositBeanMock)
    }

    @Test
    fun `test get Deposit Bean`() {
        //arrange
        //act
        val balances = eventBus.execute(expectedEvent)

        //assert
        verify(applicationContextMock).getBeanNamesForType(EventRun::class.java)
        verify(applicationContextMock).getBeanNamesForType(EventCheck::class.java)
        verify(applicationContextMock).getBean(EventType.DEPOSIT.name)
        verify(expectedDepositBeanMock).getType()
        verify(expectedDepositBeanMock).execute(expectedEvent)
        assertEquals(expectedDepositBalance, balances)
    }

    @Test
    fun `test to get NotImplemented exception when sending an event that has no Event Strategy`() {
        //arrange
        //act
        assertFailsWith(
            NotImplementedError::class,
            expectedStrategyNotImplementedMessage
        ) { eventBus.execute(expectedEventWithoutStrategy) }

        //assert
        verify(applicationContextMock).getBeanNamesForType(EventRun::class.java)
        verify(applicationContextMock).getBeanNamesForType(EventCheck::class.java)
        verify(applicationContextMock).getBean(EventType.DEPOSIT.name)
        verify(expectedDepositBeanMock).getType()
    }

    @Test
    fun `test to create a DepositResponse when the type is Deposit`() {
        //arrange

        //act
        val response = eventBus.createBy(expectedEvent, expectedDepositBalance)

        //assert
        verify(applicationContextMock).getBeanNamesForType(EventResponseBy::class.java)
        verify(applicationContextMock).getBeanNamesForType(EventCheck::class.java)
        verify(applicationContextMock).getBean(expectedEventResponseBeanName)
        verify(expectedDepositResponseByMock).getType()
        verify(expectedDepositResponseByMock).createBy(expectedEvent, expectedDepositBalance)
        assertEquals(expectedDepositResponse, response)
    }

    @Test
    fun `test to get NotImplemented exception when creating an event response that has no Event Strategy`() {
        //arrange
        //act
        assertFailsWith(
            NotImplementedError::class,
            expectedStrategyNotImplementedMessage
        ) { eventBus.createBy(expectedEventWithoutStrategy, setOf()) }

        //assert
        verify(applicationContextMock).getBeanNamesForType(EventResponseBy::class.java)
        verify(applicationContextMock).getBeanNamesForType(EventCheck::class.java)
        verify(applicationContextMock).getBean(expectedEventResponseBeanName)
        verify(expectedDepositResponseByMock).getType()
    }
}
