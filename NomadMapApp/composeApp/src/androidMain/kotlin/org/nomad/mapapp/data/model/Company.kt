package org.nomad.mapapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Company(
    val id: String,
    val nif: String,
    val name: String,
    val address: Address,
    val mail: String,
    val phone: String,
    val tags: List<String>,
    val score: Float,
    val isProvider: Boolean,
    val isRetail: Boolean
)

@Serializable
data class Address(
    val location: String,
    val coordinates: Coordinates
)

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)