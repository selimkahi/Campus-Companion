package com.inci.campuscompanion.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inci.campuscompanion.data.EventsRepository
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

class EventsViewModel(private val repository: EventsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            _uiState.value = EventsUiState.Loading
            _uiState.value = try {
                val events = repository.getEvents()
                if (events.isEmpty()) EventsUiState.Empty else EventsUiState.Content(events)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: HttpException) {
                val message = if (exception.code() in 500..599) {
                    "The events server is unavailable. Please try again later."
                } else {
                    "The events request was rejected (HTTP ${exception.code()}). Please retry."
                }
                EventsUiState.Error(message)
            } catch (exception: IOException) {
                EventsUiState.Error("Cannot connect to the events server. Check your connection and retry.")
            } catch (exception: SerializationException) {
                exception.printStackTrace()
                EventsUiState.Error("The server returned invalid event data. Please retry.")
            } catch (exception: Exception) {
                exception.printStackTrace()
                EventsUiState.Error("Events could not be loaded. Please retry.")
            }
        }
    }

    companion object {
        fun Factory(repository: EventsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EventsViewModel(repository) as T
                }
            }
    }
}
