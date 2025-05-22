package org.nomad.mapapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nomad.mapapp.data.model.User
import org.nomad.mapapp.data.repository.UserRepository

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentUser.collect {
                _currentUser.value = it
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val response = repository.login(email, password)
            _loginState.value = if (response.success) {
                LoginState.Success
            } else {
                LoginState.Error(response.message ?: "Error d'autenticació")
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            val response = repository.register(email, password, confirmPassword)
            _registerState.value = if (response.success) {
                RegisterState.Success
            } else {
                RegisterState.Error(response.message ?: "Error de registre")
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Initial
    }

    fun logout() {
        repository.logout()
        _loginState.value = LoginState.Initial
    }

    fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }

    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    sealed class RegisterState {
        object Initial : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}