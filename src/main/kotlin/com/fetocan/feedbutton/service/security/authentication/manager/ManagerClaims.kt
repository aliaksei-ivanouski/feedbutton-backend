package com.fetocan.feedbutton.service.security.authentication.manager

import com.fetocan.feedbutton.service.manager.Manager
import com.fetocan.feedbutton.service.manager.ManagerRole
import com.fetocan.feedbutton.service.security.authentication.AuthClaims
import com.fetocan.feedbutton.service.util.tryOrNull
import java.util.UUID

data class ManagerClaims(
    val id: UUID,
    val email: String,
    val name: String,
    val managerRole: ManagerRole
) : AuthClaims {
    override val sub: String
        get() = id.toString()

    override val role: String
        get() = managerRole.name

    constructor(manager: Manager) : this(
        manager.id,
        manager.email,
        manager.name,
        manager.role
    )

    constructor(map: Map<String, Any?>) : this(
        map["sub"]?.toString()
            ?.let { tryOrNull { UUID.fromString(it) } }
            ?: throw RuntimeException("id is required"),
        map["email"]?.toString()
            ?: throw RuntimeException("email is required"),
        map["name"]?.toString()
            ?: throw RuntimeException("name is required"),
        map["role"]?.toString()
            ?.let { ManagerRole.valueOf(it) }
            ?: throw RuntimeException("managerRole is required")
    )

    override fun toMap(): Map<String, Any?> = mapOf(
        "role" to managerRole,
        "sub" to id.toString(),
        "email" to email,
        "name" to name
    )
}
