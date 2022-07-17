package com.fetocan.feedbutton.service.venue

import com.fetocan.feedbutton.service.jooq.Tables.VENUE
import com.fetocan.feedbutton.service.jooq.Tables.MANAGER
import com.fetocan.feedbutton.service.LoggerDelegate
import com.fetocan.feedbutton.service.exception.ErrorCodes.VENUE_NOT_FOUND
import com.fetocan.feedbutton.service.exception.NotFoundException
import com.fetocan.feedbutton.service.jooq.paged
import com.fetocan.feedbutton.service.manager.Manager
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class VenueService(
    val venueRepository: VenueRepository,
    val dsl: DSLContext
) : VenueRepository by venueRepository {

    private val logger by LoggerDelegate()

    fun getVenue(
        id: Long
    ) = findById(id)
        .map {
            Venue.BasicProjection(it)
        }
        .takeIf { it.isPresent }
        ?.get()
        ?: throw NotFoundException(
            VENUE_NOT_FOUND,
            "Department with id: $id not found"
        )

    fun <T : Venue.Projection> getVenues(
        pageable: Pageable,
        type: Class<T>
    ): Page<T> {
        logger.info("fetching departments...")

        val result = dsl
            .select(VENUE.asterisk())
            .from(VENUE)
            .paged(pageable, VENUE)
            .fetchInto(type)

        val count = count()

        return PageImpl(result, pageable, count)
    }

    fun <T : Venue.Projection> getVenuesWithManagers(
        pageable: Pageable,
        type: Class<T>
    ): Page<T> {
        logger.info("fetching departments with employees...")

        val result = dsl
            .select(
                VENUE.asterisk(),
                multiset(
                    select(MANAGER.asterisk())
                        .from(MANAGER)
                        .where(MANAGER.VENUE_ID.eq(VENUE.ID))
                ).`as`("users").convertFrom {
                    it.into(Manager.BasicProjection::class.java)
                }
            )
            .from(VENUE)
            .paged(pageable, VENUE)
            .fetchInto(type)

        val count = count()

        return PageImpl(result, pageable, count)
    }
}
