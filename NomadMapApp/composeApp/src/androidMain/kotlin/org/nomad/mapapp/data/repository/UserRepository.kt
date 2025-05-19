package org.nomad.mapapp.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nomad.mapapp.data.model.User
import org.nomad.mapapp.data.network.ApiClient

class UserRepository(private val apiClient: ApiClient) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    suspend fun login(email: String, password: String): Boolean {
        val user = apiClient.login(email, password)
        _currentUser.value = user
        return user != null
    }

    fun logout() {
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}
