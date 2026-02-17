package com.lumio.lumiotelevison.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lumio.lumiotelevison.data.repository.NewsRepository

/**
 * Creates [NewsViewModel] with [NewsRepository] that has access to [Application] for Room.
 */
class NewsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != NewsViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        val repository = NewsRepository(application.applicationContext)
        return NewsViewModel(repository) as T
    }
}
