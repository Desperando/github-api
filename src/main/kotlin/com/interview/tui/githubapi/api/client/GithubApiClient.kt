package com.interview.tui.githubapi.api.client

import com.interview.tui.githubapi.api.dto.GithubApiBranchDto
import com.interview.tui.githubapi.api.dto.GithubApiRepositoryDto
import com.interview.tui.githubapi.api.error.exception.ApiClientException
import com.interview.tui.githubapi.api.error.exception.UserNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Component
class GithubApiClient(
    private val githubWebClient: WebClient,
) {
    internal companion object {
        //Accept Header value
        val GITHUB_API_ACCEPT_HEADER = "application/vnd.github+json"

        //Mapping URLS
        val REPOSITORIES_FOR_USER_URL = "/users/{username}/repos"
        val BRACNHES_FOR_USER_REPO_URL = "/repos/{username}/{repoName}/branches"

        //Request parameters
        val PAGE_LIMIT_PARAMETER_NAME = "per_page"
        val PAGE_LIMIT_PARAMETER_VALUE = 100
        val PAGE_NUMBER_PARAMETER_NAME = "page"
    }

    fun getRepositoriesForUser(username: String, pageNumber: Int): Flux<GithubApiRepositoryDto> {
        return githubWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path(REPOSITORIES_FOR_USER_URL)
                    .queryParam(PAGE_NUMBER_PARAMETER_NAME, pageNumber)
                    .queryParam(PAGE_LIMIT_PARAMETER_NAME, PAGE_LIMIT_PARAMETER_VALUE)
                    .build(username)
            }
            .header(HttpHeaders.ACCEPT, GITHUB_API_ACCEPT_HEADER)
            .exchangeToFlux {
                when (it.statusCode()) {
                    HttpStatus.OK -> it.bodyToFlux(GithubApiRepositoryDto::class.java)
                    HttpStatus.NOT_FOUND -> throw UserNotFoundException(username = username)
                    else -> throw ApiClientException("API returned with error code ${it.statusCode()}")
                }
            }
    }

    fun getBranchesForUserAndRepo(username: String, repoName: String, pageNumber: Int): Flux<GithubApiBranchDto> {
        return githubWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path(BRACNHES_FOR_USER_REPO_URL)
                    .queryParam(PAGE_NUMBER_PARAMETER_NAME, pageNumber)
                    .queryParam(PAGE_LIMIT_PARAMETER_NAME, PAGE_LIMIT_PARAMETER_VALUE)
                    .build(username, repoName)
            }
            .header(HttpHeaders.ACCEPT, GITHUB_API_ACCEPT_HEADER)
            .exchangeToFlux {
                when (it.statusCode()) {
                    HttpStatus.OK -> it.bodyToFlux(GithubApiBranchDto::class.java)
                    HttpStatus.NOT_FOUND -> Flux.empty()
                    else -> throw ApiClientException("API returned with error code ${it.statusCode()}")
                }
            }
    }
}