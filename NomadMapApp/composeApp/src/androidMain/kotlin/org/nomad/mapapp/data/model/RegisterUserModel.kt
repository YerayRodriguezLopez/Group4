package org.nomad.mapapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserModel(
    val email: String,
    val password: String,
    val userName: String,
    val phoneNumber: String? = null
)