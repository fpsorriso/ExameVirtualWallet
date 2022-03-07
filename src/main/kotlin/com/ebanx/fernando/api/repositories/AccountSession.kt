package com.ebanx.fernando.api.repositories

import com.ebanx.fernando.api.repositories.models.Account
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Repository

@Repository
@Scope("singleton")
final class AccountSession : AccountRepository {
    private val accounts = mutableSetOf<Account>()

    override fun findById(id: String): Account? {
        return accounts.firstOrNull { it.id.equals(other = id, ignoreCase = true) }
    }

    override fun save(account: Account): Account {
        if (accounts.indexOf(account) < 0) {
            accounts.add(account)
        } else {
            accounts.first { it.id.equals(other = account.id, ignoreCase = true) }.apply { amount = account.amount }
        }

        return account
    }

    override fun removeAll() {
        accounts.clear()
    }
}