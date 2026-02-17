package com.lumio.lumiotelevison.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.focusable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lumio.lumiotelevison.data.model.NewsArticle

private const val SUMMARY_PREVIEW_SENTENCES = 2
private const val IMAGE_SIZE_DP = 160

/**
 * Truncates [text] to approximately [sentenceCount] sentences for preview.
 */
fun truncateToSentences(text: String?, sentenceCount: Int = SUMMARY_PREVIEW_SENTENCES): String {
    if (text.isNullOrBlank()) return ""
    val sentences = text.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
    return if (sentences.size <= sentenceCount) text
    else sentences.take(sentenceCount).joinToString(" ").trim()
}

/**
 * A single news item: optional image, headline, and expandable summary.
 * Click (DPAD center) toggles expanded summary. Optimized for 1080p TV.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NewsItem(
    article: NewsArticle,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val summary = article.description ?: article.content ?: ""
    val previewSummary = truncateToSentences(summary)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .focusable()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Image: load at display size for memory efficiency; Coil memory + disk cache for retention
        if (!article.urlToImage.isNullOrBlank()) {
            val imageModifier = Modifier.size(IMAGE_SIZE_DP.dp, (IMAGE_SIZE_DP * 9f / 16f).dp)
            Box(
                modifier = Modifier
                    .width(IMAGE_SIZE_DP.dp)
                    .height((IMAGE_SIZE_DP * 9f / 16f).dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(article.urlToImage)
                        .size(320, 180)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                    placeholder = {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        )
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        )
                    },
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (expanded) summary.ifBlank { previewSummary } else previewSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) 20 else 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (!expanded && summary.isNotBlank() && summary != previewSummary) {
                Text(
                    text = "Press OK to expand",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
