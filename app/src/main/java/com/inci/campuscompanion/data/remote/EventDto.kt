package com.inci.campuscompanion.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: String,
    @SerialName("name") val title: String,
    @SerialName("startingDate") val startsAt: String,
    @SerialName("address") val location: String? = null
)
