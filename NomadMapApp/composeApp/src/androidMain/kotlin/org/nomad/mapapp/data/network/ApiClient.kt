package org.nomad.mapapp.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.nomad.mapapp.data.model.*

class ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private val baseUrl = "https://api.example.com" // Replace with your actual API URL

    suspend fun getCompanies(): List<Company> {
        return client.get("$baseUrl/api/Companies").body()
    }

    suspend fun getCompaniesNearby(lat: Double, lng: Double, radius: Double): List<Company> {
        return client.get("$baseUrl/api/Companies/nearby") {
            parameter("lat", lat)
            parameter("lng", lng)
            parameter("radius", radius)
        }.body()
    }

    suspend fun login(email: String, password: String): AuthResponse {
        return try {
            client.post("$baseUrl/api/Auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password, true))
            }.body()
        } catch (e: Exception) {
            AuthResponse(success = false, message = e.message)
        }
    }

    suspend fun register(email: String, password: String, confirmPassword: String): AuthResponse {
        return try {
            client.post("$baseUrl/api/Auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(email, password, confirmPassword))
            }.body()
        } catch (e: Exception) {
            AuthResponse(success = false, message = e.message)
        }
    }

    suspend fun rateCompany(companyId: Int, score: Float, userId: String): Boolean {
        return try {
            client.post("$baseUrl/api/Rates") {
                contentType(ContentType.Application.Json)
                setBody(Rate(userId = userId, companyId = companyId, score = score))
            }.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }
}