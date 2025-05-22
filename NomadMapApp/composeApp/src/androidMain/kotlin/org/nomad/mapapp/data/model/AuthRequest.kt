package org.nomad.mapapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = true
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String
)

@Serializable
data class AuthResponse(
    val token: String? = null,
    val success: Boolean = false,
    val message: String? = null
)