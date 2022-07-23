package com.fetocan.feedbutton.service.venue

import com.fetocan.feedbutton.service.jpa.AbstractBaseEntity
import com.fetocan.feedbutton.service.manager.Manager
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.time.Instant
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "venue")
class Venue(
    id: UUID? = null,
    val name: String
): AbstractBaseEntity(id), Serializable {

    interface Projection: Serializable

    @Schema(name = "VenueBasicProjection")
    open class BasicProjection(
        val id: UUID?,
        val name: String,
        val updatedAt: Instant?,
        val createdAt: Instant?
    ): Projection {
        constructor(venue: Venue): this(
            venue.id,
            venue.name,
            venue.updatedAt,
            venue.createdAt
        )
    }

    @Schema(name = "VenueFullProjection")
    open class VenueFullProjection(
        id: UUID?,
        name: String,
        updatedAt: Instant?,
        createdAt: Instant?,
        val managers: List<Manager.BasicProjection>?
    ): BasicProjection(id, name, updatedAt, createdAt)

}
