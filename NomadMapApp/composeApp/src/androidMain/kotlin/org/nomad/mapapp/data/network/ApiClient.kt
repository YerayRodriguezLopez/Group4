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

    // Authentication - Login expects hashed password, Register expects plain password
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

    suspend fun register(email: String, password: String): User? {
        return try {
            val response = client.post("$baseUrl/api/Users") {
                contentType(ContentType.Application.Json)
                setBody(RegisterUserModel(
                    email = email,
                    password = password, // Plain password - API will hash it
                    userName = email,
                    phoneNumber = null
                ))
            }
            response.body<User>()
        } catch (e: Exception) {
            println("Register error: ${e.message}")
            null
        }
    }

    // Companies - Using the exact response structure from your API
    suspend fun getCompanies(): List<Company> {
        return try {
            val response = client.get("$baseUrl/api/Companies")
            response.body<List<Company>>()
        } catch (e: Exception) {
            println("Get companies error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getCompany(id: Int): Company? {
        return try {
            val response = client.get("$baseUrl/api/Companies/$id")
            response.body<Company>()
        } catch (e: Exception) {
            println("Get company error: ${e.message}")
            null
        }
    }

    // Search companies using SearchController
    suspend fun searchCompanies(
        query: String? = null,
        isProvider: Boolean? = null,
        isRetail: Boolean? = null,
        minScore: Float? = null,
        tags: String? = null
    ): List<Company> {
        return try {
            val response = client.get("$baseUrl/api/Search/companies") {
                if (!query.isNullOrEmpty()) parameter("query", query)
                if (isProvider != null) parameter("isProvider", isProvider)
                if (isRetail != null) parameter("isRetail", isRetail)
                if (minScore != null) parameter("minScore", minScore)
                if (!tags.isNullOrEmpty()) parameter("tags", tags)
            }
            response.body<List<Company>>()
        } catch (e: Exception) {
            println("Search companies error: ${e.message}")
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
    suspend fun rateCompany(companyId: Int, score: Float, userId: String): Boolean {
        return try {
            val response = client.post("$baseUrl/api/Rates") {
                contentType(ContentType.Application.Json)
                setBody(Rate(userId = userId, companyId = companyId, score = score))
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            println("Rate company error: ${e.message}")
            false
        }
    }

    suspend fun getCompanyRates(companyId: Int): List<Rate> {
        return try {
            val response = client.get("$baseUrl/api/Rates/company/$companyId")
            response.body<List<Rate>>()
        } catch (e: Exception) {
            println("Get company rates error: ${e.message}")
            emptyList()
        }
    }

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