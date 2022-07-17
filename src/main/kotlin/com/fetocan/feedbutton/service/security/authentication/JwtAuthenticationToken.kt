package com.fetocan.feedbutton.service.security.authentication

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken(
    private val principal: Any,
    authorities: List<GrantedAuthority>? = null
) : AbstractAuthenticationToken(authorities) {

    init {
        if (authorities != null) {
            isAuthenticated = true
        }
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): Any {
        return principal
    }

}
