package com.fetocan.feedbutton.service.manager

import com.fetocan.feedbutton.service.LoggerDelegate
import com.fetocan.feedbutton.service.exception.BadRequestException
import com.fetocan.feedbutton.service.exception.ErrorCodes.MANAGER_INCORRECT_STATUS
import com.fetocan.feedbutton.service.exception.ErrorCodes.MANAGER_NOT_FOUND
import com.fetocan.feedbutton.service.exception.NotFoundException
import com.fetocan.feedbutton.service.mail.MailEvent
import com.fetocan.feedbutton.service.mail.MailTemplate
import com.fetocan.feedbutton.service.mail.TemplateId
import com.fetocan.feedbutton.service.pwdreset.PasswordResetService
import com.fetocan.feedbutton.service.util.PageableDoc
import com.fetocan.feedbutton.service.web.SuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/managers")
@Tag(name = "Manager operations")
class ManagerController(
    private val managerService: ManagerService,
    private val passwordResetService: PasswordResetService,
    private val passwordEncoder: PasswordEncoder,
    private val publisher: ApplicationEventPublisher,
    @Value("\${app.dashboard-url}") private val dashboardUrl: String
) {

    private val logger by LoggerDelegate()

    @GetManagerDoc
    @GetMapping("/{id}")
    fun getManager(
        @ManagerIdDoc @PathVariable id: UUID
    ) = managerService.getManagerById(id)

    @GetManagersDoc
    @GetMapping
    fun getManagers(
        @PageableDoc pageable: Pageable
    ) = managerService.getManagers(
        pageable, Manager.BasicProjection::class.java
    )

    @Operation(summary = "Send password reset email")
    @PostMapping("/forgot-password")
    fun forgotPassword(
        @Valid @RequestBody payload: ForgotPasswordPayload
    ): SuccessResponse {
        val manager = managerService.findByEmailIgnoreCase(payload.email)
            ?: throw NotFoundException(
                MANAGER_NOT_FOUND,
                "manager account not found"
            )

        val token = passwordResetService.createResetToken(manager.id)

        val link = "${dashboardUrl}/apple-app-site-association?action=RESET_PASSWORD&token=$token"
        logger.info("Link has been sent to the manager id: ${manager.id}, link: $link")

        publisher.publishEvent(
            MailEvent(
                MailTemplate(
                    subject = "Reset password request",
                    recipient = manager.email,
                    templateId = TemplateId.TWILIO_RESET_PASSWORD,
                    params = mapOf(
                        Pair("name", manager.name),
                        Pair("link", link)
                    )
                )
            )
        )
        return SuccessResponse()
    }

    @Operation(summary = "Reset password with password reset token")
    @PostMapping("/reset-password")
    fun resetPassword(
        @Valid @RequestBody payload: ResetPasswordPayload
    ): SuccessResponse {
        val managerId = passwordResetService.validateToken(payload.token)

        val manager = managerService.findByIdOrNull(managerId)
            ?: throw NotFoundException(
                MANAGER_NOT_FOUND,
                "manager account not found"
            )

        if (manager.status == Manager.Status.INACTIVE) {
            throw BadRequestException(
                MANAGER_INCORRECT_STATUS,
                "the manager is INACTIVE")
        }

        manager.password = passwordEncoder.encode(payload.newPassword)
        if (manager.status == Manager.Status.PENDING) {
            manager.status = Manager.Status.ACTIVE
        }
        managerService.save(manager)

        passwordResetService.markAsUsed(payload.token)

        return SuccessResponse()
    }

    @GetManagersWithVenuesDoc
    @GetMapping("/venues")
    @PreAuthorize("isAdmin()")
    fun getManagersWithVenues(
        @PageableDoc pageable: Pageable
    ) = managerService.getManagersWithVenues(
        pageable,
        Manager.FullProjection::class.java
    )

    @Schema(name = "ManagerForgotPasswordPayload")
    data class ForgotPasswordPayload(
        val email: String
    )

    @Schema(name = "ManagerResetPasswordPayload")
    data class ResetPasswordPayload(
        val token: String,
        val newPassword: String
    )
}
