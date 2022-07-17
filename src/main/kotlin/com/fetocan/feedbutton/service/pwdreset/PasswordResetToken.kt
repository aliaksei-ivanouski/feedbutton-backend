package com.socialvenu.pwdreset

import java.time.Instant
import java.util.*

internal class PasswordResetToken(
    /**
     * reset token, primary key
     */
    val token: String,
    /**
     * ID of a customer or user
     */
    val accountId: UUID,
    val active: Boolean = true,
    val createdAt: Instant? = Instant.now(),
    val updatedAt: Instant? = Instant.now()
)
