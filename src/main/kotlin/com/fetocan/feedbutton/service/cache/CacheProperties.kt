package com.fetocan.feedbutton.service.cache

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "app.cache")
data class CacheProperties(
    var timeToLive: Map<String, Long> = mutableMapOf()
)
