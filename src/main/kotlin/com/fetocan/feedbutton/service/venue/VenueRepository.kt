package com.fetocan.feedbutton.service.venue

import com.fetocan.feedbutton.service.cache.CacheConfig.Companion.VENUE_CACHE_NAME
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface VenueRepository : JpaRepository<Venue, Long> {
    @Cacheable(
        cacheNames = [VENUE_CACHE_NAME],
        key = "#id"
    )
    override fun findById(id: Long): Optional<Venue>
}
