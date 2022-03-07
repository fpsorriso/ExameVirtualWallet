package com.ebanx.fernando.api.domains

import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import com.ebanx.fernando.api.repositories.AccountRepository
import com.ebanx.fernando.api.repositories.models.Account
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions


internal class DepositTest {

    private val expectedEventType = EventType.DEPOSIT

    private val expectedAccountId = "100"

    private val expectedAmount = 10

    private val accountRepository = mock(AccountRepository::class.java)

    private val eventRun = Deposit(accountRepository)

    @AfterEach
    internal fun tearDown() {
        verifyNoMoreInteractions(accountRepository)
    }

    @Test
    fun `deposit in new account`() {
        //arrange
        val requestEvent = Event(type = expectedEventType, destination = expectedAccountId, amount = expectedAmount)
        val expectedBalances = setOf(Balance(expectedAccountId, expectedAmount))
        val expectedSaveAccount = Account(expectedAccountId, expectedAmount)

        `when`(accountRepository.findById(expectedAccountId)).thenReturn(null)

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        verify(accountRepository).findById(expectedAccountId)
        verify(accountRepository).save(expectedSaveAccount)
        assertEquals(expectedBalances, balances)
    }

    @Test
    fun `deposit in existing account`() {
        //arrange
        val existingAccount = Account(expectedAccountId, expectedAmount)
        val requestEvent = Event(type = expectedEventType, destination = expectedAccountId, amount = expectedAmount)
        val expectedBalances = setOf(Balance(expectedAccountId, expectedAmount.plus(expectedAmount)))
        val expectedSaveAccount = Account(expectedAccountId, expectedAmount.plus(expectedAmount))

        `when`(accountRepository.findById(expectedAccountId)).thenReturn(existingAccount)

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        verify(accountRepository).findById(expectedAccountId)
        verify(accountRepository).save(expectedSaveAccount)
        assertEquals(expectedBalances, balances)
    }

    @Test
    fun `deposit with null or empty destination`() {
        //arrange
        val requestEvent = Event(type = expectedEventType, amount = expectedAmount)
        val expectedBalances = setOf<Balance>()

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        assertEquals(expectedBalances, balances)
    }

    @Test
    fun `deposit with amount is zero`() {
        //arrange
        val existingAccount = Account(expectedAccountId, expectedAmount)
        val requestEvent = Event(type = expectedEventType, destination = expectedAccountId, amount = 0)
        val expectedBalances = setOf(Balance(expectedAccountId, expectedAmount))

        `when`(accountRepository.findById(expectedAccountId)).thenReturn(existingAccount)

        //act
        val balances = eventRun.execute(requestEvent)

        //assert
        verify(accountRepository).findById(expectedAccountId)
        assertEquals(expectedBalances, balances)
    }
}