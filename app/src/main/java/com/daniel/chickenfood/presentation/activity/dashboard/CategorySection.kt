package com.daniel.chickenfood.presentation.activity.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.CategoryModel

@Composable
fun CategorySection(
    categories: List<CategoryModel>,
    isLoading: Boolean,
    onCategoryClick: (CategoryModel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Escoge una categoría",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.white),
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
        )
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(R.color.orange)
                )
            }
        } else {
            val rows = categories.chunked(3)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { category ->
                            CategoryItem(
                                category = category,
                                onClick = { onCategoryClick(category) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: CategoryModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colorResource(R.color.lightOrange))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = category.imagePath,
            contentDescription = category.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(70.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.darkPurple),
            fontWeight = FontWeight.Bold
        )
    }
}

