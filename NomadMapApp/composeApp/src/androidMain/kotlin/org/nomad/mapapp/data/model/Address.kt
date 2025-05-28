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
    fun toLatLng(): LatLng = LatLng(lat.toDouble(), lng.toDouble())
}