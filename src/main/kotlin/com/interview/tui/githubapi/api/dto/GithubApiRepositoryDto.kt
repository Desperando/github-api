package com.interview.tui.githubapi.api.dto;

data class GithubApiRepositoryDto(
    val id: Int,
    val name: String,
    val owner: GithubApiOwnerDto,
    val fork: Boolean
)

