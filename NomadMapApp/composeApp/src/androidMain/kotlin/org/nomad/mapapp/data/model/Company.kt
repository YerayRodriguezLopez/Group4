package org.nomad.mapapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Company(
    val id: Int = 0,
    val nif: String,
    val name: String,
    val mail: String,
    val phone: Int,
    val tags: String,
    val score: Float,
    val isProvider: Boolean,
    val isRetail: Boolean,
    val addressId: Int = 0,
    val address: Address? = null,
    val ratingsCount: Int = 0,
    val averageRating: Float = 0f
) {
    fun getPhoneAsString(): String = phone.toString()
    fun getTagsList(): List<String> = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    fun getDisplayScore(): Float = if (ratingsCount > 0) averageRating else 0f
}