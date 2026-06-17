package com.daniel.chickenfood.presentation.activity.dashboard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "SearchBar"

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel(),
    onSearchResultClick: (FoodModel) -> Unit = {},
    onSearch: (String) -> Unit = {}
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    
    var text by rememberSaveable {
        mutableStateOf("")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    viewModel.searchFoods(it)
                    onSearch(it)
                },
                placeholder = {
                    Text(
                        text = "¿Qué deseas comer?",
                        fontFamily = FontFamily(Font(R.font.playwrite_ar_guides_regular)),
                        color = colorResource(R.color.darkBrown),
                        fontSize = 17.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search",
                        tint = colorResource(R.color.orange)
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            modifier = Modifier
                                .clickable {
                                    text = ""
                                    viewModel.clearSearch()
                                    onSearch("")
                                },
                            tint = colorResource(R.color.orange)
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.lightOrange),
                    unfocusedContainerColor = colorResource(R.color.grey),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = colorResource(R.color.orange),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            )
            
            // Resultados de búsqueda
            if (text.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(
                            color = colorResource(R.color.darkGray),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                ) {
                    when {
                        isSearching -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = colorResource(R.color.orange),
                                    modifier = Modifier
                                        .height(20.dp)
                                        .padding(end = 8.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    "Buscando...",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        searchResults.isEmpty() -> {
                            Text(
                                "No encontramos '${text}' en nuestro menú",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> {
                            val searchLazyListState = rememberLazyListState()
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp)
                                    .scrollIndicatorModifier(searchLazyListState)
                            ) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 300.dp),
                                    state = searchLazyListState
                                ) {
                                    items(searchResults) { food ->
                                        SearchResultItem(
                                            food = food,
                                            onItemClick = {
                                                Log.d(TAG, "Selected food: ${food.title}")
                                                text = ""
                                                viewModel.clearSearch()
                                                onSearchResultClick(food)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    food: FoodModel,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
            ) {
                Text(
                    text = food.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = food.description.take(50) + if (food.description.length > 50) "..." else "",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
            Text(
                text = "$${food.price}",
                color = colorResource(R.color.orange),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1B1B1B
)
@Composable
fun SearchBarPreview() {
    SearchBar()
}
