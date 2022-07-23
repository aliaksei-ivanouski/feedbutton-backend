package com.fetocan.feedbutton.service.exception

object ErrorCodes {

    //AUTH
    const val UNAUTHORIZED = "error.auth.unauthorized"
    const val ACCESS_DENIED = "error.access.denied"

    //MANAGER
    const val MANAGER_NOT_FOUND = "error.manager.not.found"

    //VENUE
    const val VENUE_NOT_FOUND = "error.venue.not.found"

    //MANAGER_VENUE MAPPING
    const val MANAGER_VENUE_MAPPING_ALREADY_EXISTS = "error.manager.venue.mapping.already.exists"
    const val MANAGER_DOES_NOT_BELONG_TO_THE_VENUE = "error.manager.does.not.belong.to.the.venue"
}
