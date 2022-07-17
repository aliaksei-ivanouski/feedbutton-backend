package com.fetocan.feedbutton.service.venue

import com.fetocan.feedbutton.service.util.PageableDoc
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/venues")
@Tag(name = "Venue operations")
class VenueController(
    val venueService: VenueService
) {

    @GetVenueDoc
    @GetMapping("/{id}")
    fun getVenue(
        @VenueIdDoc @PathVariable id: Long
    ) = venueService.getVenue(id)

    @GetVenuesDoc
    @GetMapping
    fun getVenues(
        @PageableDoc pageable: Pageable
    ) = venueService.getVenues(
        pageable,
        Venue.BasicProjection::class.java
    )

    @GetVenuesWithManagersDoc
    @GetMapping("/users")
    fun getVenuesWithManagers(
        @PageableDoc pageable: Pageable
    ) = venueService.getVenuesWithManagers(
        pageable,
        Venue.VenueFullProjection::class.java
    )
}
