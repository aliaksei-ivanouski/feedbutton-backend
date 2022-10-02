package com.fetocan.feedbutton.service.security.authentication

import com.fetocan.feedbutton.service.exception.BadRequestException
import com.fetocan.feedbutton.service.exception.ErrorCodes.NO_REFRESH_TOKEN
import com.fetocan.feedbutton.service.manager.Manager
import com.fetocan.feedbutton.service.manager.ManagerService
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtHelper
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtSettings
import com.fetocan.feedbutton.service.security.authentication.manager.ManagerClaims
import com.fetocan.feedbutton.service.security.authentication.user.UserClaims
import com.fetocan.feedbutton.service.user.User
import com.fetocan.feedbutton.service.user.UserService
import com.fetocan.feedbutton.service.util.tryOrNull
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication operation")
class AuthenticationController(
    private val userService: UserService,
    private val managerService: ManagerService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtSettings: JwtSettings
) {

    private val jwtHelper = JwtHelper(jwtSettings)

    @Operation(summary = "Authenticate user")
    @PostMapping
    fun userAuth(
        @Valid @RequestBody payload: UserAuthPayload,
        response: HttpServletResponse
    ): UserAuthResponse {
        val user = userService.findByEmailIgnoreCase(payload.email)

        if (user == null || !passwordEncoder.matches(payload.password, user.password)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase)
            throw BadCredentialsException("Bad credentials")
        }

        if (user.status != User.Status.ACTIVE) {
            response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.reasonPhrase)
            throw AccessDeniedException("account is not active")
        }

        val claims = UserClaims(user)
        val token = jwtHelper.createToken(user.id.toString(), claims.toMap(), Duration.ofDays(30).toMillis())
        val refreshToken = jwtHelper.createToken(user.id.toString(), mapOf(), Duration.ofDays(365).toMillis())

        userService.userCheckedIn(user)

        return UserAuthResponse(
            User.BasicProjection(user),
            token,
            refreshToken
        )
    }

    @Operation(summary = "Refresh user's token")
    @PostMapping("/refresh")
    fun refreshUserToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): UserAuthResponse {
        val refreshToken = jwtHelper.resolveRefreshToken(request)
            ?: throw BadRequestException(NO_REFRESH_TOKEN, "refresh token not provided")

        val tokenClaims = tryOrNull { jwtHelper.parseClaims(refreshToken) }
            ?: throw BadCredentialsException("invalid token")
        val userId = UUID.fromString(tokenClaims.subject)
            ?: throw BadCredentialsException("invalid token")

        val user = userService.findByIdOrNull(userId)
            ?: throw BadCredentialsException("invalid token")

        if (user.status != User.Status.ACTIVE) {
            throw AccessDeniedException("account is not active")
        }

        val claims = UserClaims(user)
        val newToken = jwtHelper.createToken(user.id.toString(), claims.toMap(), Duration.ofDays(30).toMillis())
        val newRefreshToken = jwtHelper.createToken(user.id.toString(), mapOf(), Duration.ofDays(365).toMillis())

        userService.userCheckedIn(user)

        return UserAuthResponse(
            User.BasicProjection(user),
            newToken,
            newRefreshToken
        )
    }

    @Operation(summary = "Authenticate manager account")
    @PostMapping("/manager", "/customer")
    fun managerAuth(
        @Valid @RequestBody payload: ManagerAuthPayload,
        response: HttpServletResponse
    ): ManagerAuthResponse {
        val manager = managerService.findByEmailIgnoreCase(payload.email)

        if (manager == null || !passwordEncoder.matches(payload.password, manager.password)) {
            throw BadCredentialsException("Bad credentials")
        }

        if (manager.status == Manager.Status.INACTIVE) {
            throw AccessDeniedException("account is inactive")
        }

        val claims = ManagerClaims(manager)
        val token = jwtHelper.createToken(manager.id.toString(), claims.toMap(), 0L)

        // TODO: temporarily load first venue and return info with response until client integrates with changes
        val mappings = managerService.getVenueMappings(manager.id)

        return ManagerAuthResponse(
            manager,
            TempCustomerResponse(
                manager.id,
                manager.email,
                manager.name,
                manager.photoUrl,
                mappings.firstOrNull()?.venueId,
                mappings.firstOrNull()?.accessScopes
            ),
            token
        )
    }

    @Schema(name = "UserAuthPayload")
    data class UserAuthPayload(
        val email: String,
        val password: String
    )

    @Schema(name = "ManagerAuthPayload")
    data class ManagerAuthPayload(
        val email: String,
        val password: String
    )

    @Schema(name = "UserAuthResponse")
    data class UserAuthResponse(
        val user: User.BasicProjection,
        val accessToken: String,
        val refreshToken: String
    )

    @Schema(name = "ManagerAuthResponse")
    data class ManagerAuthResponse(
        val manager: Manager,
        val customer: TempCustomerResponse,
        val accessToken: String
    )

    @Schema(name = "TempCustomerResponse")
    data class TempCustomerResponse(
        val id: UUID,
        val email: String,
        val name: String,
        val photoUrl: String?,
        val venueId: UUID?,
        val accessScopes:  Array<String>?
    )

}
