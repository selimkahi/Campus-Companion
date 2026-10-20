package com.inci.campuscompanion

import com.inci.campuscompanion.data.EventsRepository
import com.inci.campuscompanion.data.remote.CampusApi
import com.inci.campuscompanion.data.remote.EventDto
import com.inci.campuscompanion.data.remote.EventsRemoteDataSource
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class EventsRepositoryTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun apiFieldsMapToUiDataAndExtraFieldsAreIgnored() = runTest {
        val events = repositoryFor(
            """[{"id":"1","name":"Campus meetup","startingDate":"2026-10-20T12:00:00Z",
                "address":"Main hall","avatar":"unused","description":"unused"}]"""
        ).getEvents()

        assertEquals("1", events.single().id)
        assertEquals("Campus meetup", events.single().title)
        assertEquals("2026-10-20T12:00:00Z", events.single().timeLabel)
        assertEquals("Main hall", events.single().locationLabel)
    }

    @Test
    fun missingAndNullLocationsUseTba() = runTest {
        val events = repositoryFor(
            """[{"id":"1","name":"First","startingDate":"Today"},
                {"id":"2","name":"Second","startingDate":"Tomorrow","address":null}]"""
        ).getEvents()
        assertEquals(listOf("TBA", "TBA"), events.map { it.locationLabel })
    }

    @Test
    fun emptyJsonArrayStaysEmpty() = runTest {
        assertEquals(emptyList<Any>(), repositoryFor("[]").getEvents())
    }

    @Test(expected = SerializationException::class)
    fun missingRequiredFieldIsADataFailure() = runTest {
        repositoryFor("""[{"id":"1"}]""").getEvents()
        Unit
    }

    private fun repositoryFor(body: String): EventsRepository {
        val api = object : CampusApi {
            override suspend fun getEvents(): List<EventDto> = json.decodeFromString(body)
        }
        return EventsRepository(EventsRemoteDataSource(api))
    }
}
