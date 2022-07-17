package com.fetocan.feedbutton.service.util

fun <T : Any> tryOrNull(block: () -> T?): T? =
    try {
        block()
    } catch (ex: Exception) {
        null
    }
