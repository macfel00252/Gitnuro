@file:OptIn(ExperimentalStdlibApi::class)

package com.jetpackduba.gitnuro.avatarproviders

class GravatarAvatarProvider : AvatarProvider {
    override fun getAvatarUrl(email: String): String {
        val hashed = java.security.MessageDigest.getInstance("SHA-256")
            .digest(email.lowercase().toByteArray(Charsets.UTF_8))
            .toHexString()
        return "https://www.gravatar.com/avatar/${hashed}?s=60&d=404"
    }
}
