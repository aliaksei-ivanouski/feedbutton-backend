package com.fetocan.feedbutton.service.security.authentication.manager

import com.fetocan.feedbutton.service.manager.ManagerRole
import com.fetocan.feedbutton.service.manager.ManagerService
import com.fetocan.feedbutton.service.security.authentication.JwtAuthenticationProvider
import com.fetocan.feedbutton.service.security.authentication.JwtAuthenticationToken
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtHelper
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtSettings
import com.fetocan.feedbutton.service.util.tryOrNull
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

class ManagerJwtAuthenticationProvider(
    jwtSettings: JwtSettings,
    private val managerService: ManagerService
) : JwtAuthenticationProvider() {

    private val jwtHelper: JwtHelper = JwtHelper(jwtSettings)

    override fun authenticate(token: String): Authentication? {
        val claims = tryOrNull { jwtHelper.parseClaims(token) }
            ?: throw BadCredentialsException("invalid token")
        if (tryOrNull { ManagerRole.valueOf(claims["role"].toString()) } == null) {
            return null
        }

        val id = claims["sub"]?.toString()?.let { tryOrNull { it.toLong() } }
            ?: throw BadCredentialsException("valid sub is required")

        // consider putting accessScopes into token
        val manager = managerService.findByIdOrNull(id)
            ?: throw BadCredentialsException("valid sub is required")

//        if (manager.status == ManagerAccount.Status.INACTIVE)
//            throw AccessDeniedException("account is inactive")

        val managerClaims = ManagerClaims(claims)

        return JwtAuthenticationToken(
            managerClaims,
            listOf(
                SimpleGrantedAuthority("ROLE_MANAGER"),
                SimpleGrantedAuthority("ROLE_${managerClaims.role}")
            )
        )
    }

}
