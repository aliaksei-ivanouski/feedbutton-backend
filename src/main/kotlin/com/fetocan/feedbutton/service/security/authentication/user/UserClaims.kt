package com.fetocan.feedbutton.service.security.authentication.user

import com.fetocan.feedbutton.service.security.authentication.AuthClaims
import com.fetocan.feedbutton.service.user.User
import com.fetocan.feedbutton.service.util.tryOrNull
import java.util.UUID

class UserClaims(
    val id: UUID,
    val email: String?,
    val name: String?
): AuthClaims {

    override val sub: String
        get() = id.toString()

    override val role: String
        get() = "USER"

    constructor(user: User) : this(user.id, user.email, user.name)
    constructor(map: Map<String, Any?>) : this(
        map["sub"]?.toString()
            ?.let { tryOrNull { UUID.fromString(it) } }
            ?: throw RuntimeException("id is required"),
        map["email"].toString(),
        map["name"].toString()
    )

    override fun toMap(): Map<String, String?> = mapOf(
        "sub" to id.toString(),
        "email" to email,
        "name" to name
    )
}
