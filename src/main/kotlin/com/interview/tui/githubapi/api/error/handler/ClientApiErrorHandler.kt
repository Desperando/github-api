package com.interview.tui.githubapi.api.error.handler

import com.interview.tui.githubapi.api.error.exception.ApiClientException
import com.interview.tui.githubapi.api.error.exception.UserNotFoundException
import com.interview.tui.githubapi.error.dto.ApplicationErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ClientApiErrorHandler {
    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: UserNotFoundException): ApplicationErrorResponse {
        return ApplicationErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = exception.message
        )
    }

    @ExceptionHandler(ApiClientException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNotFound(exception: ApiClientException): ApplicationErrorResponse {
        return ApplicationErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = exception.message.orEmpty()
        )
    }
}