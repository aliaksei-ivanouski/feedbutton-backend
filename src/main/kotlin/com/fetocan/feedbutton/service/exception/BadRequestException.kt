package com.fetocan.feedbutton.service.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException(
    override val code: String,
    override val message: String,
    override val cause: Throwable? = null
): BaseRestException(code, message, cause)
