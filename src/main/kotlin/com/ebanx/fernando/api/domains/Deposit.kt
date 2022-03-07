package com.ebanx.fernando.api.domains

import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import com.ebanx.fernando.api.repositories.AccountRepository
import com.ebanx.fernando.api.repositories.models.Account
import kotlin.math.abs
import org.springframework.stereotype.Component

@Component
class Deposit(val accountRepository: AccountRepository) : EventRun, EventCheck {

    override fun getType() = EventType.DEPOSIT

    override fun execute(event: Event): Set<Balance>? {
        return deposit(event.destination, event.amount)
    }

    private fun deposit(accountId: String?, amount: Int): Set<Balance> {
        return if (!accountId.isNullOrEmpty()) {
            val account = accountRepository.findById(accountId) ?: Account(accountId, 0)
            updateIfAmountNotZero(account, amount)
            setOf(account.let { Balance(it.id, it.amount) })

        } else setOf()

    }

    private fun updateIfAmountNotZero(account: Account, amount: Int) {
        if (abs(amount) > 0) {
            account.amount += abs(amount)
            accountRepository.save(account)
        }
    }

}