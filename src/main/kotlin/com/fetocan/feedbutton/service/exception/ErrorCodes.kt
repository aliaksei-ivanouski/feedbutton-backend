package com.fetocan.feedbutton.service.exception

object ErrorCodes {

    //AUTH
    const val UNAUTHORIZED = "error.auth.unauthorized"
    const val ACCESS_DENIED = "error.access.denied"
    const val INVALID_TOKEN = "error.invalid.token"
    const val NO_REFRESH_TOKEN = "error.no.refresh.token"

    //MANAGER
    const val MANAGER_NOT_FOUND = "error.manager.not.found"
    const val MANAGER_INCORRECT_STATUS = "error.manager.has.incorrect.status"

    //USER
    const val USER_NOT_FOUND = "error.user.not.found"
    const val USER_ALREADY_EXISTS = "error.user.already.exists"
    const val ILLEGAL_USER_STATUS_REQUEST = "error.illegal.user.status.request"

    //VENUE
    const val VENUE_NOT_FOUND = "error.venue.not.found"

    //MANAGER_VENUE MAPPING
    const val MANAGER_VENUE_MAPPING_ALREADY_EXISTS = "error.manager.venue.mapping.already.exists"
    const val MANAGER_DOES_NOT_BELONG_TO_THE_VENUE = "error.manager.does.not.belong.to.the.venue"

    //TWILIO
    const val SEND_EMAIL_ERROR = "error.send.email"
}
