package com.interview.tui.githubapi.service.dto.api.github

data class GithubApiBranchDto(
    val name: String,
    val commit: GithubApiLastCommitDto
)