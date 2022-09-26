package com.interview.tui.githubapi.service.dto.api.github;

data class GithubApiRepositoryDto(
    val id: Int,
    val name: String,
    val owner: GithubApiOwnerDto,
    val fork: Boolean
)

