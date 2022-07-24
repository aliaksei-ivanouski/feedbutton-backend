package com.fetocan.feedbutton.service.mail

import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async

interface MailClient {
    @Async
    @EventListener
    fun sendEmail(mailEvent: MailEvent)
}
