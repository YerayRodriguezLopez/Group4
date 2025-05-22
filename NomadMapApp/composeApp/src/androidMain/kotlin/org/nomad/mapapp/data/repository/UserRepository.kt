package org.nomad.mapapp.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nomad.mapapp.data.model.AuthResponse
import org.nomad.mapapp.data.model.User
import org.nomad.mapapp.data.network.ApiClient

class UserRepository(private val apiClient: ApiClient) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken = _authToken.asStateFlow()

    suspend fun login(email: String, password: String): AuthResponse {
        val response = apiClient.login(email, password)
        if (response.success && response.token != null) {
            _authToken.value = response.token
            _currentUser.value = User(id = "", email = email) // In a real app, you'd decode the JWT token
        }
        return response
    }

    suspend fun register(email: String, password: String, confirmPassword: String): AuthResponse {
        return apiClient.register(email, password, confirmPassword)
    }

    fun logout() {
        _currentUser.value = null
        _authToken.value = null
    }

    fun isLoggedIn(): Boolean {
        return _authToken.value != null
    }
}