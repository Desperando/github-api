package com.interview.tui.githubapi.api.configuration

import com.interview.tui.githubapi.api.configuration.properties.GithubClientConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(GithubClientConfigurationProperties::class)
class GithubClientConfiguration(
    val githubClientConfigurationProperties: GithubClientConfigurationProperties,
) {

    @Bean
    fun githubWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(this.githubClientConfigurationProperties.urlPrefix)
            .defaultHeaders { headers ->
                if (githubClientConfigurationProperties.clientId != null
                    && githubClientConfigurationProperties.clientSecret != null
                ) {
                    headers.setBasicAuth(
                        githubClientConfigurationProperties.clientId.orEmpty(),
                        githubClientConfigurationProperties.clientSecret.orEmpty()
                    )
                }
            }
            .build()
    }
}