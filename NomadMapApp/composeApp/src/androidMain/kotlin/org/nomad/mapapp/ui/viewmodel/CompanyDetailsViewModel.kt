package org.nomad.mapapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nomad.mapapp.data.model.Company
import org.nomad.mapapp.data.model.Rate
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

    private val _existingRating = MutableStateFlow<Rate?>(null)
    val existingRating: StateFlow<Rate?> = _existingRating.asStateFlow()

    fun selectCompany(company: Company) {
        _selectedCompany.value = company
        // Check if user has already rated this company
        val user = userRepository.currentUser.value
        if (user != null) {
            checkExistingRating(company.id, user.id)
        }
    }

    fun checkExistingRating(companyId: Int, userId: String) {
        viewModelScope.launch {
            val rating = companyRepository.getUserRating(companyId, userId)
            _existingRating.value = rating
        }
    }

    fun rateCompany(score: Float) {
        val company = _selectedCompany.value ?: return
        val user = userRepository.currentUser.value ?: return

        viewModelScope.launch {
            _ratingState.value = RatingState.Loading
            val success = companyRepository.rateCompany(company.id, score, user.id)
            _ratingState.value = if (success) {
                // Update existing rating
                _existingRating.value = Rate(
                    userId = user.id,
                    companyId = company.id,
                    score = score
                )
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