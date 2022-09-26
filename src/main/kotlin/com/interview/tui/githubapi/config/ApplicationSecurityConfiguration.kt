package com.interview.tui.githubapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache

@Configuration
@EnableWebFluxSecurity
class ApplicationSecurityConfiguration {
    @Bean
    fun securityWebFilter(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .csrf().disable()
            .authorizeExchange {
                it.pathMatchers("/**").permitAll()
            }
            .requestCache {
                it.requestCache(NoOpServerRequestCache.getInstance())
            }
        return http.build()
    }
}