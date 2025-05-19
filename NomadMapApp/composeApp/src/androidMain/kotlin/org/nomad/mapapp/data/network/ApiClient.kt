package org.nomad.mapapp.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.nomad.mapapp.data.model.Company
import org.nomad.mapapp.data.model.User

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
        return client.get("$baseUrl/companies").body()
    }

    suspend fun getCompaniesNearby(lat: Double, lng: Double, radius: Double): List<Company> {
        return client.get("$baseUrl/companies/nearby") {
            parameter("lat", lat)
            parameter("lng", lng)
            parameter("radius", radius)
        }.body()
    }

    suspend fun login(email: String, password: String): User? {
        return try {
            client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "password" to password))
            }.body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun rateCompany(companyId: String, score: Float, userId: String): Boolean {
        return try {
            client.post("$baseUrl/companies/$companyId/rate") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("score" to score, "userId" to userId))
            }.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }
}
