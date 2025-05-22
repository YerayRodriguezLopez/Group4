package org.nomad.mapapp.data.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: Int = 0,
    val location: String,
    val lat: Float,
    val lng: Float,
    val companyId: Int = 0
) {
    // Helper method to get coordinates as a LatLng object for Google Maps
    fun toLatLng(): LatLng = LatLng(lat.toDouble(), lng.toDouble())
}