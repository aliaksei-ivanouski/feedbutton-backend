package com.fetocan.feedbutton.service.cache

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration


@EnableCaching
@Configuration
open class CacheConfig {

    @Bean
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(connectionFactory)
        return template
    }

    @Bean
    fun cacheManager(
        template: RedisTemplate<String, Any>,
        cacheLifeTime: CacheProperties
    ): CacheManager {
        val connectionFactory = template.connectionFactory
            ?: throw IllegalStateException("Cache connection factory is null.")
        val timeToLive = cacheLifeTime.timeToLive
        val cacheManagerBuilder = RedisCacheManagerBuilder
            .fromConnectionFactory(connectionFactory)
            .cacheDefaults(
                getCacheConfigurationWithTtl(
                    template,
                    timeToLive[DEFAULT_CACHE_NAME]
                        ?: throw IllegalArgumentException(
                            "Lifetime is not defined for $DEFAULT_CACHE_NAME cache"
                        )
                )
            )

        timeToLive.entries.forEach { entry ->
            entry
                .takeIf { it.key != DEFAULT_CACHE_NAME }
                ?.takeIf { it.value > 0 }
                ?.also {
                    cacheManagerBuilder
                        .withCacheConfiguration(
                            it.key,
                            getCacheConfigurationWithTtl(
                                template,
                                it.value
                            )
                        )
                }
        }

        return cacheManagerBuilder
            .transactionAware()
            .build()
    }

    private fun getCacheConfigurationWithTtl(
        template: RedisTemplate<String, Any>,
        seconds: Long
    ) = RedisCacheConfiguration
        .defaultCacheConfig()
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                template.stringSerializer
            )
        )
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                template.valueSerializer
            )
        )
        .disableCachingNullValues()
        .entryTtl(
            Duration.ofSeconds(seconds)
        )

    companion object {
        const val DEFAULT_CACHE_NAME = "default"
        const val USER_CACHE_NAME = "user"
        const val MANAGER_CACHE_NAME = "manager"
        const val VENUE_CACHE_NAME = "venue"
    }
}
