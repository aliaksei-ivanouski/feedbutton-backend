package com.fetocan.feedbutton.service.security.authentication.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtHelper(
    private val jwtSettings: JwtSettings
) {

    fun createToken(subject: String, claims: Map<String, Any?>, expirationTime: Long): String {
        val builder = Jwts.builder()
            .setClaims(Jwts.claims(claims).setSubject(subject))
            .signWith(signingKey)
            .setIssuedAt(Date())

        if (expirationTime != 0L) {
            builder.setExpiration(Date(System.currentTimeMillis() + expirationTime))
        }

        return builder.compact()
    }

    fun parseClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun isValidToken(token: String): Boolean {
        return try {
            parseClaims(token)
            true
        } catch (ex: Exception) {
            false
        }
    }

    fun resolveToken(req: HttpServletRequest) = resolve(req, jwtSettings.tokenHeader)

    fun resolveRefreshToken(req: HttpServletRequest) = resolve(req, jwtSettings.refreshTokenHeader)

    private fun resolve(req: HttpServletRequest, headerName: String): String? {
        val token = req.getHeader(headerName)
        return if (token != null && token.startsWith(jwtSettings.tokenPrefix)) {
            token.substring(jwtSettings.tokenPrefix.length)
        } else {
            null
        }
    }

    private val signingKey = Keys.hmacShaKeyFor(jwtSettings.signingKey.toByteArray())

    fun setResponseHeaders(res: HttpServletResponse, token: String, refreshToken: String? = null) {
        res.setHeader(
            "Access-Control-Expose-Headers",
            "${jwtSettings.tokenHeader},${jwtSettings.refreshTokenHeader}"
        )
        res.setHeader(jwtSettings.tokenHeader, token)
        if (refreshToken != null) {
            res.setHeader(jwtSettings.refreshTokenHeader, refreshToken)
        }
    }
}

