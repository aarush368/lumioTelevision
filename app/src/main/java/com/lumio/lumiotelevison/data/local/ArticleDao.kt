package com.lumio.lumiotelevison.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for cached news articles. Used for offline-first display.
 */
@Dao
interface ArticleDao {

    @Query("SELECT * FROM cached_articles ORDER BY publishedAt DESC")
    fun getAllFlow(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM cached_articles ORDER BY publishedAt DESC")
    suspend fun getAll(): List<ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM cached_articles")
    suspend fun deleteAll()
}
