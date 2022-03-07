package com.ebanx.fernando.api.domains

import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import com.ebanx.fernando.api.exceptions.AccountNotFoundException
import com.ebanx.fernando.api.repositories.AccountRepository
import com.ebanx.fernando.api.repositories.models.Account
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verifyNoMoreInteractions

internal class WithdrawTest {

    private val expectedEventType = EventType.WITHDRAW

    private val expectedAccountId = "100"

    private val expectedAmount = 10

    private val expectedNotFoundMessage = "Account $expectedAccountId not found"

    private val accountRepository = Mockito.mock(AccountRepository::class.java)

    private val eventRun = Withdraw(accountRepository)

    @AfterEach
    internal fun tearDown() {
        verifyNoMoreInteractions(accountRepository)
    }

    @Test
    fun `withdraw from a existing account`() {
        //arrange
        val existingAccount = Account(expectedAccountId, expectedAmount)
        val requestEvent = Event(type = expectedEventType, origin = expectedAccountId, amount = expectedAmount)
        val expectedBalances = setOf(Balance(expectedAccountId, 0))
        val expectedSaveAccount = Account(expectedAccountId, 0)

        Mockito.`when`(accountRepository.findById(expectedAccountId)).thenReturn(existingAccount)

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        Mockito.verify(accountRepository).findById(expectedAccountId)
        Mockito.verify(accountRepository).save(expectedSaveAccount)
        assertEquals(expectedBalances, balances)
    }

    @Test
    fun `withdraw from a invalid account`() {
        //arrange
        val requestEvent = Event(type = expectedEventType, origin = expectedAccountId, amount = expectedAmount)

        Mockito.`when`(accountRepository.findById(expectedAccountId)).thenReturn(null)

        //act
        assertFailsWith(
            AccountNotFoundException::class,
            expectedNotFoundMessage
        ) { eventRun.execute(requestEvent) }

        //assert
        Mockito.verify(accountRepository).findById(expectedAccountId)
    }

    @Test
    fun `withdraw with null or empty origin`() {
        //arrange
        val requestEvent = Event(type = expectedEventType, amount = expectedAmount)
        val expectedBalances = setOf<Balance>()

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        assertEquals(expectedBalances, balances)
    }

    @Test
    fun `withdraw with amount is zero`() {
        //arrange
        val existingAccount = Account(expectedAccountId, expectedAmount)
        val requestEvent = Event(type = expectedEventType, origin = expectedAccountId, amount = 0)
        val expectedBalances = setOf(Balance(expectedAccountId, expectedAmount))

        Mockito.`when`(accountRepository.findById(expectedAccountId)).thenReturn(existingAccount)

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        Mockito.verify(accountRepository).findById(expectedAccountId)
        assertEquals(expectedBalances, balances)
    }
}