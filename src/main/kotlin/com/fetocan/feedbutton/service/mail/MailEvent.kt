package com.fetocan.feedbutton.service.mail

import org.springframework.context.ApplicationEvent

class MailEvent(
    mailTemplate: MailTemplate
): ApplicationEvent(mailTemplate)
