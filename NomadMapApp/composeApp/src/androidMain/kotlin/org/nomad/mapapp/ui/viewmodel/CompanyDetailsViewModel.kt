package org.nomad.mapapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nomad.mapapp.data.model.Company
import org.nomad.mapapp.data.repository.CompanyRepository
import org.nomad.mapapp.data.repository.UserRepository

class CompanyDetailsViewModel(
    private val companyRepository: CompanyRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _selectedCompany = MutableStateFlow<Company?>(null)
    val selectedCompany: StateFlow<Company?> = _selectedCompany.asStateFlow()

    private val _ratingState = MutableStateFlow<RatingState>(RatingState.Initial)
    val ratingState: StateFlow<RatingState> = _ratingState.asStateFlow()

    fun selectCompany(company: Company) {
        _selectedCompany.value = company
    }

    fun rateCompany(score: Float) {
        val company = _selectedCompany.value ?: return
        val user = userRepository.currentUser.value ?: return

        viewModelScope.launch {
            _ratingState.value = RatingState.Loading
            val success = companyRepository.rateCompany(company.id.toString(), score, user.id)
            _ratingState.value = if (success) {
                RatingState.Success
            } else {
                RatingState.Error("Error en enviar la valoració")
            }
        }
    }

    sealed class RatingState {
        object Initial : RatingState()
        object Loading : RatingState()
        object Success : RatingState()
        data class Error(val message: String) : RatingState()
    }
}