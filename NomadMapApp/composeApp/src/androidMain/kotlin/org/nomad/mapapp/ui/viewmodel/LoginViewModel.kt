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

    val currentUser: StateFlow<User?> = repository.currentUser
    val isLoading: StateFlow<Boolean> = repository.isLoading

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email i contrasenya són obligatoris")
            return
        }

        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                val loginSuccessful = repository.login(email, password)
                _loginState.value = if (loginSuccessful) {
                    LoginState.Success
                } else {
                    // Get the actual error from repository
                    val errorMessage = repository.error.value ?: "Credencials incorrectes"
                    LoginState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error inesperat: ${e.message}")
            }
        }
    }

    fun register(email: String, password: String, phoneNumber: String? = null) {
        if (email.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState.Error("Email i contrasenya són obligatoris")
            return
        }

        if (!isValidEmail(email)) {
            _registerState.value = RegisterState.Error("Format d'email invàlid")
            return
        }

        if (password.length < 6) {
            _registerState.value = RegisterState.Error("La contrasenya ha de tenir almenys 6 caràcters")
            return
        }

        viewModelScope.launch {
            try {
                _registerState.value = RegisterState.Loading
                val registerSuccessful = repository.register(email, password, phoneNumber)
                _registerState.value = if (registerSuccessful) {
                    RegisterState.Success
                } else {
                    // Get the actual error from repository
                    val errorMessage = repository.error.value ?: "Error de registre"
                    RegisterState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Error inesperat: ${e.message}")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Initial
        repository.clearError()
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Initial
        repository.clearError()
    }

    fun logout() {
        repository.logout()
        _loginState.value = LoginState.Initial
        _registerState.value = RegisterState.Initial
    }

    fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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