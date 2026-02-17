package com.lumio.lumiotelevison.data.repository

import android.content.Context
import com.lumio.lumiotelevison.BuildConfig
import com.lumio.lumiotelevison.data.local.ArticleEntity
import com.lumio.lumiotelevison.data.local.DatabaseProvider
import com.lumio.lumiotelevison.data.model.NewsArticle
import com.lumio.lumiotelevison.data.remote.NewsApiService
import com.lumio.lumiotelevison.data.remote.RetrofitClient
import com.lumio.lumiotelevison.data.remote.TopHeadlinesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository for fetching news headlines. Handles API errors and network failures,
 * maps DTOs to domain models. Caches to Room for offline and keeps in-memory for fast access.
 */
class NewsRepository(
    private val context: Context,
    private val api: NewsApiService = RetrofitClient.newsApi,
) {

    private val articleDao by lazy { DatabaseProvider.get(context).articleDao() }

    /** In-memory cache of last successful fetch for quick access. */
    @Volatile
    private var cachedArticles: List<NewsArticle>? = null

    /**
     * Returns cached articles from memory if available.
     */
    fun getCachedHeadlines(): List<NewsArticle>? = cachedArticles

    /**
     * Loads headlines from the local Room database (offline cache).
     */
    suspend fun getHeadlinesFromCache(): List<NewsArticle> = withContext(Dispatchers.IO) {
        articleDao.getAll().map { it.toNewsArticle() }
    }

    /**
     * Fetches top headlines from the API. On success, updates in-memory cache and Room.
     * On failure (e.g. no network), returns [getHeadlinesFromCache] if any, else the error.
     */
    suspend fun getTopHeadlines(): Result<List<NewsArticle>> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.NEWS_API_KEY
        if (apiKey.isBlank()) {
            val fromDb = getHeadlinesFromCache()
            return@withContext if (fromDb.isNotEmpty()) Result.success(fromDb)
            else Result.failure(SecurityException("News API key is not set. Add NEWS_API_KEY to gradle.properties or local.properties. Get a key at https://newsapi.org/register"))
        }
        try {
            val response: TopHeadlinesResponse = api.getTopHeadlines(apiKey = apiKey)
            when (response.status) {
                "ok" -> {
                    val articles = (response.articles ?: emptyList())
                        .map { it.toNewsArticle() }
                        .filter { it.title.isNotBlank() }
                    cachedArticles = articles
                    articleDao.deleteAll()
                    articleDao.insertAll(articles.map { ArticleEntity.fromNewsArticle(it) })
                    Result.success(articles)
                }
                else -> {
                    val fromDb = getHeadlinesFromCache()
                    if (fromDb.isNotEmpty()) Result.success(fromDb)
                    else Result.failure(ApiException(response.message ?: "Unknown API error", response.code))
                }
            }
        } catch (e: HttpException) {
            val fromDb = getHeadlinesFromCache()
            if (fromDb.isNotEmpty()) Result.success(fromDb)
            else {
                val body = e.response()?.errorBody()?.string()
                Result.failure(IOException("HTTP ${e.code()}: ${body ?: e.message()}"))
            }
        } catch (e: IOException) {
            val fromDb = getHeadlinesFromCache()
            if (fromDb.isNotEmpty()) Result.success(fromDb)
            else Result.failure(e)
        } catch (e: Exception) {
            val fromDb = getHeadlinesFromCache()
            if (fromDb.isNotEmpty()) Result.success(fromDb)
            else Result.failure(e)
        }
    }
}

/**
 * Thrown when the API returns status != "ok" (e.g. rate limit, invalid key).
 */
class ApiException(message: String, val code: String? = null) : Exception(message)
