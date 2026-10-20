package com.inci.campuscompanion.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EventsScreen(
    state: EventsUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Campus Events", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onRefresh, enabled = state !is EventsUiState.Loading) {
                Text("Refresh")
            }
        }

        when (state) {
            EventsUiState.Loading -> Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading events...")
                }
            }
            is EventsUiState.Content -> LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(state.events) { event ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(event.title, style = MaterialTheme.typography.titleMedium)
                            Text("Starts: ${event.timeLabel}")
                            Text("Location: ${event.locationLabel}")
                        }
                    }
                }
            }
            EventsUiState.Empty -> Box(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No campus events right now. Tap Refresh to check again.")
            }
            is EventsUiState.Error -> Box(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(state.message)
                    Button(onClick = onRefresh) { Text("Retry") }
                }
            }
        }
    }
}
