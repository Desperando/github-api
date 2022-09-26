package com.interview.tui.githubapi.error.exception

class BranchesNotFoundException(
    username: String,
    repoName: String
) : RuntimeException("Commits hasn't been detected in repo $repoName owned by $username")