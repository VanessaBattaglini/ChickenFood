package com.daniel.chickenfood.presentation.activity.itemList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.presentation.activity.dashboard.scrollIndicatorModifier

@Composable
fun ItemsList(
    items: List<FoodModel>,
    onFoodClick: (FoodModel) -> Unit
) {
    val lazyListState = rememberLazyListState()  // ✨ NUEVO: State para scroll indicator
    
    Box(
        modifier = Modifier
            .drawWithCache {  // ✨ NUEVO: Inline scroll indicator
                val layoutInfo = lazyListState.layoutInfo
                val isScrollable = layoutInfo.totalItemsCount > 0 && 
                    layoutInfo.visibleItemsInfo.isNotEmpty() &&
                    layoutInfo.visibleItemsInfo.last().index < layoutInfo.totalItemsCount - 1
                
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
                    Color(0xFF00FF00).copy(alpha = 0.9f)
                else 
                    Color.Transparent
                
                onDrawWithContent {
                    drawContent()
                    
                    if (isScrollable) {
                        val indicatorWidth = 12f
                        val indicatorHeight = size.height * 0.12f
                        val thumbY = scrollProgress * (size.height - indicatorHeight)
                        
                        // Draw faint background track
                        drawRect(
                            color = Color(0xFF00FF00).copy(alpha = 0.15f),
                            topLeft = Offset(
                                x = size.width - indicatorWidth - 2,
                                y = 0f
                            ),
                            size = Size(indicatorWidth, size.height)
                        )
                        
                        // Draw scroll indicator thumb
                        drawRect(
                            color = indicatorColor,
                            topLeft = Offset(
                                x = size.width - indicatorWidth - 2,
                                y = thumbY
                            ),
                            size = Size(indicatorWidth, indicatorHeight)
                        )
                    }
                }
            }
    ) {
        LazyColumn(
            state = lazyListState,  // ✨ NUEVO: State para scroll tracking
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            itemsIndexed(
                items = items,
                key = { _, food -> food.id }
            ) { index, food ->

                FoodCard(
                    food = food,
                    isImageLeft = index % 2 == 0,
                    onClick = {
                        onFoodClick(food)
                    }
                )
            }
        }
    }
}

@Composable
fun FoodCard(
    food: FoodModel,
    isImageLeft: Boolean,
    onClick: () -> Unit
) {

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row {
            if (isImageLeft) {
                FoodImage(food)
                FoodDetail(food)
            } else {
                FoodDetail(food)
                FoodImage(food)
            }
        }
    }
}

@Composable
private fun FoodImage(
    food: FoodModel
) {

    AsyncImage(
        model = food.imagePath,
        contentDescription = food.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(130.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
private fun RowScope.FoodDetail(
    food: FoodModel
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(16.dp)
    ) {
        Text(
            text = food.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        FoodInfoRow(
            icon = R.drawable.time,
            text = "${food.timeValue} min"
        )
        FoodInfoRow(
            icon = R.drawable.star,
            text = food.star.toString()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$${food.price}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
@Composable
fun FoodInfoRow(
    icon: Int,
    text: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}