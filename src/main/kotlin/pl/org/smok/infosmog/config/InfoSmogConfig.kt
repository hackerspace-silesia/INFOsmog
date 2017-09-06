package pl.org.smok.infosmog.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class InfoSmogConfig {

    @Bean
    protected fun restTemplate(): RestTemplate {
        return RestTemplate(clientHttpRequestFactory())
    }

    private fun clientHttpRequestFactory(): ClientHttpRequestFactory {
        val factory = HttpComponentsClientHttpRequestFactory()
        factory.setReadTimeout(10 * 60 * 1000)
        factory.setConnectTimeout(10 * 60 * 1000)
        return factory
    }
}
