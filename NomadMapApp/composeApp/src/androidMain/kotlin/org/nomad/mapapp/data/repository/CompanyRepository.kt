package org.nomad.mapapp.data.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.nomad.mapapp.data.model.Company
import org.nomad.mapapp.data.model.Rate
import org.nomad.mapapp.data.network.ApiClient

class CompanyRepository(private val apiClient: ApiClient) {
    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun fetchCompanies() {
        try {
            _isLoading.value = true
            _error.value = null
            val result = apiClient.getCompanies()
            _companies.value = result
            println("Fetched ${result.size} companies")
        } catch (e: Exception) {
            _error.value = "Error carregant empreses: ${e.message}"
            _companies.value = emptyList()
            println("Error fetching companies: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun fetchCompany(id: Int): Company? {
        return try {
            apiClient.getCompany(id)
        } catch (e: Exception) {
            println("Error fetching company $id: ${e.message}")
            null
        }
    }

    suspend fun fetchCompaniesNearby(lat: Float, lng: Float, distance: Float) {
        try {
            _isLoading.value = true
            _error.value = null
            val result = apiClient.getCompaniesNearby(lat, lng, distance)
            _companies.value = result
            println("Fetched ${result.size} nearby companies")
        } catch (e: Exception) {
            _error.value = "Error carregant empreses properes: ${e.message}"
            _companies.value = emptyList()
            println("Error fetching nearby companies: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun searchCompanies(
        query: String? = null,
        isProvider: Boolean? = null,
        isRetail: Boolean? = null,
        minScore: Float? = null,
        tags: String? = null
    ) {
        try {
            _isLoading.value = true
            _error.value = null
            val result = apiClient.searchCompanies(query, isProvider, isRetail, minScore, tags)
            _companies.value = result
            println("Search returned ${result.size} companies")
        } catch (e: Exception) {
            _error.value = "Error cercant empreses: ${e.message}"
            _companies.value = emptyList()
            println("Error searching companies: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun rateCompany(companyId: Int, score: Float, userId: String): Boolean {
        return apiClient.rateCompany(companyId, score, userId)
    }

    suspend fun getUserRating(companyId: Int, userId: String): Rate? {
        return apiClient.getUserRating(companyId, userId)
    }

    suspend fun getCompanyRates(companyId: Int): List<Rate> {
        return apiClient.getCompanyRates(companyId)
    }
}