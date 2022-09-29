package com.interview.tui.githubapi.api.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("config.github")
data class GithubClientConfigurationProperties(
    val urlPrefix: String,
    val clientId: String?,
    val clientSecret: String?
)