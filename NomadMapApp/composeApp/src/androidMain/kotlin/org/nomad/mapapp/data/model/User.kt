package org.nomad.mapapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val userName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val passwordHash: String? = null
)