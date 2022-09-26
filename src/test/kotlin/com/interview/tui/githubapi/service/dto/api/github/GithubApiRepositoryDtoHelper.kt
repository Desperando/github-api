package com.interview.tui.githubapi.service.dto.api.github

import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import reactor.core.publisher.Flux
import kotlin.random.Random
import kotlin.test.assertEquals

object GithubApiRepositoryDtoHelper {
    val FLUX_SIZE = 10

    fun generateRepositoryDto() : GithubApiRepositoryDto {
        return GithubApiRepositoryDto(
            id = Random.nextInt(100),
            name = "Repository_Name_${Random.nextInt(10)}",
            owner = GithubApiOwnerDto(
                id = Random.nextInt(100),
                login = "owner123"
            ),
            fork = false
        )
    }

    fun generateFlux() : Flux<GithubApiRepositoryDto> {
        return Flux.range(0, FLUX_SIZE)
            .map { generateRepositoryDto() }
    }

    fun compareWithResponse(githubApiRepo: GithubApiRepositoryDto, repoResponse: RepositoryDto) {
        assertEquals(githubApiRepo.name, repoResponse.repositoryName)
        assertEquals(githubApiRepo.owner.login, repoResponse.login)
    }
}