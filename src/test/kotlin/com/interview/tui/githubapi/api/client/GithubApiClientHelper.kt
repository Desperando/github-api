package com.interview.tui.githubapi.api.client

import com.github.tomakehurst.wiremock.client.WireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.util.UriComponentsBuilder

object GithubApiClientHelper {

    const val USERNAME = "username"
    const val REPOSITORY_NAME = "repository_name"
    const val PAGE_NUMBER = 1

    const val EXPECTED_REPOSITORY_AMOUNT = 4L
    const val EXPECTED_BRANCHES_AMOUNT = 3L

    private const val RESPONSE_PREFIX = "responses"
    private const val OK_REPO_RESPONSE_BODY = "$RESPONSE_PREFIX/api-github-repository-response.json"
    private const val OK_BRANCH_RESPONSE_BODY = "$RESPONSE_PREFIX/api-github-branch-response.json"
    private const val NOT_FOUND_RESPONSE_BODY = "$RESPONSE_PREFIX/api-github-not-found-response.json"
    private const val FORBIDDEN_RESPONSE_BODY = "$RESPONSE_PREFIX/api-github-forbidden-response.json"


    fun okStub() {
        applyStub(
            workingRepoUri(), HttpStatus.OK.value(),
            OK_REPO_RESPONSE_BODY
        )
        applyStub(
            workingBranchUri(), HttpStatus.OK.value(),
            OK_BRANCH_RESPONSE_BODY
        )
    }

    fun notFound() {
        applyStub(
            workingRepoUri(), HttpStatus.NOT_FOUND.value(),
            NOT_FOUND_RESPONSE_BODY
        )
        applyStub(
            workingBranchUri(), HttpStatus.NOT_FOUND.value(),
            NOT_FOUND_RESPONSE_BODY
        )
    }

    fun forbidden() {
        applyStub(
            workingRepoUri(), HttpStatus.FORBIDDEN.value(),
            FORBIDDEN_RESPONSE_BODY
        )
        applyStub(
            workingBranchUri(), HttpStatus.FORBIDDEN.value(),
            FORBIDDEN_RESPONSE_BODY
        )
    }

    fun workingRepoUri(): String {
        return createTestUri(GithubApiClient.REPOSITORIES_FOR_USER_URL, USERNAME)
    }

    fun workingBranchUri(): String {
        return createTestUri(
                GithubApiClient.BRACNHES_FOR_USER_REPO_URL,
                USERNAME,
                REPOSITORY_NAME
            )
    }

    private fun createTestUri(uriPath: String, vararg args: Any): String {
        return UriComponentsBuilder
            .fromUriString(uriPath)
            .build(*args)
            .path
    }

    private fun applyStub(requestUrl: String, responseStatus: Int, responseBodyFilePath: String) {
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo(requestUrl))
                .withQueryParam(
                    GithubApiClient.PAGE_NUMBER_PARAMETER_NAME,
                    WireMock.matching("\\d+").or(WireMock.absent())
                )
                .withQueryParam(
                    GithubApiClient.PAGE_LIMIT_PARAMETER_NAME,
                    WireMock.matching("\\d+").or(WireMock.absent())
                )
                .willReturn(
                    WireMock.ok()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(responseStatus)
                        .withBodyFile(responseBodyFilePath)
                )
        )
    }
}