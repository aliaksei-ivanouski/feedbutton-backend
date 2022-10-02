package com.fetocan.feedbutton.service.user

import com.fetocan.feedbutton.service.jpa.AbstractBaseEntity
import com.vladmihalcea.hibernate.type.basic.Inet
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import java.time.Instant
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Table

@Entity
@Table(name = "user", schema = "public")
class User(
    id: UUID? = null,
    var email: String?,
    var password: String?,
    var name: String?,
    var phoneNumber: String?,
    var photoUrl: String? = null,
    var points: Int = 0,
    /**
     * Ip address from which registration is made
     */
    var registrationIp: Inet? = null,
    /**
     * User's last tracked ip address
     */
    var lastUsedIp: Inet? = null,
    /**
     * Indicates whether user is unsubscribed from email campaigns
     */
    var unsubscribed: Boolean = false,
    /**
     * Last time user opened webapp (last time user made authorized request to get user details)
     */
    var lastCheckInAt: Instant? = null,
    @Enumerated(EnumType.STRING)
    var status: Status
) : AbstractBaseEntity(id), Serializable {

    enum class Status {
        INACTIVE,
        ACTIVE,
        ARCHIVED
    }

    interface Projection : Serializable

    @Schema(name = "UserBasicProjection")
    open class BasicProjection(
        val id: UUID,
        val email: String?,
        val name: String?,
        val phoneNumber: String?,
        val photoUrl: String?,
        val points: Int,
        val status: Status,
    ) : Projection {
        constructor(user: User) : this(
            user.id,
            user.email,
            user.name,
            user.phoneNumber,
            user.photoUrl,
            user.points,
            user.status
        )
    }

    @Schema(name = "UserFullProjection")
    open class FullProjection(
        id: UUID,
        email: String?,
        name: String?,
        phoneNumber: String?,
        photoUrl: String?,
        points: Int,
        status: Status,
        val registrationIp: Inet?,
        val lastUsedIp: Inet?,
        val unsubscribed: Boolean,
        val lastCheckInAt: Instant?,
        val createdAt: Instant?,
        val updatedAt: Instant?
    ) : BasicProjection(
        id, email, name, phoneNumber, photoUrl, points, status
    ) {
        constructor(user: User.FullProjection): this(
            user.id,
            user.email,
            user.name,
            user.phoneNumber,
            user.photoUrl,
            user.points,
            user.status,
            user.registrationIp,
            user.lastUsedIp,
            user.unsubscribed,
            user.lastCheckInAt,
            user.createdAt,
            user.updatedAt
        )
    }

}
