package com.lumio.lumiotelevison.data.remote

import com.google.gson.annotations.SerializedName
import com.lumio.lumiotelevison.data.model.NewsArticle

/**
 * Top-level response from NewsAPI.org /v2/top-headlines endpoint.
 */
data class TopHeadlinesResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int?,
    @SerializedName("articles") val articles: List<ArticleDto>?,
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
)

/**
 * Article DTO as returned by the News API.
 */
data class ArticleDto(
    @SerializedName("source") val source: SourceDto?,
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("content") val content: String?,
) {
    fun toNewsArticle(): NewsArticle = NewsArticle(
        sourceName = source?.name ?: "",
        author = author?.takeIf { it.isNotBlank() },
        title = title?.takeIf { it.isNotBlank() } ?: "(No title)",
        description = description?.takeIf { it.isNotBlank() },
        url = url ?: "",
        urlToImage = urlToImage?.takeIf { it.isNotBlank() },
        publishedAt = publishedAt ?: "",
        content = content?.takeIf { it.isNotBlank() },
    )
}

data class SourceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
)
