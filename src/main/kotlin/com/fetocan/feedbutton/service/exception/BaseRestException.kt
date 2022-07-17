package com.fetocan.feedbutton.service.exception

open class BaseRestException(
    open val code: String,
    override val message: String,
    override val cause: Throwable? = null
): RuntimeException(message, cause)
