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

    private val _existingRating = MutableStateFlow<Rate?>(null)

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
}