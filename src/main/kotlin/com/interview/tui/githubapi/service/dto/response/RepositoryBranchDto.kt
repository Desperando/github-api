package com.interview.tui.githubapi.service.dto.response

data class RepositoryBranchDto(
    val branchName: String,
    val lastCommitSha: String
)