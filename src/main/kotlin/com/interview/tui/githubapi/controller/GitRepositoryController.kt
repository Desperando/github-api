package com.interview.tui.githubapi.controller

import com.interview.tui.githubapi.service.GitService
import com.interview.tui.githubapi.service.dto.request.ForkFilter
import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("repositories")
class GitRepositoryController(
    private val gitService: GitService
) {

    @GetMapping(value = ["{username}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findGitReposForUser(
        @PathVariable username: String,
        @RequestParam(value = "forked", required = false, defaultValue = "NOT_FORKED") forkFilter: ForkFilter
    ): Flux<RepositoryDto> {
        return gitService.findRepositoriesForUser(username, forkFilter)
    }
}