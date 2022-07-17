package com.fetocan.feedbutton.service.security.authentication

import com.fetocan.feedbutton.service.security.authentication.jwt.JwtHelper
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtSettings
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    jwtSettings: JwtSettings
) : OncePerRequestFilter() {

    private val jwtHelper = JwtHelper(jwtSettings)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtHelper.resolveToken(request)

        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val authRequest = JwtAuthenticationToken(token)
            val authentication = authenticationManager.authenticate(authRequest)

            SecurityContextHolder.getContext().authentication = authentication

            filterChain.doFilter(request, response)
        } catch (ex: AuthenticationException) {
            logger.debug("Error authenticating", ex)
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase)
            throw ex
        } catch (ex: Exception) {
            logger.debug("Error authenticating", ex)
            throw ex
        } finally {
            SecurityContextHolder.clearContext()
        }
    }

}
