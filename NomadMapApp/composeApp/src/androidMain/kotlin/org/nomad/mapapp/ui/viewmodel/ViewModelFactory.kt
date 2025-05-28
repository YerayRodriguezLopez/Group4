package org.nomad.mapapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.nomad.mapapp.data.repository.CompanyRepository
import org.nomad.mapapp.data.repository.UserRepository

class ViewModelFactory(
    private val companyRepository: CompanyRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(companyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(CompanyDetailsViewModel::class.java) -> {
                CompanyDetailsViewModel(companyRepository, userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
