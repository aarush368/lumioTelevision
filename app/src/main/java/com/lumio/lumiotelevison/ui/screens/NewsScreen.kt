package com.lumio.lumiotelevison.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.focusable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.lumio.lumiotelevison.ui.NewsUiState
import com.lumio.lumiotelevison.ui.components.NewsItem

/**
 * Title shown in the top bar.
 */
private const val TOP_BAR_TITLE = "New List"

/**
 * Hint shown at the bottom for long-press refresh. Visible on TV for discoverability.
 */
private const val REFRESH_HINT = "Long-press DPAD Down to refresh"

/**
 * Main news headlines screen: list of articles with expandable summaries,
 * loading and error states. Optimized for 1080p TV viewport.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NewsScreen(
    uiState: NewsUiState,
    modifier: Modifier = Modifier,
) {
    var expandedIds by remember { mutableStateOf(setOf<Int>()) }
    fun toggleExpanded(index: Int) {
        expandedIds = if (index in expandedIds) expandedIds - index else expandedIds + index
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        // Top bar with "New List" (below status bar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 32.dp, vertical = 20.dp),
        ) {
            Text(
                text = TOP_BAR_TITLE,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.articles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Loading headlines…",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                uiState.error != null && uiState.articles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = uiState.error ?: "Error",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(32.dp),
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .focusable(),
                        content = {
                            itemsIndexed(
                                items = uiState.articles,
                                key = { _, a -> a.url + a.publishedAt },
                            ) { index, article ->
                                NewsItem(
                                    article = article,
                                    expanded = expandedIds.contains(index),
                                    onClick = { toggleExpanded(index) },
                                )
                            }
                        },
                    )
                }
            }

            // Loading overlay when refreshing (we already have articles)
            if (uiState.isLoading && uiState.articles.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Refreshing…",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            // Refresh hint at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = REFRESH_HINT,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
