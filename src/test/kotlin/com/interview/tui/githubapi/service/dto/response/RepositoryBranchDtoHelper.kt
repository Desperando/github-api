package com.interview.tui.githubapi.service.dto.response

import java.util.UUID
import kotlin.random.Random

object RepositoryBranchDtoHelper {
    fun loadRepositoryBranchDto(): RepositoryBranchDto {
        return RepositoryBranchDto(
            branchName = "master_${Random.nextInt(500)}",
            lastCommitSha = UUID.randomUUID().toString()
        )
    }
}