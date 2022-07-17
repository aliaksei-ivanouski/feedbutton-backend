package com.fetocan.feedbutton.service.manager

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name = "ManagerVenueMapping")
open class ManagerVenueMapping(
    val managerId: Long,
    val venueId: Long,
    override val accessScopes: Array<String>,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
) : AccessScopesAware
