package com.fetocan.feedbutton.service.pwdreset

import com.fetocan.feedbutton.service.exception.BadRequestException
import com.fetocan.feedbutton.service.exception.ErrorCodes.INVALID_TOKEN
import com.fetocan.feedbutton.service.jooq.Tables.PASSWORD_RESET_TOKEN
import org.apache.commons.lang3.RandomStringUtils
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PasswordResetService(
    private val dsl: DSLContext
) {

    /**
     * Creates a new password reset token for given account id (manager  or user)
     *
     * @return the generated token
     */
    fun createResetToken(
        accountId: UUID
    ): String {
        makeAllExistingTokensInactive(accountId)
        val token = generateRandomToken()
        dsl
            .insertInto(
                PASSWORD_RESET_TOKEN,
                PASSWORD_RESET_TOKEN.TOKEN,
                PASSWORD_RESET_TOKEN.ACCOUNT_ID,
                PASSWORD_RESET_TOKEN.ACTIVE
            )
            .values(token, accountId, true)
            .execute()
        return token
    }

    /**
     * Check if token exists and return associated accountId.
     * Throws an exception if token not found or if it's inactive
     *
     * @throws com.fetocan.feedbutton.service.exception.BadRequestException
     */
    fun validateToken(token: String): UUID {
        return dsl
            .select(
                PASSWORD_RESET_TOKEN.ACCOUNT_ID
            )
            .from(PASSWORD_RESET_TOKEN)
            .where(
                PASSWORD_RESET_TOKEN.TOKEN.eq(token)
                    .and(PASSWORD_RESET_TOKEN.ACTIVE.isTrue)
            )
            .fetchOne()
            ?.value1()
            ?: throw BadRequestException(
                INVALID_TOKEN,
                "Invalid token"
            )
    }

    fun markAsUsed(token: String) {
        dsl
            .update(PASSWORD_RESET_TOKEN)
            .set(PASSWORD_RESET_TOKEN.ACTIVE, false)
            .where(PASSWORD_RESET_TOKEN.TOKEN.eq(token))
            .execute()
    }

    private fun makeAllExistingTokensInactive(
        accountId: UUID
    ) {
        dsl
            .update(PASSWORD_RESET_TOKEN)
            .set(PASSWORD_RESET_TOKEN.ACTIVE, false)
            .where(PASSWORD_RESET_TOKEN.ACCOUNT_ID.eq(accountId))
            .execute()
    }

    private fun generateRandomToken() =
        RandomStringUtils.randomAlphanumeric(32)

}
