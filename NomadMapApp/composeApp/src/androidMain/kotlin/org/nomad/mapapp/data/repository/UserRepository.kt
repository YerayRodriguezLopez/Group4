package org.nomad.mapapp.data.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nomad.mapapp.data.model.User
import org.nomad.mapapp.data.network.ApiClient

class UserRepository(private val apiClient: ApiClient) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun login(email: String, password: String): Boolean {
        return try {
            _isLoading.value = true
            _error.value = null
            val user = apiClient.login(email, password)
            _currentUser.value = user
            val success = user != null
            if (!success) {
                _error.value = "Credencials incorrectes"
            }
            println("Login result: $success")
            success
        } catch (e: Exception) {
            _error.value = "Error de connexió: ${e.message}"
            println("Login error: ${e.message}")
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            _isLoading.value = true
            _error.value = null
            val user = apiClient.register(email, password)
            val success = user != null
            if (!success) {
                _error.value = "Error de registre"
            }
            println("Register result: $success")
            success
        } catch (e: Exception) {
            _error.value = "Error de connexió: ${e.message}"
            println("Register error: ${e.message}")
            false
        } finally {
            _isLoading.value = false
        }
    }

    fun logout() {
        _currentUser.value = null
        _error.value = null
        System.gc()
    }

    fun isLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}