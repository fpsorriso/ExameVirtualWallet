package com.ebanx.fernando.api.services

import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventResponse
import com.ebanx.fernando.api.exceptions.AccountNotFoundException
import com.ebanx.fernando.api.exceptions.BalancesNotFoundException
import com.ebanx.fernando.api.repositories.AccountRepository
import org.springframework.stereotype.Service

@Service
class WalletServiceImpl(val accountRepository: AccountRepository, val eventBus: EventBus) : WalletService {

    override fun reset() {
        accountRepository.removeAll()
    }

    override fun sendEvent(event: Event): EventResponse {
        return eventBus.createBy(event, eventBus.execute(event) ?: setOf()) ?: throw BalancesNotFoundException()
    }

    override fun getBalanceByAccountId(accountId: String): Int {
        return accountRepository.findById(accountId)?.amount ?: throw AccountNotFoundException(accountId)
    }
}