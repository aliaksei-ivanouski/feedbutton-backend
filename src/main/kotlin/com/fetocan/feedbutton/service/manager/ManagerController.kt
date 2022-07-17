package com.fetocan.feedbutton.service.manager

import com.fetocan.feedbutton.service.util.PageableDoc
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/managers")
@Tag(name = "Manager operations")
class ManagerController(
    private val managerService: ManagerService
) {

    @GetManagerDoc
    @GetMapping("/{id}")
    fun getManager(
        @ManagerIdDoc @PathVariable id: Long
    ) = managerService.getManagerById(id)

    @GetManagersDoc
    @GetMapping
    fun getManagers(
        @PageableDoc pageable: Pageable
    ) = managerService.getManagers(
        pageable, Manager.BasicProjection::class.java
    )

    @GetManagersWithVenuesDoc
    @GetMapping("/venues")
    @PreAuthorize("isAdmin()")
    fun getManagersWithVenues(
        @PageableDoc pageable: Pageable
    ) = managerService.getManagersWithVenues(
        pageable,
        Manager.FullProjection::class.java
    )
}
