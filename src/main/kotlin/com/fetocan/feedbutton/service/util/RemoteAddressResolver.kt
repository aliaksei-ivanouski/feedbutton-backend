package com.fetocan.feedbutton.service.util

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.InetAddress
import javax.servlet.http.HttpServletRequest

object RemoteAddressResolver {
    private val IP_HEADER_CANDIDATES = arrayOf(
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR",
    )

    private fun readIpFromHeader(
        request: HttpServletRequest,
        headerName: String
    ): String? {
        val ipList = request.getHeader(headerName)
        return if (ipList == null || ipList.isEmpty() || ipList == "unknown") {
            null
        } else {
            ipList.split(",")[0]
        }
    }

    private fun resolveAddress(
        request: HttpServletRequest
    ): InetAddress? {
        IP_HEADER_CANDIDATES.forEach { header ->
            readIpFromHeader(request, header)?.also { return InetAddress.getByName(it) }
        }

        return InetAddress.getByName(request.remoteAddr)
    }

    fun resolveAddressFromRequest(): InetAddress? {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        return resolveAddress(request)
    }
}
