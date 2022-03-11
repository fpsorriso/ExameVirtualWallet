package com.ebanx.fernando.api.domains

import com.ebanx.fernando.api.dtos.Balance
import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventType
import com.ebanx.fernando.api.exceptions.AccountNotFoundException
import com.ebanx.fernando.api.exceptions.LimitExceededException
import com.ebanx.fernando.api.repositories.AccountRepository
import com.ebanx.fernando.api.repositories.models.Account
import kotlin.math.abs
import org.springframework.stereotype.Component

@Component
class Withdraw(val accountRepository: AccountRepository) : EventRun, EventCheck {

    private val maxLimit = -100;

    override fun getType() = EventType.WITHDRAW

    override fun execute(event: Event): Set<Balance>? {
        return withdraw(event.origin, event.amount)
    }

    private fun withdraw(accountId: String?, amount: Int): Set<Balance> {
        return if (!accountId.isNullOrEmpty()) {
            val account = accountRepository.findById(accountId) ?: throw AccountNotFoundException(accountId)
            validateLimit(account, amount)
            updateIfAmountNotZero(account, amount)
            setOf(account.let { Balance(it.id, it.amount) })

        } else setOf()
    }

    private fun updateIfAmountNotZero(account: Account, amount: Int) {
        if (abs(amount) > 0) {
            account.amount -= abs(amount)
            accountRepository.save(account)
        }
    }

    private fun validateLimit(account: Account, amount: Int) {
        if (maxLimit > (account.amount - amount)) {
            throw LimitExceededException()
        }
    }
}