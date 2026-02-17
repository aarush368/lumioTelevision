package com.lumio.lumiotelevison

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.lifecycle.ViewModelProvider
import com.lumio.lumiotelevison.ui.NewsViewModelFactory
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.lumio.lumiotelevison.ui.NewsViewModel
import com.lumio.lumiotelevison.ui.screens.NewsScreen
import com.lumio.lumiotelevison.ui.theme.LumioTelevisonTheme

class MainActivity : ComponentActivity() {

    private lateinit var newsViewModel: NewsViewModel
    private val handler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null

    private companion object {
        const val LONG_PRESS_DURATION_MS = 800L
    }

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
        newsViewModel = ViewModelProvider(this, NewsViewModelFactory(application))[NewsViewModel::class.java]
        setContent {
            LumioTelevisonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                ) {
                    val uiState by newsViewModel.uiState.collectAsStateWithLifecycle()
                    NewsScreen(uiState = uiState)
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            when (event.action) {
                KeyEvent.ACTION_DOWN -> {
                    longPressRunnable?.let { handler.removeCallbacks(it) }
                    longPressRunnable = Runnable {
                        longPressRunnable = null
                        newsViewModel.loadHeadlines()
                    }.also { handler.postDelayed(it, LONG_PRESS_DURATION_MS) }
                }
                KeyEvent.ACTION_UP -> {
                    longPressRunnable?.let {
                        handler.removeCallbacks(it)
                        longPressRunnable = null
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
