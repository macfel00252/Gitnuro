package com.jetpackduba.gitnuro.avatarproviders

class JiraAvatarProvider : AvatarProvider {
    override fun getAvatarUrl(email: String): String {
        val md5 = java.security.MessageDigest.getInstance("MD5")
            .digest(email.lowercase().toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        return "https://jira.cbn.net.pl/rest/cavatar/1.0/user/${md5}"
    }
}
