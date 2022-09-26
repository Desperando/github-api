package com.interview.tui.githubapi.error.dto

data class ApplicationErrorResponse(
    val status: Int,
    val message: String
) {
}