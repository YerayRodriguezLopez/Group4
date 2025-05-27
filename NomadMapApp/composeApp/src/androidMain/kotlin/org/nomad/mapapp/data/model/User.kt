package org.nomad.mapapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val userName: String? = null,
    val normalizedUserName: String? = null,
    val email: String? = null,
    val normalizedEmail: String? = null,
    val emailConfirmed: Boolean = false,
    val passwordHash: String? = null,
    val securityStamp: String? = null,
    val concurrencyStamp: String? = null,
    val phoneNumber: String? = null,
    val phoneNumberConfirmed: Boolean = false,
    val twoFactorEnabled: Boolean = false,
    val lockoutEnd: String? = null,
    val lockoutEnabled: Boolean = false,
    val accessFailedCount: Int = 0,
    val rates: List<Rate>? = null
)