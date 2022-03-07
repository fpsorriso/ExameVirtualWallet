package com.ebanx.fernando.api.exceptions

class AccountNotFoundException(accountId: String) : Exception("Account $accountId not found")