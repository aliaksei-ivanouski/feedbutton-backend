package com.fetocan.feedbutton.service.manager

import com.fetocan.feedbutton.service.jooq.Tables.VENUE
import com.fetocan.feedbutton.service.jooq.Tables.MANAGER
import com.fetocan.feedbutton.service.LoggerDelegate
import com.fetocan.feedbutton.service.venue.Venue
import com.fetocan.feedbutton.service.exception.ErrorCodes.MANAGER_NOT_FOUND
import com.fetocan.feedbutton.service.exception.NotFoundException
import com.fetocan.feedbutton.service.jooq.Tables.MANAGER_VENUE
import com.fetocan.feedbutton.service.jooq.paged
import com.fetocan.feedbutton.service.pwdreset.PasswordResetService
import com.fetocan.feedbutton.service.util.RemoteAddressResolver
import com.vladmihalcea.hibernate.type.basic.Inet
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import org.springframework.cache.CacheManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID


@Service
@Transactional
class ManagerService(
    private val managerRepository: ManagerRepository,
    private val dsl: DSLContext,
    private val passwordResetService: PasswordResetService,
    private val cacheManager: CacheManager
) : ManagerRepository by managerRepository {

    private val logger by LoggerDelegate()

    fun getManagerById(
        id: UUID
    ): Manager.BasicProjection {
        val currentIp = RemoteAddressResolver.resolveAddressFromRequest()
            ?.let { Inet(it.hostAddress) }
        val user = findById(id)
            .takeIf { it.isPresent }
            ?.also {
                val user = it.get()
                if (user.lastUsedIp != currentIp) {
                    user.lastUsedIp = currentIp
                }
            }
        return user?.map {
            Manager.BasicProjection(it)
        }
            ?.get()
            ?: throw NotFoundException(
                MANAGER_NOT_FOUND,
                "User with id: $id not found"
            )
    }

    fun getVenueMappings(
        accountId: UUID
    ): List<ManagerVenueMapping> =
        dsl
            .select(MANAGER_VENUE.asterisk())
            .from(MANAGER_VENUE)
            .where(MANAGER_VENUE.MANAGER_ID.eq(accountId))
            .fetch().into(ManagerVenueMapping::class.java)

    fun getVenueMapping(
        accountId: UUID,
        venueId: UUID
    ): ManagerVenueMapping? =
        dsl
            .select(MANAGER_VENUE.asterisk())
            .from(MANAGER_VENUE)
            .where(
                MANAGER_VENUE.MANAGER_ID.eq(accountId)
                    .and(MANAGER_VENUE.VENUE_ID.eq(venueId))
            )
            .fetchOneInto(ManagerVenueMapping::class.java)

    fun <T : Manager.Projection> getManagers(
        pageable: Pageable,
        type: Class<T>
    ): Page<T> {
        logger.info("fetching users...")

        val result = dsl
            .select(MANAGER.asterisk())
            .from(MANAGER)
            .paged(pageable, MANAGER)
            .fetchInto(type)

        val count = count()

        return PageImpl(result, pageable, count)
    }

    fun <T : Manager.Projection> getManagersWithVenues(
        pageable: Pageable,
        type: Class<T>
    ): Page<T> {
        logger.info("fetching users with department...")

        val result = dsl
            .select(
                MANAGER.asterisk(),
                multiset(
                    select(
                        VENUE.ID,
                        VENUE.NAME
                    )
                        .from(VENUE)
                        .join(MANAGER_VENUE)
                        .on(
                            VENUE.ID.eq(MANAGER_VENUE.VENUE_ID)
                                .and(MANAGER.ID.eq(MANAGER_VENUE.MANAGER_ID))
                        )
                ).`as`("venues").convertFrom {
                    it.into(Venue.BasicProjection::class.java)
                }
            )
            .from(MANAGER)
            .paged(pageable, MANAGER)
            .fetchInto(type)

        val count = count()

        return PageImpl(result, pageable, count)
    }

    fun createManager(
        account: Manager,
        sendInvite: Boolean = false
    ): Manager {
        if (!account.isNew) {
            throw IllegalArgumentException("ManagerAccount should be a new one")
        }
        val newAccount = saveAndFlush(account)
        if (sendInvite) {
//            sendInvitation(newAccount)
        }
        return newAccount
    }

    fun hasVenueMapping(
        accountId: UUID,
        venueId: UUID
    ): Boolean = dsl.fetchExists(
        MANAGER_VENUE,
        MANAGER_VENUE.MANAGER_ID.eq(accountId).and(MANAGER_VENUE.VENUE_ID.eq(venueId))
    )

    fun createVenueMapping(
        mapping: ManagerVenueMapping
    ) = createVenueMappings(listOf(mapping))

    fun createVenueMappings(
        mappings: List<ManagerVenueMapping>
    ) {
        var statement = dsl.insertInto(
            MANAGER_VENUE,
            MANAGER_VENUE.MANAGER_ID,
            MANAGER_VENUE.VENUE_ID,
            MANAGER_VENUE.ACCESS_SCOPES
        )

        mappings.forEach {
            statement = statement.values(
                it.managerId,
                it.venueId,
                it.accessScopes
            )
        }

        statement.execute()
    }
}
