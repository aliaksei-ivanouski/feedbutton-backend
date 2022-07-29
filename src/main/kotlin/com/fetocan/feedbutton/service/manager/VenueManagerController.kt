package com.fetocan.feedbutton.service.manager

import com.fetocan.feedbutton.service.exception.BadRequestException
import com.fetocan.feedbutton.service.exception.ErrorCodes.MANAGER_DOES_NOT_BELONG_TO_THE_VENUE
import com.fetocan.feedbutton.service.exception.ErrorCodes.MANAGER_NOT_FOUND
import com.fetocan.feedbutton.service.exception.ErrorCodes.MANAGER_VENUE_MAPPING_ALREADY_EXISTS
import com.fetocan.feedbutton.service.exception.ErrorCodes.VENUE_NOT_FOUND
import com.fetocan.feedbutton.service.exception.NotFoundException
import com.fetocan.feedbutton.service.venue.VenueService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.Size

@RestController
@RequestMapping("/api/v1/venues/{venueId}/managers")
@Tag(name = "Manager-Venue operations")
class VenueManagerController(
    private val venueService: VenueService,
    private val managerService: ManagerService
) {

    @VenueManagerApiDocs
    @PostMapping
    @PreAuthorize("hasAccess(#venueId, 'manager', 'create')")
    fun createVenueManager(
        @PathVariable("venueId") venueId: UUID,
        @Valid @RequestBody payload: CreateManagerPayload
    ): Manager {
        val venue = (venueService.findByIdOrNull(venueId)
            ?: throw NotFoundException(
                VENUE_NOT_FOUND,
                "Venue with id: $venueId not found"
            ))

        val manager = managerService.findByEmailIgnoreCase(payload.email)
            ?: managerService.createManager(
                Manager(
                    role = ManagerRole.VENUE_MANAGER,
                    email = payload.email,
                    password = null,
                    name = payload.name,
                    lastName = payload.lastName,
                    photoUrl = payload.photoUrl,
                    status = Manager.Status.PENDING
                ),
                true
            )

        if (managerService.hasVenueMapping(manager.id, venue.id)) {
            throw BadRequestException(
                MANAGER_VENUE_MAPPING_ALREADY_EXISTS,
                "Venue mapping already exists for the given manager"
            )
        }

        if (manager.role == ManagerRole.VENUE_MANAGER) {
            managerService.createVenueMapping(
                ManagerVenueMapping(
                    manager.id,
                    venue.id,
                    payload.accessScopes ?: arrayOf("*:*")
                )
            )
        }

        return manager
    }

    @Operation(summary = "Activate manager account (set status to ACTIVE or PENDING)")
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAccess(#venueId, 'manager', 'update')")
    fun activate(
        @PathVariable("venueId") venueId: UUID,
        @PathVariable("id") id: UUID
    ): Manager {
        val venue = (venueService.findByIdOrNull(venueId)
            ?: throw NotFoundException(
                VENUE_NOT_FOUND,
                "Venue with id: $venueId not found"
            ))
        val manager = managerService.findByIdOrNull(id)
            ?: throw NotFoundException(
                MANAGER_NOT_FOUND,
                "manager with given id not found"
            )

        if (!managerService.hasVenueMapping(manager.id, venue.id)) {
            throw BadRequestException(
                MANAGER_DOES_NOT_BELONG_TO_THE_VENUE,
                "manager does not belong to the given venue"
            )
        }

        manager.status = if (manager.password != null) {
            Manager.Status.ACTIVE
        } else {
            Manager.Status.PENDING
        }

        managerService.save(manager)

        return manager
    }

    @Schema(name = "CreateManagerPayload")
    data class CreateManagerPayload(
        @field:Email
        @field:Size(max = 200)
        val email: String,
        @field:Size(max = 200)
        val name: String,
        @field:Size(max = 200)
        val lastName: String,
        @field:Size(max = 2048)
        val photoUrl: String?,
        val accessScopes: Array<String>?
    )
}
