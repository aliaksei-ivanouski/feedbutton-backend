package com.fetocan.feedbutton.service.security.authentication

interface JwtClaimsResolver {
    fun resolveClaims(token: String): AuthClaims?
}
