package com.interview.tui.githubapi.service.mapper

import com.interview.tui.githubapi.service.dto.api.github.GithubApiBranchDto
import com.interview.tui.githubapi.service.dto.api.github.GithubApiRepositoryDto
import com.interview.tui.githubapi.service.dto.response.RepositoryBranchDto
import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface GithubApiDtoMapper {

    @Mapping(target = "repositoryName", source = "name")
    @Mapping(target = "login", source = "owner.login")
    @Mapping(target = "branches", ignore = true)
    fun mapRepositoryApiToResponse(apiRepository: GithubApiRepositoryDto): RepositoryDto

    @Mapping(target = "lastCommitSha", source = "commit.sha")
    @Mapping(target = "branchName", source = "name")
    fun mapBranchApiToResponse(apiBranch: GithubApiBranchDto): RepositoryBranchDto
}