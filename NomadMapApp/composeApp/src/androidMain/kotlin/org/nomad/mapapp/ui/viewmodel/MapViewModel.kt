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
        // Collect from repository StateFlows
        viewModelScope.launch {
            _companies.value = repository.fetchCompanies()
        }

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
            repository.fetchCompanies()
        }
    }

    fun fetchCompaniesNearby(lat: Float, lng: Float, distance: Float = 5.0f) {
        viewModelScope.launch {
            println("Fetching companies nearby: lat=$lat, lng=$lng, distance=$distance")
            repository.fetchCompaniesNearby(lat, lng, distance)
        }
    }

    fun updateMapPosition(position: LatLng) {
        _lastMapPosition.value = position
    }

    fun updateSelectedTags(tags: Set<String>) {
        _selectedTags.value = tags
        if (tags.isNotEmpty()) {
            viewModelScope.launch {
                _companies.value = repository.searchCompanies(tags = tags.joinToString(","))
            }
        } else {
            loadCompanies()
        }
    }

    fun toggleDaltonismMode() {
        _isDaltonismMode.value = !_isDaltonismMode.value
    }

    fun getFilteredCompanies(): List<Company> {
        val tags = _selectedTags.value
        val filtered = if (tags.isEmpty()) {
            _companies.value
        } else {
            _companies.value.filter { company ->
                company.getTagsList().any { it in tags }
            }
        }
        return if (filtered.isEmpty()) _companies.value else filtered
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