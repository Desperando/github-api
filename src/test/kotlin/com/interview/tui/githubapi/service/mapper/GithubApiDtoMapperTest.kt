package com.interview.tui.githubapi.service.mapper

import com.interview.tui.githubapi.service.dto.api.github.GithubApiBranchDto
import com.interview.tui.githubapi.service.dto.api.github.GithubApiRepositoryBranchDtoHelper
import com.interview.tui.githubapi.service.dto.api.github.GithubApiRepositoryDtoHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [GithubApiDtoMapperImpl::class])
class GithubApiDtoMapperTest {

    @Autowired
    private lateinit var githubApiDtoMapper: GithubApiDtoMapper

    @Test
    fun mapRepositoryApiToResponse_apiRepositoryGiven_shouldReturnRepositoryDto() {
        val apiRepository = GithubApiRepositoryDtoHelper.generateRepositoryDto()
        val repositoryDto = githubApiDtoMapper.mapRepositoryApiToResponse(apiRepository)
        GithubApiRepositoryDtoHelper.compareWithResponse(apiRepository, repositoryDto)
    }

    @Test
    fun mapBranchApiToResponse_apiBranchGiven_shouldReturnRepositoryBranchDto() {
        val apiBranch = GithubApiRepositoryBranchDtoHelper.generateGithubApiBranchDto()
        val repositoryDto = githubApiDtoMapper.mapBranchApiToResponse(apiBranch)
        GithubApiRepositoryBranchDtoHelper.compareBranches(apiBranch, repositoryDto)
    }
}