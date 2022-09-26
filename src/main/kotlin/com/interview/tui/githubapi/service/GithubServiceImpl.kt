package com.interview.tui.githubapi.service

import com.interview.tui.githubapi.service.dto.api.github.GithubApiBranchDto
import com.interview.tui.githubapi.service.dto.api.github.GithubApiRepositoryDto
import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import com.interview.tui.githubapi.service.mapper.GithubApiDtoMapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Signal

@Service
class GithubServiceImpl(
    private val githubApiClient: GithubApiClient,
    private val apiResponseMapper: GithubApiDtoMapper
) : GitService {

    val startPage = 1

    override fun findRepositoriesForUser(username: String): Flux<RepositoryDto> {
        return fetchPagesForRepository(username, startPage)
            .filter { !it.fork }
            .map {
                apiResponseMapper.mapRepositoryApiToResponse(it)
            }.flatMap { repository ->
                fetchPagesForBranch(username, repository, startPage)
                    .map { apiResponseMapper.mapBranchApiToResponse(it) }
                    .map {
                        repository.branches.add(it)
                        repository
                    }
            }
    }

    fun fetchPagesForRepository(username: String, pageNumber: Int): Flux<GithubApiRepositoryDto> {
        return githubApiClient
            .getRepositoriesForUser(username, pageNumber)
            .switchOnFirst { signal: Signal<out GithubApiRepositoryDto>, source: Flux<GithubApiRepositoryDto> ->
                if (signal.hasValue()) {
                    fetchPagesForRepository(username, (pageNumber + 1))
                        .mergeWith(source)
                } else {
                    Flux.from(source)
                }
            }
    }

    fun fetchPagesForBranch(username: String, repository: RepositoryDto, pageNumber: Int): Flux<GithubApiBranchDto> {
        return githubApiClient
            .getBranchesForUserAndRepo(username, repository.repositoryName, pageNumber)
            .switchOnFirst { signal: Signal<out GithubApiBranchDto>, source: Flux<GithubApiBranchDto> ->
                if (signal.hasValue()) {
                    fetchPagesForBranch(username, repository, (pageNumber + 1))
                        .mergeWith(source)
                } else {
                    Flux.from(source)
                }
            }
    }
}