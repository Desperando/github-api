package com.interview.tui.githubapi.service.dto.request

enum class ForkFilter(
    val forked: Boolean?
) {
    ALL(null), FORKED(true), NOT_FORKED(false);
}