package org.nomad.mapapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.nomad.mapapp.data.model.Company
import org.nomad.mapapp.data.repository.CompanyRepository

class MapViewModel(private val repository: CompanyRepository) : ViewModel() {
    // Store all companies (unfiltered)
    private val _allCompanies = MutableStateFlow<List<Company>>(emptyList())

    // Store displayed companies (filtered)
    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()

    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    val selectedTags: StateFlow<Set<String>> = _selectedTags.asStateFlow()

    private val _lastMapPosition = MutableStateFlow<LatLng?>(null)
    val lastMapPosition: StateFlow<LatLng?> = _lastMapPosition.asStateFlow()

    private val _isDaltonismMode = MutableStateFlow(false)
    val isDaltonismMode: StateFlow<Boolean> = _isDaltonismMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Collect loading and error states from repository
        viewModelScope.launch {
            repository.isLoading.collect {
                _isLoading.value = it
            }
        }

        viewModelScope.launch {
            repository.error.collect {
                _error.value = it
            }
        }
    }

    fun loadCompanies() {
        viewModelScope.launch {
            try {
                _error.value = null
                val fetchedCompanies = repository.fetchCompanies()
                _allCompanies.value = fetchedCompanies
                // Apply current filters
                applyFilters()
                println("MapViewModel: Loaded ${fetchedCompanies.size} companies")
            } catch (e: Exception) {
                _error.value = "Error loading companies: ${e.message}"
                println("MapViewModel: Error loading companies: ${e.message}")
            }
        }
    }

    fun fetchCompaniesNearby(lat: Float, lng: Float, distance: Float = 5.0f) {
        viewModelScope.launch {
            try {
                _error.value = null
                println("Fetching companies nearby: lat=$lat, lng=$lng, distance=$distance")
                repository.fetchCompaniesNearby(lat, lng, distance)
                // Get the updated companies from repository
                val nearbyCompanies = repository.companies.value
                _allCompanies.value = nearbyCompanies
                // Apply current filters
                applyFilters()
                println("MapViewModel: Loaded ${nearbyCompanies.size} nearby companies")
            } catch (e: Exception) {
                _error.value = "Error loading nearby companies: ${e.message}"
                println("MapViewModel: Error loading nearby companies: ${e.message}")
            }
        }
    }

    fun updateMapPosition(position: LatLng) {
        _lastMapPosition.value = position
    }

    fun updateSelectedTags(tags: Set<String>) {
        _selectedTags.value = tags
        applyFilters()
        println("MapViewModel: Applied filters for tags: $tags")
    }

    private fun applyFilters() {
        val tags = _selectedTags.value
        val allCompanies = _allCompanies.value

        val filteredCompanies = if (tags.isEmpty()) {
            allCompanies
        } else {
            allCompanies.filter { company ->
                val companyTags = company.getTagsList()
                println("Company ${company.name} has tags: $companyTags")
                tags.any { selectedTag ->
                    companyTags.any { companyTag ->
                        companyTag.equals(selectedTag, ignoreCase = true)
                    }
                }
            }
        }

        _companies.value = filteredCompanies
        println("MapViewModel: Filtered companies count: ${filteredCompanies.size} from ${allCompanies.size} total")

        if (filteredCompanies.isEmpty() && allCompanies.isNotEmpty()) {
            println("MapViewModel: No companies match the selected filters")
        }
    }

    fun toggleDaltonismMode() {
        _isDaltonismMode.value = !_isDaltonismMode.value
    }

    fun getFilteredCompanies(): List<Company> {
        return _companies.value
    }

    fun getCompaniesSortedByProximity(userLat: Float, userLng: Float): List<Company> {
        return _companies.value.sortedBy { company ->
            company.address?.let { address ->
                val latDiff = address.lat - userLat
                val lngDiff = address.lng - userLng
                kotlin.math.sqrt((latDiff * latDiff + lngDiff * lngDiff).toDouble()).toFloat()
            } ?: Float.MAX_VALUE
        }
    }
}