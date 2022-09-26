package com.interview.tui.githubapi.service

import com.interview.tui.githubapi.service.dto.api.github.GithubApiRepositoryBranchDtoHelper
import com.interview.tui.githubapi.service.dto.api.github.GithubApiRepositoryDtoHelper
import com.interview.tui.githubapi.service.mapper.GithubApiDtoMapperImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [GithubServiceImpl::class, GithubApiDtoMapperImpl::class])
class GithubServiceImplTest {

    @Autowired
    private lateinit var githubService: GithubServiceImpl

    @MockBean
    private lateinit var githubApiClient: GithubApiClient

    @Test
    fun findRepositoriesForUser_existedUsernameGiven_returnFluxOfRepositoriesWithBranches() {
        var username = "TestName"
        val repoFlux = GithubApiRepositoryDtoHelper.generateFlux()
        val branchFlux = Flux.fromIterable(GithubApiRepositoryBranchDtoHelper.generateGithubBranchCollection())
        whenever(githubApiClient.getRepositoriesForUser(username, 1))
            .thenReturn(repoFlux)
        whenever(githubApiClient.getRepositoriesForUser(eq(username), argThat { a -> a != 1 }))
            .thenReturn(Flux.empty())
        whenever(githubApiClient.getBranchesForUserAndRepo(eq(username), any(), eq(1)))
            .thenReturn(branchFlux)
        whenever(githubApiClient.getBranchesForUserAndRepo(eq(username), any(), argThat { a -> a != 1 }))
            .thenReturn(Flux.empty())
        StepVerifier.create(githubService.findRepositoriesForUser(username))
            .expectSubscription()
            .thenRequest(Long.MAX_VALUE)
            .expectNextCount(
                (GithubApiRepositoryDtoHelper.FLUX_SIZE
                        * GithubApiRepositoryBranchDtoHelper.FLUX_SIZE).toLong()
            )
            .expectComplete()
            .verify()
        verify(
            githubApiClient, times(2)
        )
            .getRepositoriesForUser(eq(username), anyInt())
        verify(githubApiClient, times(GithubApiRepositoryDtoHelper.FLUX_SIZE * 2))
            .getBranchesForUserAndRepo(org.mockito.kotlin.eq(username), any(), anyInt())
    }
}