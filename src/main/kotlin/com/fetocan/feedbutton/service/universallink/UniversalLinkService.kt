package com.fetocan.feedbutton.service.universallink

import com.fetocan.feedbutton.service.LoggerDelegate
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.io.IOException


@Service
class UniversalLinkService(
    @Value("classpath:apple-app-site-association")
    private val resource: Resource? = null
) {

    private val logger by LoggerDelegate()

    fun readFile(): ByteArray? {
        try {
            val bytes: ByteArray = resource!!.inputStream.readBytes()
            logger.info("Universal link has been read successfully.")
            return bytes
        } catch (e: IOException) {
            logger.error("Error occurred while access to universal link file.", e)
        }
        return ByteArray(0)
    }
}
