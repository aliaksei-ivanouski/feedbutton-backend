package com.fetocan.feedbutton.service.manager

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import com.fetocan.feedbutton.service.venue.Venue
import com.fetocan.feedbutton.service.jpa.AbstractBaseEntity
import com.vladmihalcea.hibernate.type.basic.Inet
import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.annotations.Type
import java.io.Serializable
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Table

@Entity
@Table(name = "manager")
class Manager(
    id: Long? = null,
    val venueId: Long,
    val email: String,
    @JsonIgnore
    var password: String?,
    @Enumerated(EnumType.STRING)
    var role: ManagerRole,
    var photoUrl: String? = null,
    val name: String,
    val lastName: String,
    val active: Boolean,
    val age: Int,
    var lastUsedIp: Inet? = null,
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "details")
    var details: JsonNode? = null
): AbstractBaseEntity(id), Serializable {

    interface Projection: Serializable

    @Schema(name = "ManagerBasicProjection")
    open class BasicProjection(
        val id: Long,
        val venueId: Long,
        val email: String,
        val name: String,
        val lastName: String,
        val active: Boolean,
        val age: Int,
        val lastUsedIp: Inet? = null,
        val updatedAt: Instant?,
        val createdAt: Instant?
    ): Projection {
        constructor(manager: Manager) : this(
            manager.id,
            manager.venueId,
            manager.email,
            manager.name,
            manager.lastName,
            manager.active,
            manager.age,
            manager.lastUsedIp,
            manager.updatedAt,
            manager.createdAt
        )
    }

    @Schema(name = "ManagerFullProjection")
    open class FullProjection(
        id: Long,
        venueId: Long,
        email: String,
        name: String,
        lastName: String,
        active: Boolean,
        age: Int,
        lastUsedIp: Inet? = null,
        updatedAt: Instant?,
        createdAt: Instant?,
        val venues: List<Venue.BasicProjection>? = mutableListOf()
    ): BasicProjection(id, venueId, email, name, lastName, active, age, lastUsedIp, updatedAt, createdAt)
}
