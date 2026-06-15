package com.daniel.chickenfood.presentation.activity.checkout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.OrderItemModel

private const val TAG = "CheckoutScreen"

@Composable
fun CheckoutScreen(
    cartItems: List<OrderItemModel> = emptyList(),
    cartTotal: Double = 0.0,
    userPoints: Int = 0,
    isLoading: Boolean = false,
    error: String? = null,
    onBackClick: () -> Unit = {},
    onConfirmPayment: (paymentMethod: String, cardData: Map<String, String>?) -> Unit = { _, _ -> }
) {
    Log.d(TAG, "CheckoutScreen rendering - items=${cartItems.size}, total=$cartTotal, points=$userPoints")

    var selectedPaymentMethod by remember { mutableStateOf("card") }
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var pointsToUse by remember { mutableStateOf(calculatePointsNeeded(cartTotal)) }
    var errorMessage by remember { mutableStateOf(error) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.darkBrown))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.back_grey),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Confirmar Compra",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        // Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Resumen del Carrito
            item {
                CartSummaryCard(
                    items = cartItems,
                    totalPrice = cartTotal
                )
            }

            // Información de Puntos
            item {
                PointsInfoCard(
                    currentPoints = userPoints
                )
            }

            // Selector de Método de Pago
            item {
                Text(
                    text = "Método de Pago",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Opción 1: Pagar con Tarjeta
            item {
                PaymentMethodCard(
                    title = "💳 Pagar con Tarjeta",
                    description = "Tarjeta de crédito/débito",
                    isSelected = selectedPaymentMethod == "card",
                    onClick = {
                        Log.d(TAG, "Selected payment method: card")
                        selectedPaymentMethod = "card"
                    }
                )
            }

            // Formulario de Tarjeta (solo visible si está seleccionada)
            if (selectedPaymentMethod == "card") {
                item {
                    CardFormSection(
                        cardNumber = cardNumber,
                        onCardNumberChange = { cardNumber = it },
                        cardHolder = cardHolder,
                        onCardHolderChange = { cardHolder = it },
                        expiryDate = expiryDate,
                        onExpiryDateChange = { expiryDate = formatExpiryDate(it) },
                        cvc = cvc,
                        onCvcChange = { cvc = it }
                    )
                }
            }

            // Opción 2: Pagar con Puntos
            item {
                PaymentMethodCard(
                    title = "💎 Pagar con Puntos",
                    description = "Usa tus puntos acumulados",
                    isSelected = selectedPaymentMethod == "points",
                    onClick = {
                        Log.d(TAG, "Selected payment method: points")
                        selectedPaymentMethod = "points"
                    }
                )
            }

            // Información de Puntos para Pago
            if (selectedPaymentMethod == "points") {
                item {
                    PointsPaymentInfo(
                        pointsNeeded = pointsToUse,
                        pointsAvailable = userPoints
                    )
                }
            }

            // Espacio para el botón
            item {
                Box(modifier = Modifier.height(24.dp))
            }
        }

        // Mensajes de Error
        if (errorMessage != null && errorMessage!!.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF6B6B), RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Footer con Botones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón Confirmar
            Button(
                onClick = {
                    Log.d(TAG, "Confirm payment clicked - method=$selectedPaymentMethod")
                    when {
                        isLoading -> {
                            // No hacer nada si está cargando
                        }
                        selectedPaymentMethod == "card" -> {
                            val isValid = validateCardData(cardNumber, cardHolder, expiryDate, cvc)
                            if (isValid) {
                                val cardData = mapOf(
                                    "cardNumber" to cardNumber,
                                    "cardHolder" to cardHolder,
                                    "expiryDate" to expiryDate,
                                    "cvc" to cvc
                                )
                                onConfirmPayment("card", cardData)
                            } else {
                                errorMessage = "Por favor verifica los datos de la tarjeta"
                            }
                        }
                        selectedPaymentMethod == "points" -> {
                            if (userPoints >= pointsToUse) {
                                onConfirmPayment("points", null)
                            } else {
                                errorMessage = "No tienes suficientes puntos para esta compra"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading && (
                    (selectedPaymentMethod == "card" && cardNumber.isNotEmpty()) ||
                    (selectedPaymentMethod == "points" && userPoints >= pointsToUse)
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.orange),
                    disabledContainerColor = Color.Gray
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Procesando...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Confirmar Pago",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Botón Cancelar
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text(
                    text = "Cancelar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }
        }
    }
}

/**
 * Sección de formulario de tarjeta
 */
@Composable
private fun CardFormSection(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardHolder: String,
    onCardHolderChange: (String) -> Unit,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    cvc: String,
    onCvcChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Número de tarjeta
        CheckoutInputField(
            value = cardNumber,
            onValueChange = onCardNumberChange,
            label = "Número de Tarjeta",
            placeholder = "1234 5678 9012 3456",
            isValid = isValidCardNumber(cardNumber),
            errorMessage = "Debe tener 16 dígitos",
            keyboardType = KeyboardType.Number,
            maxLength = 16
        )

        // Nombre del titular
        CheckoutInputField(
            value = cardHolder,
            onValueChange = onCardHolderChange,
            label = "Nombre del Titular",
            placeholder = "John Doe",
            isValid = isValidCardHolder(cardHolder),
            errorMessage = "Mínimo 2 caracteres"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Vencimiento
            CheckoutInputField(
                value = expiryDate,
                onValueChange = onExpiryDateChange,
                label = "Vencimiento",
                placeholder = "MM/YY",
                isValid = isValidExpiryDate(expiryDate),
                errorMessage = "Formato MM/YY",
                keyboardType = KeyboardType.Number,
                maxLength = 5,
                modifier = Modifier.weight(1f)
            )

            // CVC
            CheckoutInputField(
                value = cvc,
                onValueChange = onCvcChange,
                label = "CVC",
                placeholder = "123",
                isValid = isValidCVC(cvc),
                errorMessage = "3 dígitos",
                keyboardType = KeyboardType.Number,
                maxLength = 3,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Información para pago con puntos
 */
@Composable
private fun PointsPaymentInfo(
    pointsNeeded: Int,
    pointsAvailable: Int
) {
    val canPay = pointsAvailable >= pointsNeeded
    val remaining = pointsAvailable - pointsNeeded

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (canPay) Color.White else Color(0xFFFFEBEE),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Necesitas:",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$pointsNeeded pts",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tienes:",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$pointsAvailable pts",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.orange)
            )
        }

        if (canPay) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Te sobrarán:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$remaining pts",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Te faltan:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${pointsNeeded - pointsAvailable} pts",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }
    }
}

// Funciones de Validación

private fun calculatePointsNeeded(amount: Double): Int {
    return (amount * 100).toInt()
}

private fun isValidCardNumber(cardNumber: String): Boolean {
    val cleaned = cardNumber.replace(" ", "")
    return cleaned.length == 16 && cleaned.all { it.isDigit() }
}

private fun isValidCardHolder(cardHolder: String): Boolean {
    return cardHolder.trim().length >= 2
}

private fun formatExpiryDate(input: String): String {
    val cleaned = input.filter { it.isDigit() }
    return when {
        cleaned.length <= 2 -> cleaned
        cleaned.length <= 4 -> "${cleaned.substring(0, 2)}/${cleaned.substring(2)}"
        else -> "${cleaned.substring(0, 2)}/${cleaned.substring(2, 4)}"
    }
}

private fun isValidExpiryDate(expiryDate: String): Boolean {
    if (expiryDate.length != 5) return false
    val parts = expiryDate.split("/")
    if (parts.size != 2) return false
    
    val month = parts[0].toIntOrNull() ?: return false
    val year = parts[1].toIntOrNull() ?: return false
    
    return month in 1..12 && year >= 24 // Asumiendo año 2024+
}

private fun isValidCVC(cvc: String): Boolean {
    return cvc.length == 3 && cvc.all { it.isDigit() }
}

private fun validateCardData(
    cardNumber: String,
    cardHolder: String,
    expiryDate: String,
    cvc: String
): Boolean {
    return isValidCardNumber(cardNumber) &&
            isValidCardHolder(cardHolder) &&
            isValidExpiryDate(expiryDate) &&
            isValidCVC(cvc)
}
