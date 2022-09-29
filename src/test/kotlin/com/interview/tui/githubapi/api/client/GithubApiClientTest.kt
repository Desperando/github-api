package com.interview.tui.githubapi.api.client

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.interview.tui.githubapi.api.error.exception.ApiClientException
import com.interview.tui.githubapi.api.error.exception.UserNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.YamlMapFactoryBean
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(
    classes = [
        GithubApiClientTest.TestWireMockConfiguration::class
    ]
)
class GithubApiClientTest {

    @Autowired
    private lateinit var server: WireMockServer

    @Autowired
    private lateinit var githubApiClient: GithubApiClient

    @Autowired
    private lateinit var wireMockConfiguration: TestWireMockConfiguration

    @BeforeEach
    fun handleBeforeEach() {
        resetAllRequests()
    }

    @Test
    fun getRepositoriesForUser_whenAllDataForRequestIsProvided_thenReturnFluxOfRepositories() {
        GithubApiClientHelper.okStub()

        val apiRepositories = githubApiClient.getRepositoriesForUser(
            GithubApiClientHelper.USERNAME, GithubApiClientHelper.PAGE_NUMBER
        )

        StepVerifier.create(apiRepositories)
            .expectNextCount(GithubApiClientHelper.EXPECTED_REPOSITORY_AMOUNT)
            .expectComplete()
            .verify()

        verifyApiCall(GithubApiClientHelper.workingRepoUri())
    }

    @Test
    fun getRepositoriesForUser_whenNotExistedUsernameRequested_thenThrowUserNotFoundException() {
        GithubApiClientHelper.notFound()

        assertThrows<UserNotFoundException> {
            githubApiClient
                .getRepositoriesForUser(GithubApiClientHelper.USERNAME, GithubApiClientHelper.PAGE_NUMBER)
                .blockFirst()
        }

        verifyApiCall(GithubApiClientHelper.workingRepoUri())
    }

    @Test
    fun getRepositoriesForUser_whenRequestIsForbidden_thenThrowApiClientException() {
        GithubApiClientHelper.forbidden()

        assertThrows<ApiClientException> {
            githubApiClient
                .getRepositoriesForUser(GithubApiClientHelper.USERNAME, GithubApiClientHelper.PAGE_NUMBER)
                .blockFirst()
        }
        verifyApiCall(GithubApiClientHelper.workingRepoUri())
    }

    @Test
    fun getBranchesForUserAndRepo_whenAllDataForRequestIsProvided_thenReturnFluxOfRepositories() {
        GithubApiClientHelper.okStub()

        val apiBranches = githubApiClient.getBranchesForUserAndRepo(
            GithubApiClientHelper.USERNAME,
            GithubApiClientHelper.REPOSITORY_NAME, GithubApiClientHelper.PAGE_NUMBER
        )

        StepVerifier.create(apiBranches)
            .expectNextCount(GithubApiClientHelper.EXPECTED_BRANCHES_AMOUNT)
            .expectComplete()
            .verify()

        verifyApiCall(GithubApiClientHelper.workingBranchUri())
    }

    @Test
    fun getBranchesForUserAndRepo_whenNotExistedUsernameOrRepoRequested_thenReturnEmptyFlux() {
        GithubApiClientHelper.notFound()


        StepVerifier.create(
            githubApiClient
                .getBranchesForUserAndRepo(
                    GithubApiClientHelper.USERNAME, GithubApiClientHelper.REPOSITORY_NAME,
                    GithubApiClientHelper.PAGE_NUMBER
                )
        )
            .expectNextCount(0)
            .expectComplete()
            .verify()


        verifyApiCall(GithubApiClientHelper.workingBranchUri())
    }

    @Test
    fun getBranchesForUserAndRepo_whenRequestIsForbidden_thenThrowApiClientException() {
        GithubApiClientHelper.forbidden()

        assertThrows<ApiClientException> {
            githubApiClient.getBranchesForUserAndRepo(
                GithubApiClientHelper.USERNAME,
                GithubApiClientHelper.REPOSITORY_NAME, GithubApiClientHelper.PAGE_NUMBER
            ).blockFirst()
        }
        verifyApiCall(GithubApiClientHelper.workingBranchUri())
    }

    private fun verifyApiCall(urlPath: String) {
        server.verify(
            exactly(1), getRequestedFor(urlPathEqualTo(urlPath))
                .withHeader(HttpHeaders.ACCEPT, equalTo(GithubApiClient.GITHUB_API_ACCEPT_HEADER))
                .withQueryParam(
                    GithubApiClient.PAGE_LIMIT_PARAMETER_NAME,
                    equalTo(GithubApiClient.PAGE_LIMIT_PARAMETER_VALUE.toString())
                )
                .withQueryParam(
                    GithubApiClient.PAGE_NUMBER_PARAMETER_NAME,
                    equalTo(GithubApiClientHelper.PAGE_NUMBER.toString())
                )
        )
    }

    @TestConfiguration
    @ComponentScan("com.interview.tui.githubapi.api")
    class TestWireMockConfiguration {

        @Value("\${wiremock.port}")
        private lateinit var port: String

        @Bean
        fun wireMockServer(): WireMockServer {
            val server = WireMockServer(
                WireMockConfiguration.options()
                    .port(port.toInt())
            )
            server.start()
            configureFor(port.toInt())
            return server
        }
    }
}