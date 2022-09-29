package com.interview.tui.githubapi.service.dto

import com.interview.tui.githubapi.service.dto.response.RepositoryDto

object RepositoryDtoHelper {
    fun generateRepositoryResponseDto() : RepositoryDto {
        return RepositoryDto(
            login =  "owner123",
            repositoryName = "Repository"
        )
    }
}