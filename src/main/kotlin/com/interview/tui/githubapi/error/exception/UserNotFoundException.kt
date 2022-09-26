package com.interview.tui.githubapi.error.exception

class UserNotFoundException(
    private val username: String,
    override val message: String = "User with name $username has not been found."
) : RuntimeException(message)