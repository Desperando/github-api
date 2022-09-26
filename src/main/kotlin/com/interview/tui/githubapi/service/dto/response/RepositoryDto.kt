package com.interview.tui.githubapi.service.dto.response

data class RepositoryDto(
    val login: String,
    val repositoryName: String
) {
    val branches: MutableList<RepositoryBranchDto> = mutableListOf()
}