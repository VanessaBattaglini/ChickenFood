package com.daniel.chickenfood.presentation.activity.checkout

import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.OrderItemModel
import com.daniel.chickenfood.presentation.activity.dashboard.scrollIndicatorModifier

private const val TAG = "ConfirmationScreen"

@Composable
fun ConfirmationScreen(
    orderId: String = "",
    cartItems: List<OrderItemModel> = emptyList(),
    cartTotal: Double = 0.0,
    paymentMethod: String = "card",
    pointsBefore: Int = 0,
    pointsChange: Int = 0,
    pointsAfter: Int = 0,
    onBackClick: () -> Unit = {},
    onViewOrderClick: () -> Unit = {}
) {
    Log.d(TAG, "ConfirmationScreen rendering - orderId=$orderId, total=$cartTotal")

    var animateCheckmark by remember { mutableStateOf(false) }
    var animateContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateCheckmark = true
        animateContent = true
    }

    val checkmarkRotation by animateFloatAsState(
        targetValue = if (animateCheckmark) 360f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        ),
        label = "checkmark_rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.darkBrown))
    ) {
        // Header con icono de éxito animado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✅",
                fontSize = 80.sp,
                modifier = Modifier.rotate(checkmarkRotation)
            )
        }

        // Título de éxito
        Text(
            text = "¡ÉXITO!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        // Número de orden grande
        Text(
            text = orderId,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.orange),
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
        )

        // Contenido scrolleable
        val confirmationLazyListState = rememberLazyListState()
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .scrollIndicatorModifier(confirmationLazyListState)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = confirmationLazyListState,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Resumen de compra
            item {
                OrderSummaryCard(
                    items = cartItems,
                    totalPrice = cartTotal,
                    paymentMethod = paymentMethod,
                    orderId = orderId
                )
            }

            // Información de puntos (solo si se pagó con tarjeta y se ganaron puntos)
            if (paymentMethod == "card" && pointsChange > 0) {
                item {
                    PointsSummaryCard(
                        pointsBefore = pointsBefore,
                        pointsChange = pointsChange,
                        pointsAfter = pointsAfter,
                        isDeduction = false
                    )
                }
            }

            // Información de pago mixto (Puntos + Tarjeta)
            if (paymentMethod == "mixed") {
                item {
                    val POINTS_CONVERSION_RATE = 1.0  // 1 punto = 1 peso chileno
                    val pointsUsed = kotlin.math.abs(pointsChange)
                    val discountAmount = pointsUsed / POINTS_CONVERSION_RATE
                    val cardAmount = (cartTotal - discountAmount).coerceAtLeast(0.0)
                    
                    MixedPaymentSummaryCard(
                        cartTotal = cartTotal,
                        pointsUsed = pointsUsed,
                        cardAmount = cardAmount
                    )
                }
            }

            // Información de puntos (si se pagó con puntos o pago mixto)
            if (paymentMethod == "points" || paymentMethod == "mixed") {
                item {
                    PointsSummaryCard(
                        pointsBefore = pointsBefore,
                        pointsChange = pointsChange,
                        pointsAfter = pointsAfter,
                        isDeduction = true
                    )
                }
            }

            // Espacio
            item {
                Box(modifier = Modifier.height(24.dp))
            }
        }
        }

        // Footer con botones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón principal - Volver al Inicio
            Button(
                onClick = {
                    Log.d(TAG, "Back to home clicked")
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.orange)
                )
            ) {
                Text(
                    text = "Volver al Inicio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Botón secundario - Ver Detalle
            Button(
                onClick = {
                    Log.d(TAG, "View order detail clicked")
                    onViewOrderClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                )
            ) {
                Text(
                    text = "Ver Detalle de Orden",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }
        }
    }
}

/**
 * Preview para ConfirmationScreen
 */
@Composable
fun ConfirmationScreenPreview() {
    val sampleItems = listOf(
        OrderItemModel(
            foodId = 1,
            title = "Pollo Frito",
            price = 8.0,
            quantity = 2,
            subtotal = 16.0,
            imagePath = ""
        ),
        OrderItemModel(
            foodId = 2,
            title = "Papas",
            price = 3.0,
            quantity = 1,
            subtotal = 3.0,
            imagePath = ""
        )
    )

    ConfirmationScreen(
        orderId = "ORD_abc123_1718000000",
        cartItems = sampleItems,
        cartTotal = 19.97,
        paymentMethod = "card",
        pointsBefore = 500,
        pointsChange = 2,
        pointsAfter = 502,
        onBackClick = {},
        onViewOrderClick = {}
    )
}
