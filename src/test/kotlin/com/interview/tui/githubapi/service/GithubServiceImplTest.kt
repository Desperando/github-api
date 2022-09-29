package com.interview.tui.githubapi.service

import com.interview.tui.githubapi.api.client.GithubApiClient
import com.interview.tui.githubapi.api.dto.GithubApiRepositoryBranchDtoHelper
import com.interview.tui.githubapi.api.dto.GithubApiRepositoryDtoHelper
import com.interview.tui.githubapi.api.dto.GithubApiRepositoryDtoHelper.FLUX_SIZE
import com.interview.tui.githubapi.service.dto.request.ForkFilter
import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import com.interview.tui.githubapi.service.mapper.GithubApiDtoMapperImpl
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [GithubServiceImpl::class, GithubApiDtoMapperImpl::class])
class GithubServiceImplTest {

    @Autowired
    private lateinit var githubService: GithubServiceImpl

    @MockBean
    private lateinit var githubApiClient: GithubApiClient

    @SpyBean
    private lateinit var mapperSpy: GithubApiDtoMapperImpl

    @ParameterizedTest
    @CsvSource(value = ["ALL, 10", "FORKED, 5", "NOT_FORKED, 5"])
    fun findRepositoriesForUser_existedUsernameGiven_returnFluxOfRepositoriesWithBranches(
        forkFilter: ForkFilter, repositoryExpected: Int
    ) {
        val username = "TestName"
        val apiRepos = GithubApiRepositoryDtoHelper.generateRepositoriesWithDifferentForks()
        val apiBranches = GithubApiRepositoryBranchDtoHelper.generateGithubBranchCollection()
        val repoFlux = Flux.fromIterable(apiRepos)
        val branchFlux = Flux.fromIterable(apiBranches)

        whenever(githubApiClient.getRepositoriesForUser(username, 1))
            .thenReturn(repoFlux)
        whenever(githubApiClient.getRepositoriesForUser(eq(username), argThat { a -> a != 1 }))
            .thenReturn(Flux.empty())
        whenever(githubApiClient.getBranchesForUserAndRepo(eq(username), any(), eq(1)))
            .thenReturn(branchFlux)
        whenever(githubApiClient.getBranchesForUserAndRepo(eq(username), any(), argThat { a -> a != 1 }))
            .thenReturn(Flux.empty())

        val repoDtos = ArrayList<RepositoryDto>()
        StepVerifier.create(githubService.findRepositoriesForUser(username, forkFilter))
            .expectSubscription()
            .thenRequest(Long.MAX_VALUE)
            .recordWith { repoDtos }
            .expectNextCount(repositoryExpected.toLong())
            .expectComplete()
            .verifyThenAssertThat()

        assertEquals(repositoryExpected, repoDtos.size)
        repoDtos.stream()
            .forEach { }
        verify(githubApiClient, times(2))
            .getRepositoriesForUser(eq(username), anyInt())
        verify(githubApiClient, times((repositoryExpected * 2)))
            .getBranchesForUserAndRepo(eq(username), any(), anyInt())
        verify(mapperSpy, times(repositoryExpected)).mapRepositoryApiToResponse(any())
        verify(mapperSpy, times(repositoryExpected * FLUX_SIZE)).mapBranchApiToResponse(any())
    }
}