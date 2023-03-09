package org.mmh.clean_therapist.android.core.util

object Urls {
    fun get(tenant: String): String {
        return when (tenant.lowercase()) {
            "emma" -> "https://vaapi.injurycloud.com"
            "dev" -> ""
            else -> ""
        }
    }
}