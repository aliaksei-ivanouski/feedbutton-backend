package com.fetocan.feedbutton.service.mail

data class MailTemplate(
    val ccRecipients: List<String> = emptyList(),
    val bccRecipients: List<String> = emptyList(),
    val subject: String,
    val recipient: String,
    val templateId: TemplateId,
    val params: Map<String, Any>
)

enum class TemplateId(
    val value: String
) {
    //Twilio
    TWILIO_MANAGER_INVITATION("d-36df405e85b74a969d79830b9bc74be4");

    override fun toString(): String {
        return value
    }
}
