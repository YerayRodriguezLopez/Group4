package org.nomad.mapapp.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nomad.mapapp.data.model.User
import org.nomad.mapapp.data.network.ApiClient

class UserRepository(private val apiClient: ApiClient) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val user = apiClient.login(email, password)
            _currentUser.value = user
            val success = user != null
            println("Login result: $success")
            success
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            false
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            val user = apiClient.register(email, password)
            val success = user != null
            println("Register result: $success")
            success
        } catch (e: Exception) {
            println("Register error: ${e.message}")
            false
        }
    }

    fun logout() {
        _currentUser.value = null
        System.gc()
    }

    fun isLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}