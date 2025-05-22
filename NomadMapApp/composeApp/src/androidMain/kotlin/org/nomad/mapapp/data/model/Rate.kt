package org.nomad.mapapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Rate(
    val id: Int = 0,
    val userId: String,
    val companyId: Int,
    val score: Float
)