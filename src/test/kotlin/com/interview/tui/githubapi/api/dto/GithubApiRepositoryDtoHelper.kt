package com.interview.tui.githubapi.api.dto

import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import kotlin.random.Random
import kotlin.test.assertEquals

object GithubApiRepositoryDtoHelper {

    val FLUX_SIZE = 10

    fun generateRepositoryDto(): GithubApiRepositoryDto {
        return generateRepositoryDto(false)
    }

    fun generateRepositoryDto(forked: Boolean): GithubApiRepositoryDto {
        return GithubApiRepositoryDto(
            id = Random.nextInt(100),
            name = "Repository_Name_${Random.nextInt(10)}",
            owner = GithubApiOwnerDto(
                id = Random.nextInt(100),
                login = "owner123"
            ),
            fork = forked
        )
    }

    fun generateRepositoriesWithDifferentForks(): List<GithubApiRepositoryDto> {
        return (1..FLUX_SIZE)
            .map { it % 2 == 0 }
            .map { generateRepositoryDto(it) }
    }

    fun compareWithResponse(githubApiRepo: GithubApiRepositoryDto, repoResponse: RepositoryDto) {
        assertEquals(githubApiRepo.name, repoResponse.repositoryName)
        assertEquals(githubApiRepo.owner.login, repoResponse.login)
    }
}