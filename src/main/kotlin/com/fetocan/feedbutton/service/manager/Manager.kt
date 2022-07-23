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
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Table

@Entity
@Table(name = "manager")
class Manager(
    id: UUID? = null,
    val email: String,
    @JsonIgnore
    var password: String? = null,
    @Enumerated(EnumType.STRING)
    var role: ManagerRole,
    var photoUrl: String? = null,
    val name: String,
    val lastName: String? = null,
    val age: Int? = null,
    var lastUsedIp: Inet? = null,
    @Enumerated(EnumType.STRING)
    var status: Status,
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "details")
    var details: JsonNode? = null
): AbstractBaseEntity(id), Serializable {

    enum class Status {
        PENDING,
        ACTIVE,
        INACTIVE
    }

    interface Projection: Serializable

    @Schema(name = "ManagerBasicProjection")
    open class BasicProjection(
        val id: UUID,
        val email: String,
        val name: String,
        val lastName: String?,
        val age: Int?,
        val role: ManagerRole,
        val lastUsedIp: Inet?,
        val updatedAt: Instant?,
        val createdAt: Instant?
    ): Projection {
        constructor(manager: Manager) : this(
            manager.id,
            manager.email,
            manager.name,
            manager.lastName,
            manager.age,
            manager.role,
            manager.lastUsedIp,
            manager.updatedAt,
            manager.createdAt
        )
    }

    @Schema(name = "ManagerFullProjection")
    open class FullProjection(
        id: UUID,
        email: String,
        name: String,
        lastName: String?,
        age: Int?,
        role: ManagerRole,
        lastUsedIp: Inet? = null,
        updatedAt: Instant?,
        createdAt: Instant?,
        val venues: List<Venue.BasicProjection>? = mutableListOf()
    ): BasicProjection(id, email, name, lastName, age, role, lastUsedIp, updatedAt, createdAt)
}
