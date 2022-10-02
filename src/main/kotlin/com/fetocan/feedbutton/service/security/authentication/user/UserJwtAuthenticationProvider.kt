package com.fetocan.feedbutton.service.security.authentication.user

import com.fetocan.feedbutton.service.security.authentication.JwtAuthenticationProvider
import com.fetocan.feedbutton.service.security.authentication.JwtAuthenticationToken
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtHelper
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtSettings
import com.fetocan.feedbutton.service.util.tryOrNull
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

class UserJwtAuthenticationProvider(
    jwtSettings: JwtSettings
) : JwtAuthenticationProvider() {

    private val jwtHelper: JwtHelper = JwtHelper(jwtSettings)

    override fun authenticate(token: String): Authentication? {
        val claims = tryOrNull { jwtHelper.parseClaims(token) }
            ?: throw BadCredentialsException("invalid token")
        if (claims["role"] != null && claims["role"] != "USER") {
            return null
        }
        val userClaims = UserClaims(claims)

        return JwtAuthenticationToken(
            userClaims,
            listOf(SimpleGrantedAuthority("ROLE_${userClaims.role}"))
        )
    }

}
