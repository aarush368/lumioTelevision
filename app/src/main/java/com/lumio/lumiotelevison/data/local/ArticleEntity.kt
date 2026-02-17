package com.lumio.lumiotelevison.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lumio.lumiotelevison.data.model.NewsArticle

/**
 * Room entity for offline caching of news articles.
 * Persists the last successful fetch so headlines are available without network.
 */
@Entity(tableName = "cached_articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceName: String,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
) {
    fun toNewsArticle(): NewsArticle = NewsArticle(
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
    )

    companion object {
        fun fromNewsArticle(a: NewsArticle): ArticleEntity = ArticleEntity(
            sourceName = a.sourceName,
            author = a.author,
            title = a.title,
            description = a.description,
            url = a.url,
            urlToImage = a.urlToImage,
            publishedAt = a.publishedAt,
            content = a.content,
        )
    }
}
