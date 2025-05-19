package org.nomad.mapapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String,
    val id: String,
    val userName: String,
    val phoneNumber: String? = null
)