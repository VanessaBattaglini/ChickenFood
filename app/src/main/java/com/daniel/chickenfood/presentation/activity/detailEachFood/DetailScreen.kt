package com.daniel.chickenfood.presentation.activity.detailEachFood

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniel.chickenfood.domain.model.FoodModel

private const val TAG = "DetailScreen"

@Composable
fun DetailScreen(
    item: FoodModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onAddToCartClick: (quantity: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d(TAG, "DetailScreen rendering for item: ${item.title}")

    var quantity by remember {
        mutableIntStateOf(1)  // ✅ SIEMPRE empezar en 1, no usar item.numberInCart
    }

    Column(
        modifier = modifier
            .padding(top = 40.dp)
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Contenido scrolleable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            HeaderSection(
                item = item,
                quantity = quantity,
                onBackClick = onBackClick,
                onHomeClick = onHomeClick,
                onIncrement = {
                    Log.d(TAG, "Increment clicked, quantity: $quantity -> ${quantity + 1}")
                    quantity++
                },
                onDecrement = {
                    if (quantity > 1) {
                        Log.d(TAG, "Decrement clicked, quantity: $quantity -> ${quantity - 1}")
                        quantity--
                    }
                }
            )
            DescriptionSection(
                description = item.description
            )
        }
        // Footer fijo en la parte inferior
        Log.d(TAG, "Rendering FooterSection with totalPrice: ${item.price * quantity}")
        FooterSection(
            totalPrice = (item.price * quantity).toDouble(),
            onAddToCartClick = {
                Log.d(TAG, "FooterSection.onAddToCartClick called with quantity: $quantity")
                // Pasar la cantidad actualizada al callback
                onAddToCartClick(quantity)
            }
        )
    }
}
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DetailScreenPreview() {
    val comidaDePrueba = FoodModel(
        title = "Delicioso Pollo Frito",
        description = "Crujiente por fuera, jugoso por dentro. Acompañado de papas fritas y salsa de la casa hecha con ingredientes seleccionados.",
        price = 12000,
    )
    DetailScreen(
        item = comidaDePrueba,
        onBackClick = {},
        onHomeClick = {},
        onAddToCartClick = { cantidad ->
            Log.d("Preview_Debug", "Click en agregar al carrito: $cantidad")
        },
        modifier = Modifier
    )
}