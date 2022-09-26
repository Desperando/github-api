package com.interview.tui.githubapi.service.dto.response

object RepositoryDtoHelper {
    fun generateRepositoryResponseDto() : RepositoryDto {
        return RepositoryDto(
            login =  "owner123",
            repositoryName = "Repository"
        )
    }
}