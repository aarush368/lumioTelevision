package com.lumio.lumiotelevison.data.model

import androidx.annotation.Keep

/**
 * Domain model for a news article displayed in the app.
 * Mapped from the News API response.
 */
@Keep
data class NewsArticle(
    val sourceName: String,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
)
