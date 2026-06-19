package com.daniel.chickenfood.presentation.activity.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.daniel.chickenfood.R

/**
 * ScrollIndicator - Visual indicator showing scroll position
 * Displays a vertical bar on the right side when content is scrollable
 * 
 * Usage: Add this modifier to your LazyColumn parent Box
 * Box(modifier = scrollIndicatorModifier(lazyListState))
 * 
 * @param lazyListState The state of the LazyColumn to monitor
 */
fun scrollIndicatorModifier(lazyListState: LazyListState): Modifier {
    return Modifier.drawWithCache {
        val layoutInfo = lazyListState.layoutInfo
        val isScrollable = layoutInfo.totalItemsCount > 0 && 
            layoutInfo.visibleItemsInfo.isNotEmpty() &&
            layoutInfo.visibleItemsInfo.last().index < layoutInfo.totalItemsCount - 1
        
        // Calculate scroll progress
        val scrollProgress = if (layoutInfo.totalItemsCount == 0) {
            0f
        } else {
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                0f
            } else {
                val firstVisibleIndex = visibleItemsInfo.first().index.toFloat()
                val totalItems = layoutInfo.totalItemsCount.toFloat()
                (firstVisibleIndex / (totalItems - 1)).coerceIn(0f, 1f)
            }
        }
        
        val indicatorColor = if (isScrollable) 
            Color(0xFF00FF00).copy(alpha = 0.9f)  // Bright lime green with high visibility
        else 
            Color.Transparent
        
        onDrawWithContent {
            drawContent()
            
            if (isScrollable) {
                val indicatorWidth = 8f  // Increased from 4dp to 8dp for better visibility
                val indicatorHeight = size.height * 0.15f
                val thumbY = scrollProgress * (size.height - indicatorHeight)
                
                // Draw faint background track
                drawRect(
                    color = Color(0xFF00FF00).copy(alpha = 0.15f),
                    topLeft = Offset(
                        x = size.width - indicatorWidth - 4,
                        y = 0f
                    ),
                    size = Size(indicatorWidth, size.height)
                )
                
                // Draw scroll indicator thumb
                drawRect(
                    color = indicatorColor,
                    topLeft = Offset(
                        x = size.width - indicatorWidth - 4,
                        y = thumbY
                    ),
                    size = Size(indicatorWidth, indicatorHeight)
                )
            }
        }
    }
}
