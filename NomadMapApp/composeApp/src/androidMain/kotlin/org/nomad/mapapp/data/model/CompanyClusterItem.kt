package org.nomad.mapapp.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class CompanyClusterItem(
    val company: Company,
    private val position: LatLng = company.address?.toLatLng() ?: LatLng(0.0, 0.0)
) : ClusterItem {
    override fun getPosition(): LatLng = position
    override fun getTitle(): String = company.name
    override fun getSnippet(): String = "${company.address?.location ?: ""} - ★ ${company.getDisplayScore()}"
    override fun getZIndex(): Float = 0f
}