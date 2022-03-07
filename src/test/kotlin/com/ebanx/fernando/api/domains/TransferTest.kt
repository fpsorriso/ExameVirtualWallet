package com.ebanx.fernando.api.domains

import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import com.ebanx.fernando.api.exceptions.AccountNotFoundException
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

internal class TransferTest {

    private val expectedEventType = EventType.TRANSFER

    private val expectedOriginAccountId = "100"

    private val expectedDestinationAccountId = "200"

    private val expectedCurrentAmount = 100

    private val expectedAmount = 10

    private val expectedOriginNotFoundMessage = "Account $expectedOriginAccountId not found"

    private val withdrawMock = mock(Withdraw::class.java)

    private val depositMock = mock(Deposit::class.java)

    private val eventRun = Transfer(withdrawMock, depositMock)

    @AfterEach
    internal fun tearDown() {
        verifyNoMoreInteractions(withdrawMock, depositMock)
    }

    @Test
    fun `transfer from a existing account to another existing account`() {
        //arrange
        val originBalance = Balance(expectedOriginAccountId, expectedCurrentAmount - expectedAmount)
        val destinationBalance = Balance(expectedDestinationAccountId, expectedCurrentAmount + expectedAmount)

        val requestEvent = Event(
            type = expectedEventType,
            origin = expectedOriginAccountId,
            destination = expectedDestinationAccountId,
            amount = expectedAmount
        )

        val expectedBalances = setOf(originBalance,destinationBalance)

        `when`(withdrawMock.execute(requestEvent)).thenReturn(setOf(originBalance))
        `when`(depositMock.execute(requestEvent)).thenReturn(setOf(destinationBalance))

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        verify(withdrawMock).execute(requestEvent)
        verify(depositMock).execute(requestEvent)
        assertEquals(expectedBalances, balances)
    }

    @Test
    fun `transfer from a invalid origin account`() {
        //arrange
        val requestEvent = Event(
            type = expectedEventType,
            origin = expectedOriginAccountId,
            destination = expectedDestinationAccountId,
            amount = expectedAmount
        )

        doAnswer { throw AccountNotFoundException(expectedOriginAccountId) }.`when`(withdrawMock).execute(requestEvent)

        assertFailsWith(
            AccountNotFoundException::class,
            expectedOriginNotFoundMessage
        ) { eventRun.execute(requestEvent) }

        //assert
        verify(withdrawMock).execute(requestEvent)
    }

    @Test
    fun `transfer with null or empty origin`() {
        //arrange
        val requestEvent =
            Event(type = expectedEventType, destination = expectedDestinationAccountId, amount = expectedAmount)

        val expectedBalances = setOf<Balance>()

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        assertEquals(expectedBalances, balances)
    }

    @Test
    fun `transfer with null or empty destination`() {
        //arrange
        val requestEvent =
            Event(type = expectedEventType, origin = expectedOriginAccountId, amount = expectedAmount)

        val expectedBalances = setOf<Balance>()

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        assertEquals(expectedBalances, balances)
    }

    @Test
    fun `transfer with amount is zero`() {
        //arrange
        val requestEvent = Event(
            type = expectedEventType,
            origin = expectedOriginAccountId,
            destination = expectedDestinationAccountId,
            amount = expectedAmount
        )

        val expectedBalances = setOf<Balance>()

        `when`(withdrawMock.execute(requestEvent)).thenReturn(setOf())
        `when`(depositMock.execute(requestEvent)).thenReturn(setOf())

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        verify(withdrawMock).execute(requestEvent)
        verify(depositMock).execute(requestEvent)
        assertEquals(expectedBalances, balances)
    }
}