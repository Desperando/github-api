package com.interview.tui.githubapi.service.dto

import com.interview.tui.githubapi.service.dto.response.RepositoryBranchDto
import java.util.*
import kotlin.random.Random

object RepositoryBranchDtoHelper {
    fun loadRepositoryBranchDto(): RepositoryBranchDto {
        return RepositoryBranchDto(
            branchName = "master_${Random.nextInt(500)}",
            lastCommitSha = UUID.randomUUID().toString()
        )
    }
}