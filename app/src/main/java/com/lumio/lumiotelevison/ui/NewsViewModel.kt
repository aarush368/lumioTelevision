package com.lumio.lumiotelevison.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumio.lumiotelevison.data.model.NewsArticle
import com.lumio.lumiotelevison.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the news headlines screen.
 */
data class NewsUiState(
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

/**
 * ViewModel for the news screen. Loads headlines on init and supports refresh.
 */
class NewsViewModel(
    private val repository: NewsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadHeadlines()
    }

    /**
     * Loads or refreshes headlines from the API. Call this on long-press DPAD-Down.
     * On network error, keeps previous articles if available (offline cache).
     */
    fun loadHeadlines() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getTopHeadlines()
                .onSuccess { articles ->
                    _uiState.value = _uiState.value.copy(
                        articles = articles,
                        isLoading = false,
                        error = null,
                    )
                }
                .onFailure { e ->
                    val cached = repository.getCachedHeadlines()
                    _uiState.value = _uiState.value.copy(
                        articles = cached ?: _uiState.value.articles,
                        isLoading = false,
                        error = e.message ?: "Unknown error",
                    )
                }
        }
    }
}
