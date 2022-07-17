package com.fetocan.feedbutton.service.security.authentication

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

abstract class JwtAuthenticationProvider() : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication? {
        val token = (authentication as JwtAuthenticationToken).principal as String

        return authenticate(token)
    }

    protected abstract fun authenticate(token: String): Authentication?

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

}
