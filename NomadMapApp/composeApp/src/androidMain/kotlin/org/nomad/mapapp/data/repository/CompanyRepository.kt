package org.nomad.mapapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nomad.mapapp.data.model.Company
import org.nomad.mapapp.data.network.ApiClient

class CompanyRepository(private val apiClient: ApiClient) {
    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: Flow<List<Company>> = _companies.asStateFlow()

    suspend fun fetchCompanies() {
        try {
            val result = apiClient.getCompanies()
            _companies.value = result
        } catch (e: Exception) {
            // Handle error
            _companies.value = emptyList() // Reset to empty list on error
        }
    }

    suspend fun fetchCompaniesNearby(lat: Double, lng: Double, radius: Double) {
        try {
            val result = apiClient.getCompaniesNearby(lat, lng, radius)
            _companies.value = result
        } catch (e: Exception) {
            // Handle error
            _companies.value = emptyList() // Reset to empty list on error
        }
    }

    suspend fun rateCompany(companyId: String, score: Float, userId: String): Boolean {
        return apiClient.rateCompany(companyId, score, userId)
    }
}