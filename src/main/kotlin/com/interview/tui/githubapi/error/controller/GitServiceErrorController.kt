package com.interview.tui.githubapi.error.controller

import com.interview.tui.githubapi.error.dto.ApplicationErrorResponse
import com.interview.tui.githubapi.error.exception.GithubApiClientException
import com.interview.tui.githubapi.error.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GitServiceErrorController {
    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: UserNotFoundException): ApplicationErrorResponse {
        return ApplicationErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = exception.message
        )
    }

    @ExceptionHandler(GithubApiClientException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNotFound(exception: GithubApiClientException): ApplicationErrorResponse {
        return ApplicationErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = exception.message.orEmpty()
        )
    }
}