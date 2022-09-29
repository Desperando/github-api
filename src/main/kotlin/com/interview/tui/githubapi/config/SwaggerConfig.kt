package com.interview.tui.githubapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.router
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider
import springfox.documentation.swagger.web.SwaggerResource
import springfox.documentation.swagger.web.SwaggerResourcesProvider
import java.net.URI


@Configuration
class SwaggerConfig {

    private val baseSwaggerPrefix = "/swagger"
    private val baseSwaggerConfigPrefix = "$baseSwaggerPrefix/config"
    private val swaggerConfigFileName = "swagger.yaml"
    private val swaggerConfigFilePath = "swagger/$swaggerConfigFileName"
    private val swaggerMainPage = "/swagger-ui/index.html"

    @Bean
    @Primary
    fun swaggerResourceProvider(resourcesProvider: InMemorySwaggerResourcesProvider): SwaggerResourcesProvider {
        return SwaggerResourcesProvider {
            val wsResource = SwaggerResource()
            wsResource.name = "GitHub API repositories"
            wsResource.swaggerVersion = "3.0.0"
            wsResource.url = "$baseSwaggerConfigPrefix/$swaggerConfigFileName"
            listOf(wsResource)
        }
    }

    @Bean
    fun swaggerRouter() = router {
        GET(baseSwaggerPrefix) {
            permanentRedirect(URI.create(swaggerMainPage))
                .build()
        }
    }

    @Bean
    fun swaggerResource() = RouterFunctions.resources(
        "$baseSwaggerConfigPrefix/**",
        ClassPathResource(swaggerConfigFilePath)
    )
}