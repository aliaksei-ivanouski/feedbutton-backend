package com.fetocan.feedbutton.service.security

import com.fetocan.feedbutton.service.manager.ManagerService
import com.fetocan.feedbutton.service.security.authentication.JwtTokenAuthenticationFilter
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtSettings
import com.fetocan.feedbutton.service.security.authentication.manager.ManagerJwtAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val managerService: ManagerService,
    private val jwtSettings: JwtSettings
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        val bcrypt = BCryptPasswordEncoder()
        return DelegatingPasswordEncoder("bcrypt", mapOf(
            "bcrypt" to bcrypt
        ))
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addExposedHeader("Authorization")
        config.addExposedHeader(jwtSettings.tokenHeader)
        config.addExposedHeader(jwtSettings.refreshTokenHeader)
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return source
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
//        auth.authenticationProvider(AppUserJwtAuthenticationProvider(jwtSettings))
        auth.authenticationProvider(
            ManagerJwtAuthenticationProvider(
                jwtSettings,
                managerService
            )
        )
    }

    override fun configure(http: HttpSecurity) {
        http {
            csrf {
                disable()
            }
            authorizeRequests {
                authorize(anyRequest, permitAll)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            cors {  }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(
                JwtTokenAuthenticationFilter(
                    authenticationManager(),
                    jwtSettings
                )
            )
            headers {
                frameOptions {
                    disable()
                }
            }
        }
    }

}
