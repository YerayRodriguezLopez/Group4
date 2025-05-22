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
    val address: Address? = null
) {
    // Parse phone number to string when needed
    fun getPhoneAsString(): String = phone.toString()

    // Parse tags CSV to list
    fun getTagsList(): List<String> = tags.split(",").map { it.trim() }
}