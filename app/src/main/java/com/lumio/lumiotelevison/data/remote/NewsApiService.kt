package com.lumio.lumiotelevison.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for NewsAPI.org v2 top-headlines endpoint.
 * See: https://newsapi.org/docs/endpoints/top-headlines
 */
interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("pageSize") pageSize: Int = 30,
        @Query("apiKey") apiKey: String,
    ): TopHeadlinesResponse
}
