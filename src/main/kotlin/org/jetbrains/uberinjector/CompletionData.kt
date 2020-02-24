package org.jetbrains.uberinjector

import java.util.regex.Pattern

object CompletionData {
    val MIME_PATTERN: Pattern = Pattern.compile("[^\\p{Cc}^\\s]+/[^\\p{Cc}^\\s]+")

    val MIME_TYPES = listOf(
        "application/json",
        "application/xml",
        "application/octet-stream",
        "application/x-www-form-urlencoded",
        "text/plain",
        "text/xml",
        "text/html",
        "text/json",
        "multipart/form-data"
    )

    val HTTP_HEADERS = listOf(
        "Accept",
        "Accept-Encoding",
        "Accept-Language",
        "Allow",
        "Authorization",
        "Content-Length",
        "Content-Type",
        "Expires",
        "Host",
        "Location",
        "Set-Cookie",
        "User-Agent"
    )
}