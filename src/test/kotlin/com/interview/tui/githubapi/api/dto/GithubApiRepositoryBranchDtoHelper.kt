package com.interview.tui.githubapi.api.dto

import com.interview.tui.githubapi.service.dto.response.RepositoryBranchDto
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals

object GithubApiRepositoryBranchDtoHelper {

    val FLUX_SIZE = 10

    fun generateGithubApiBranchDto(): GithubApiBranchDto {
        return GithubApiBranchDto(
            name = "master_${Random.nextInt(10)}",
            commit = GithubApiLastCommitDto(
                sha = UUID.randomUUID().toString()
            )
        )
    }

    fun generateGithubBranchCollection(): List<GithubApiBranchDto> {
        return buildList<GithubApiBranchDto>{
            for (i in 1..FLUX_SIZE) add(generateGithubApiBranchDto())
        }
    }

    fun compareBranches(githubApiBranchDto: GithubApiBranchDto, repositoryBranchDto: RepositoryBranchDto) {
        assertEquals(githubApiBranchDto.commit.sha, repositoryBranchDto.lastCommitSha)
        assertEquals(githubApiBranchDto.name, repositoryBranchDto.branchName)
    }
}