package com.fetocan.feedbutton.service.security.authentication.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "security.jwt")
data class JwtSettings(
    val tokenPrefix: String,
    val signingKey: String,
    val tokenHeader: String,
    val refreshTokenHeader: String
)
