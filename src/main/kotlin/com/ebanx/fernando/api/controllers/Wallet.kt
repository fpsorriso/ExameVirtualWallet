package com.ebanx.fernando.api.controllers

import com.ebanx.fernando.api.dtos.events.Event
import com.ebanx.fernando.api.dtos.events.EventResponse
import com.ebanx.fernando.api.services.WalletService
import javax.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Wallet(val service: WalletService) {

    @PostMapping(
        path = ["/reset"]
    )
    fun reset(): ResponseEntity<String> {
        service.reset()
        return ResponseEntity.ok("OK")
    }

    @PostMapping(
        path = ["/event"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun sendEvent(@RequestBody payload: Event): ResponseEntity<EventResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.sendEvent(payload))
    }

    @GetMapping(path = ["/balance"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBalance(@NotBlank @RequestParam("account_id") accountId: String): ResponseEntity<Int> {
        return ResponseEntity.ok(service.getBalanceByAccountId(accountId))
    }
}