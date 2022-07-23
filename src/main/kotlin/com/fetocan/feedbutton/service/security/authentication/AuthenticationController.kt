package com.fetocan.feedbutton.service.security.authentication

import com.fetocan.feedbutton.service.manager.Manager
import com.fetocan.feedbutton.service.manager.ManagerService
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtHelper
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtSettings
import com.fetocan.feedbutton.service.security.authentication.manager.ManagerClaims
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/v1/auth")
class AuthenticationController(
//    private val appUserService: AppUserService,
    private val managerService: ManagerService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtSettings: JwtSettings
) {

    private val jwtHelper = JwtHelper(jwtSettings)

//    @Operation(summary = "Authenticate user")
//    @PostMapping
//    fun appUserAuth(
//        @Valid @RequestBody payload: AppUserAuthPayload,
//        response: HttpServletResponse
//    ): AppUserAuthResponse {
//        val user = appUserService.findByVenueIdAndEmailIgnoreCase(
//            payload.venueId,
//            payload.email
//        )
//
//        if (user == null || !passwordEncoder.matches(payload.password, user.password)) {
//            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase)
//            throw BadCredentialsException("Bad credentials")
//        }
//
//        if (user.status != AppUser.Status.ACTIVE) {
//            response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.reasonPhrase)
//            throw AccessDeniedException("account is not active")
//        }
//
//        val claims = AppUserClaims(user)
//        val token = jwtHelper.createToken(user.id.toString(), claims.toMap(), Duration.ofDays(30).toMillis())
//        val refreshToken = jwtHelper.createToken(user.id.toString(), mapOf(), Duration.ofDays(365).toMillis())
//
//        appUserService.userCheckedIn(user)
//
//        return AppUserAuthResponse(
//            AppUser.BasicProjection(user),
//            token,
//            refreshToken
//        )
//    }

//    @Operation(summary = "Refresh user's token")
//    @PostMapping("/refresh")
//    fun refreshUserToken(
//        request: HttpServletRequest,
//        response: HttpServletResponse
//    ): AppUserAuthResponse {
//        val refreshToken = jwtHelper.resolveRefreshToken(request)
//            ?: throw BadRequestException("refresh token not provided")
//
//        val tokenClaims = tryOrNull { jwtHelper.parseClaims(refreshToken) }
//            ?: throw BadCredentialsException("invalid token")
//        val userId = UUID.fromString(tokenClaims.subject)
//            ?: throw BadCredentialsException("invalid token")
//
//        val user = appUserService.findByIdOrNull(userId)
//            ?: throw BadCredentialsException("invalid token")
//
//        if (user.status != AppUser.Status.ACTIVE) {
//            throw AccessDeniedException("account is not active")
//        }
//
//        val claims = AppUserClaims(user)
//        val newToken = jwtHelper.createToken(user.id.toString(), claims.toMap(), Duration.ofDays(30).toMillis())
//        val newRefreshToken = jwtHelper.createToken(user.id.toString(), mapOf(), Duration.ofDays(365).toMillis())
//
//        appUserService.userCheckedIn(user)
//
//        return AppUserAuthResponse(
//            AppUser.BasicProjection(user),
//            newToken,
//            newRefreshToken
//        )
//    }

    @Operation(summary = "Authenticate manager account")
    @PostMapping("/manager", "/customer")
    fun managerAuth(
        @Valid @RequestBody payload: ManagerAuthPayload,
        response: HttpServletResponse
    ): ManagerAuthResponse {
        val manager = managerService.findByEmailIgnoreCase(payload.email)

        if (manager == null || !passwordEncoder.matches(payload.password, manager.password)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase)
            throw BadCredentialsException("Bad credentials")
        }

        if (manager.status == Manager.Status.INACTIVE) {
            response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.reasonPhrase)
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

    @Schema(name = "AppUserAuthPayload")
    data class AppUserAuthPayload(
        val email: String,
        val password: String,
        val venueId: UUID
    )

    @Schema(name = "ManagerAuthPayload")
    data class ManagerAuthPayload(
        val email: String,
        val password: String
    )

//    @Schema(name = "AppUserAuthResponse")
//    data class AppUserAuthResponse(
//        val user: AppUser.BasicProjection,
//        val accessToken: String,
//        val refreshToken: String
//    )

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
