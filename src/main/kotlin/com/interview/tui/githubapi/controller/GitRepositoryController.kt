package com.interview.tui.githubapi.controller

import com.interview.tui.githubapi.service.GitService
import com.interview.tui.githubapi.service.dto.response.RepositoryDto
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("repositories")
class GitRepositoryController(
    private val gitService: GitService
) {

    @GetMapping(value = ["{username}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findGitReposForUser(@PathVariable username: String): Flux<RepositoryDto> {
        return gitService.findRepositoriesForUser(username)
    }
}