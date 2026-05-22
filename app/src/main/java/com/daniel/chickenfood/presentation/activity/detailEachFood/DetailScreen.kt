package com.daniel.chickenfood.presentation.activity.detailEachFood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daniel.chickenfood.domain.model.FoodModel

@Composable
fun DetailScreen(
    item: FoodModel,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var quantity by remember {
        mutableIntStateOf(
            maxOf(1, item.numberInCart)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(bottom = 100.dp)
        ) {

            HeaderSection(
                item = item,
                quantity = quantity,
                onBackClick = onBackClick,
                onIncrease = {
                    quantity++
                },
                onDecrease = {
                    if (quantity > 1) {
                        quantity--
                    }
                }
            )

            DescriptionSection(
                description = item.description
            )
        }

        FooterSection(
            totalPrice = item.price * quantity,
            onAddToCartClick = onAddToCartClick,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }
}
