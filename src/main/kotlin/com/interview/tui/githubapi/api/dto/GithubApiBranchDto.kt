package com.interview.tui.githubapi.api.dto

data class GithubApiBranchDto(
    val name: String,
    val commit: GithubApiLastCommitDto
)