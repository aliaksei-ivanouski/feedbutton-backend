package com.fetocan.feedbutton.service.manager

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

@Schema(name = "ManagerVenueMapping")
open class ManagerVenueMapping(
    val managerId: UUID,
    val venueId: UUID,
    override val accessScopes: Array<String>,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
) : AccessScopesAware
