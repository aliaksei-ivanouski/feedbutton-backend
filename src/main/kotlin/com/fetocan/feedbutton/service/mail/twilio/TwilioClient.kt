package com.fetocan.feedbutton.service.mail.twilio

import com.fetocan.feedbutton.service.LoggerDelegate
import com.fetocan.feedbutton.service.exception.ErrorCodes.SEND_EMAIL_ERROR
import com.fetocan.feedbutton.service.exception.TwilioException
import com.fetocan.feedbutton.service.mail.MailClient
import com.fetocan.feedbutton.service.mail.MailEvent
import com.fetocan.feedbutton.service.mail.MailTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class TwilioClient(
    @Value("\${twilio.url}") private val twilioUrl: String,
    @Value("\${twilio.api-key}") private val apiKey: String,
    @Value("\${app.email-from}") private val emailFrom: String
) : MailClient {

    private val logger by LoggerDelegate()
    private val httpClient = HttpClient.newBuilder().build()

    override fun sendEmail(
        mailEvent: MailEvent
    ) {
        val source = mailEvent.source as MailTemplate

        val params = source.params
            .map { "\"${it.key}\": \"${it.value}\"" }
            .joinToString(",")

        val request = HttpRequest.newBuilder()
            .uri(URI.create(twilioUrl))
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                        {
                            "personalizations": [
                                {
                                    "to": [
                                        {
                                            "email": "${source.recipient}"
                                        }
                                    ],
                                    "dynamic_template_data": {
                                        $params
                                    }
                                }
                            ],
                            "from": {
                                "email": "$emailFrom"
                            },
                            "subject": "${source.subject}",
                            "content": [
                                {
                                    "type": "text/html", 
                                    "value": "Hello, World!"
                                }
                            ],
                            "template_id": "${source.templateId}",
                        }
            """.trimIndent()
                )
            )
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() >= 400) {
            throw TwilioException(
                SEND_EMAIL_ERROR,
                "Error sending request: " +
                        "statusCode=${response.statusCode()}, " +
                        "body=${response.body()}"
            )
        }
    }
}
