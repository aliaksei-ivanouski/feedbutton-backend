package com.fetocan.feedbutton.service.user

import com.fetocan.feedbutton.service.exception.AlreadyExistsException
import com.fetocan.feedbutton.service.exception.BadRequestException
import com.fetocan.feedbutton.service.exception.ErrorCodes.ILLEGAL_USER_STATUS_REQUEST
import com.fetocan.feedbutton.service.exception.ErrorCodes.USER_ALREADY_EXISTS
import com.fetocan.feedbutton.service.exception.ErrorCodes.USER_NOT_FOUND
import com.fetocan.feedbutton.service.exception.NotFoundException
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtHelper
import com.fetocan.feedbutton.service.security.authentication.jwt.JwtSettings
import com.fetocan.feedbutton.service.security.authentication.user.UserClaims
import com.fetocan.feedbutton.service.security.authorization.Auth
import com.fetocan.feedbutton.service.util.RemoteAddressResolver
import com.vladmihalcea.hibernate.type.basic.Inet
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.Size

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User operations")
class UserController(
    val userService: UserService,
    val passwordEncoder: PasswordEncoder,
    private val jwtSettings: JwtSettings
) {

    private val jwtHelper = JwtHelper(jwtSettings)

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    @PreAuthorize("isMe(#id)")
    fun findById(@PathVariable("id") id: UUID): User.BasicProjection {
        return userService.findById(id, User.BasicProjection::class.java)
            ?: throw NotFoundException(USER_NOT_FOUND, "user with id: $id not found")
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody payload: RegisterUserPayload
    ): RegisterUserResponse {
        val existingUser = payload.email.let {
            userService.findByEmailIgnoreCase(it)
        }
        if (existingUser != null) {
            if (existingUser.password != null) {
                throw AlreadyExistsException(
                    USER_ALREADY_EXISTS,
                    "user with email: ${payload.email} already exists."
                )
            }

            // if we have a guest user with the same email, update and return it
            payload.name
                ?.takeIf { it.isNotBlank() }
                ?.also { existingUser.name = it.trim() }
            payload.phoneNumber
                ?.takeIf { it.isNotBlank() }
                ?.also { existingUser.phoneNumber = it.trim() }
            payload.photoUrl
                ?.takeIf { it.isNotBlank() }
                ?.also { existingUser.photoUrl = it.trim() }

            existingUser.password = passwordEncoder.encode(payload.password)
            existingUser.status = User.Status.ACTIVE

            userService.save(existingUser)
        }

        val user = existingUser ?: userService.save(
            User(
                email = payload.email.trim(),
                name = payload.name?.takeIf { it.isNotBlank() }?.trim(),
                password = passwordEncoder.encode(payload.password),
                phoneNumber = payload.phoneNumber?.takeIf { it.isNotBlank() }?.trim(),
                photoUrl = payload.photoUrl?.takeIf { it.isNotBlank() }?.trim(),
                registrationIp = RemoteAddressResolver.resolveAddressFromRequest()?.let { Inet(it.hostAddress) },
                status = User.Status.ACTIVE
            )
        )

        return RegisterUserResponse(
            User.BasicProjection(user),
            jwtHelper.createToken(user.id.toString(), UserClaims(user).toMap(), Duration.ofDays(30).toMillis()),
            jwtHelper.createToken(user.id.toString(), mapOf(), Duration.ofDays(365).toMillis())
        )
    }

    @Operation(summary = "Update user details")
    @PutMapping("/{id}")
    @PreAuthorize("isMe(#id)")
    fun update(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody payload: UpdateUserPayload
    ): User.BasicProjection {
        val user = userService.findByIdOrNull(id)
            ?: throw NotFoundException(USER_NOT_FOUND, "user with id: $id not found")

        if (!Auth.isMe(user.id)) {
            throw AccessDeniedException("access denied.")
        }

        payload.name?.also { user.name = it.trim() }
        payload.email?.also { user.email = it.trim() }
        payload.photoUrl?.also { user.photoUrl = it.trim() }
        payload.phoneNumber?.also { user.phoneNumber = it.trim() }
        if (!payload.password.isNullOrBlank()) {
            user.password = passwordEncoder.encode(payload.password)
        }

        if (Auth.isManager()) {
            payload.status?.also {
                if (payload.status == User.Status.ACTIVE && user.password == null) {
                    throw BadRequestException(
                        ILLEGAL_USER_STATUS_REQUEST,
                        "user's status can't be set to ACTIVE when the password is not set"
                    )
                }
                user.status = payload.status
            }
        }

        return User.BasicProjection(userService.save(user))
    }

    @Schema(name = "RegisterUserPayload")
    data class RegisterUserPayload(
        @field:Size(max = 200)
        val name: String?,
        @field:Email
        val email: String,
        @field:Size(min = 6, max = 200)
        val password: String,
        @field:Size(max = 100)
        val phoneNumber: String?,
        @field:Size(max = 2048)
        val photoUrl: String?
    )

    @Schema(name = "UpdateUserPayload")
    data class UpdateUserPayload(
        @field:Size(max = 200)
        val name: String?,
        @field:Email
        val email: String?,
        @field:Size(max = 2048)
        val photoUrl: String?,
        @field:Size(max = 200)
        val phoneNumber: String?,
        @field:Size(max = 200)
        val password: String?,
        @field:Size(max = 2048)
        val returnUrl: String?,
        val status: User.Status?
    )

    @Schema(name = "RegisterUserResponse")
    data class RegisterUserResponse(
        val user: User.BasicProjection,
        val accessToken: String,
        val refreshToken: String
    )
}
