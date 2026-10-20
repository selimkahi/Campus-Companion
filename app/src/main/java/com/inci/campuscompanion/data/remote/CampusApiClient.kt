package com.inci.campuscompanion.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object CampusApiClient {
    private const val BASE_URL = "https://6ad70838f71f154ac4879fac.mockapi.io/api/v1/events/"
    private val json = Json { ignoreUnknownKeys = true }

    val api: CampusApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        .build()
        .create(CampusApi::class.java)
}
