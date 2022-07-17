package com.fetocan.feedbutton.service.security.authentication

interface AuthClaims {
    val sub: String
    val role: String
    fun toMap(): Map<String, Any?>
}
