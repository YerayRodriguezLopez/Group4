package org.nomad.mapapp.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.nomad.mapapp.data.model.*
import java.security.MessageDigest

class ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    // Update this with your actual API URL
    private val baseUrl = "https://group4apiapi.azure-api.net"

    // Hash password using SHA256 for login
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // Authentication
    suspend fun login(email: String, password: String): User? {
        return try {
            val hashedPassword = hashPassword(password)
            val response = client.get("$baseUrl/api/Users/$email,$hashedPassword")
            response.body<User>()
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            null
        }
    }

    suspend fun register(email: String, password: String, phoneNumber: String? = null): User? {
        return try {
            val response = client.post("$baseUrl/api/Users") {
                contentType(ContentType.Application.Json)
                setBody(RegisterUserModel(
                    email = email,
                    password = hashPassword(password),
                    userName = email,
                    phoneNumber = phoneNumber ?: "000000000"
                ))
            }
            response.body<User>()
        } catch (e: Exception) {
            println("Register error: ${e.message}")
            null
        }
    }

    // Companies
    suspend fun getCompanies(): List<Company> {
        return try {
            val response = client.get("$baseUrl/api/Companies")
            response.body<List<Company>>()
        } catch (e: Exception) {
            println("Get companies error: ${e.message}")
            emptyList()
        }
    }

    // Get nearby companies using SearchController
    suspend fun getCompaniesNearby(lat: Float, lng: Float, distance: Float = 5.0f): List<Company> {
        return try {
            val response = client.get("$baseUrl/api/Search/nearby") {
                parameter("lat", lat)
                parameter("lng", lng)
                parameter("distance", distance)
            }
            response.body<List<Company>>()
        } catch (e: Exception) {
            println("Get nearby companies error: ${e.message}")
            emptyList()
        }
    }

    // Ratings
    suspend fun getUserRates(userId: String): List<Rate> {
        return try {
            val response = client.get("$baseUrl/api/Rates/user/$userId")
            response.body<List<Rate>>()
        } catch (e: Exception) {
            println("Get user rates error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getUserRating(companyId: Int, userId: String): Rate? {
        return try {
            val userRates = getUserRates(userId)
            userRates.find { it.companyId == companyId }
        } catch (e: Exception) {
            println("Get user rating error: ${e.message}")
            null
        }
    }
}