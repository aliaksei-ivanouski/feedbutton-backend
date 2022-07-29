package com.fetocan.feedbutton.service.universallink

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UniversalLinkController(
    private val universalLinkService: UniversalLinkService
) {

    @GetMapping(
        value = ["/apple-app-site-association"],
        produces = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun universalLink() = universalLinkService.readFile()

}
