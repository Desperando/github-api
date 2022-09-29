package com.interview.tui.githubapi.service

import com.interview.tui.githubapi.api.client.GithubApiClient
import com.interview.tui.githubapi.api.dto.GithubApiBranchDto
import com.interview.tui.githubapi.api.dto.GithubApiRepositoryDto
import com.interview.tui.githubapi.service.dto.request.ForkFilter
import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import com.interview.tui.githubapi.service.mapper.GithubApiDtoMapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Signal
import reactor.kotlin.core.publisher.toFlux
import java.util.stream.Collectors

@Service
class GithubServiceImpl(
    private val githubApiClient: GithubApiClient,
    private val apiResponseMapper: GithubApiDtoMapper
) : GitService {

    private companion object {
        val START_PAGE = 1
    }

    override fun findRepositoriesForUser(username: String, forkFilter: ForkFilter): Flux<RepositoryDto> {
        return fetchPagesForRepository(username, START_PAGE)
            .filter { ForkFilter.ALL == forkFilter || it.fork == forkFilter.forked }
            .map { apiResponseMapper.mapRepositoryApiToResponse(it) }
            .flatMap { repository ->
                fetchPagesForBranch(username, repository, START_PAGE)
                    .map { apiResponseMapper.mapBranchApiToResponse(it) }
                    .collect(Collectors.toList())
                    .map {
                        repository.branches.addAll(it)
                        repository
                    }.toFlux()
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