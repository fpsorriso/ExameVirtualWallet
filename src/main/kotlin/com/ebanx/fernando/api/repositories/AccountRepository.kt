package com.ebanx.fernando.api.repositories

import com.ebanx.fernando.api.repositories.models.Account

interface AccountRepository  {
    fun findById(id: String): Account?
    fun save(account: Account): Account
    fun removeAll()
}