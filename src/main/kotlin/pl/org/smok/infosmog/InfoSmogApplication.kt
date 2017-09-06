package pl.org.smok.infosmog

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableCaching
class InfoSmogApplication

fun main(args: Array<String>) {
    SpringApplication.run(InfoSmogApplication::class.java, *args)
}
