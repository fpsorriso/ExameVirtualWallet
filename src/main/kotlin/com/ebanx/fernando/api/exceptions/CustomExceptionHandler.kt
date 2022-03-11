package com.ebanx.fernando.api.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {
    private val logger = LoggerFactory.getLogger(CustomExceptionHandler::class.java)

    @ExceptionHandler(value = [AccountNotFoundException::class,
        BalancesNotFoundException::class,
        LimitExceededException::class
    ])
    fun notFoundHandler(ex: Exception, request: WebRequest): ResponseEntity<Int> {
        logger.warn("Not found values", ex)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0)
    }

}