package com.interview.tui.githubapi.service

import com.interview.tui.githubapi.service.dto.request.ForkFilter
import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import reactor.core.publisher.Flux

interface GitService {
    fun findRepositoriesForUser(username: String, forkFilter: ForkFilter): Flux<RepositoryDto>
}