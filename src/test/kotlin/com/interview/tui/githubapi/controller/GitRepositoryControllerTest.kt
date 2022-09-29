package com.interview.tui.githubapi.controller

import com.interview.tui.githubapi.api.error.exception.UserNotFoundException
import com.interview.tui.githubapi.api.error.handler.ClientApiErrorHandler
import com.interview.tui.githubapi.config.ApplicationSecurityConfiguration
import com.interview.tui.githubapi.error.dto.ApplicationErrorResponse
import com.interview.tui.githubapi.service.GitService
import com.interview.tui.githubapi.service.dto.RepositoryDtoHelper
import com.interview.tui.githubapi.service.dto.request.ForkFilter
import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.NullSource
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@WebFluxTest(GitRepositoryController::class)
@Import(
    value = [
        ApplicationSecurityConfiguration::class,
        GitRepositoryController::class,
        GitRepositoryControllerTest.WebTestConfiguration::class
    ]
)
@ComponentScan("com.interview.tui.githubapi.error")
class GitRepositoryControllerTest {

    val username = "DEFAULT_NAME"

    @MockBean
    private lateinit var gitService: GitService

    @Autowired
    private lateinit var testClient: WebTestClient

    @Test
    @WithAnonymousUser
    fun findGitReposForUser_usernameGiven_shouldReturnFluxResponse() {
        val mockResponse = Flux.just(RepositoryDtoHelper.generateRepositoryResponseDto())
        whenever(gitService.findRepositoriesForUser(eq(username), any()))
            .thenReturn(mockResponse)
        testClient.get().uri("/repositories/{username}", username)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(RepositoryDto::class.java)
    }

    @ParameterizedTest
    @NullSource
    @EnumSource(ForkFilter::class)
    @WithAnonymousUser
    fun findGitReposForUser_filterParamGiven_shouldPassProperFilterParamIntoService(forkFilter: ForkFilter?) {
        val mockResponse = Flux.just(RepositoryDtoHelper.generateRepositoryResponseDto())
        val expectedForkFilter = forkFilter ?: ForkFilter.NOT_FORKED
        whenever(gitService.findRepositoriesForUser(eq(username), any()))
            .thenAnswer {
                assertEquals(expectedForkFilter, it.arguments[1])
                mockResponse
            }
        testClient.get().uri { ub ->
            ub.path("/repositories/{username}")
            forkFilter?.let { ub.queryParam("forked", forkFilter.name) }
            ub.build(username)
        }
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(RepositoryDto::class.java)
    }

    @Test
    @WithAnonymousUser
    fun findGitReposForUser_notExistedUsernameGiven_shouldReturnNotFoundWithMappedError() {
        whenever(gitService.findRepositoriesForUser(eq(username), any()))
            .thenThrow(UserNotFoundException(username = username))
        testClient.get().uri("/repositories/{username}", username)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isNotFound
            .expectBody(ApplicationErrorResponse::class.java)
    }

    @Test
    @WithAnonymousUser
    fun findGitReposForUser_wrongAcceptHeaderGiven_shouldReturnNotAcceptableWithMappedError() {
        testClient.get().uri("/repositories/{username}", username)
            .accept(MediaType.APPLICATION_XML)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE.value())
            .expectBody(ApplicationErrorResponse::class.java)
    }

    @TestConfiguration
    class WebTestConfiguration(
        val gitRepositoryController: GitRepositoryController,
        val clientApiErrorHandler: ClientApiErrorHandler,
    ) {
        @Bean
        fun testClient(): WebTestClient {
            return WebTestClient.bindToController(gitRepositoryController)
                .controllerAdvice(clientApiErrorHandler)
                .build()
        }
    }
}