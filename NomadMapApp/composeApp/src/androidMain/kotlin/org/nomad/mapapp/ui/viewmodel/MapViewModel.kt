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

    private val _searchRadius = MutableStateFlow(5.0) // 5 km default
    val searchRadius: StateFlow<Double> = _searchRadius.asStateFlow()

    private val _lastMapPosition = MutableStateFlow<LatLng?>(null)
    val lastMapPosition: StateFlow<LatLng?> = _lastMapPosition.asStateFlow()

    private val _isDaltonismMode = MutableStateFlow(false)
    val isDaltonismMode: StateFlow<Boolean> = _isDaltonismMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.companies.collect {
                _companies.value = it
            }
        }
    }

    fun fetchCompaniesNearby(lat: Double, lng: Double) {
        viewModelScope.launch {
            repository.fetchCompaniesNearby(lat, lng, _searchRadius.value)
        }
    }

    fun updateSearchRadius(radius: Double) {
        _searchRadius.value = radius
        _lastMapPosition.value?.let {
            fetchCompaniesNearby(it.latitude, it.longitude)
        }
    }

    fun updateMapPosition(position: LatLng) {
        _lastMapPosition.value = position
    }

    fun updateSelectedTags(tags: Set<String>) {
        _selectedTags.value = tags
    }

    fun toggleDaltonismMode() {
        _isDaltonismMode.value = !_isDaltonismMode.value
    }

    fun getFilteredCompanies(): List<Company> {
        return if (_selectedTags.value.isEmpty()) {
            _companies.value
        } else {
            _companies.value.filter { company ->
                company.getTagsList().any { tag -> tag in _selectedTags.value }
            }
        }
    }
}