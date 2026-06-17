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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.daniel.chickenfood.presentation.activity.dashboard.scrollIndicatorModifier

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
    // Constante de conversión: 1 punto = 1 peso chileno
    val POINTS_CONVERSION_RATE = 1.0
    
    Log.d(TAG, "CheckoutScreen rendering - items=${cartItems.size}, total=$cartTotal, points=$userPoints")

    var selectedPaymentMethod by remember { mutableStateOf("card") }
    var cardNumber by remember { mutableStateOf("4532123456789010") }  // ← Precargar
    var cardHolder by remember { mutableStateOf("JOHN DOE") }  // ← Precargar
    var expiryDate by remember { mutableStateOf("12/25") }  // ← Precargar
    var cvc by remember { mutableStateOf("123") }  // ← Precargar
    var pointsDiscount by remember { mutableStateOf(calculatePointsDiscount(userPoints)) }
    var errorMessage by remember { mutableStateOf(error) }
    var showPointsDialog by remember { mutableStateOf(false) }  // Comienza cerrado
    var userHasSeenPointsDialog by remember { mutableStateOf(false) }  // Para no mostrar múltiples veces
    var showCardPaymentDialog by remember { mutableStateOf(false) }  // Para confirmar pago con tarjeta de la diferencia
    var remainingAmount by remember { mutableStateOf(0.0) }  // Monto restante a pagar con tarjeta
    var isMixedPayment by remember { mutableStateOf(false) }  // Indica si es pago mixto (puntos + tarjeta)

    // ✅ REACTIVO: Mostrar dialog cuando userPoints cambia de 0 a >0
    LaunchedEffect(userPoints) {
        Log.d(TAG, "LaunchedEffect userPoints changed: $userPoints, seen=$userHasSeenPointsDialog")
        if (userPoints > 0 && !userHasSeenPointsDialog) {
            Log.d(TAG, "Showing points dialog - user has $userPoints points")
            showPointsDialog = true
            userHasSeenPointsDialog = true
        }
    }

    // ✅ Diálogo para confirmar si pagar con tarjeta la diferencia después de usar puntos
    if (showCardPaymentDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showCardPaymentDialog = false },
            title = {
                Text(
                    text = "💳 Pagar Diferencia con Tarjeta",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Tus puntos cubren parte de la compra.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Monto restante a pagar: $${"%.2f".format(remainingAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.orange)
                    )
                    
                    Text(
                        text = "¿Deseas pagar esta diferencia con tarjeta?",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Log.d(TAG, "User chose to pay difference with card: $remainingAmount")
                        isMixedPayment = true  // Marcar como pago mixto
                        showCardPaymentDialog = false
                        selectedPaymentMethod = "card"  // Cambiar a pago con tarjeta
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.orange)
                    )
                ) {
                    Text("Sí, Pagar con Tarjeta", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        Log.d(TAG, "User chose NOT to pay difference")
                        showCardPaymentDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("Cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 🆕 Dialog para preguntar si desea usar puntos
    if (showPointsDialog && userPoints > 0) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPointsDialog = false },
            title = {
                Text(
                    text = "💎 Usar Puntos Acumulados",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Tienes $userPoints puntos acumulados",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val discount = calculatePointsDiscount(userPoints)
                    Text(
                        text = "Valor: $${"%.2f".format(discount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(R.color.orange)
                    )
                    
                    val finalTotal = (cartTotal - discount).coerceAtLeast(0.0)
                    if (discount >= cartTotal) {
                        Text(
                            text = "✅ ¡Puedes pagar la compra COMPLETA con puntos!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Green,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Pagarías $${"%.2f".format(finalTotal)} en tarjeta + $${" %.2f".format(discount)} de descuento",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    Text(
                        text = "¿Deseas usar tus puntos en esta compra?",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Log.d(TAG, "User chose to use points")
                        selectedPaymentMethod = "points"  // Seleccionar puntos automáticamente
                        showPointsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.orange)
                    )
                ) {
                    Text("Sí, Usar Puntos", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        Log.d(TAG, "User chose NOT to use points")
                        selectedPaymentMethod = "card"  // Mantener tarjeta
                        showPointsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("No, Usar Tarjeta", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

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
        val checkoutLazyListState = rememberLazyListState()
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .scrollIndicatorModifier(checkoutLazyListState)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = checkoutLazyListState,
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
                        userPoints = userPoints,
                        cartTotal = cartTotal
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
                                val cardData = mutableMapOf(
                                    "cardNumber" to cardNumber,
                                    "cardHolder" to cardHolder,
                                    "expiryDate" to expiryDate,
                                    "cvc" to cvc
                                )
                                // Si es pago mixto, pasar bandera indicando que también se usaron puntos
                                if (isMixedPayment) {
                                    Log.d(TAG, "Confirming mixed payment (points + card) - card amount: $remainingAmount")
                                    cardData["paymentType"] = "mixed"  // Indicar que es pago mixto
                                    cardData["pointsUsed"] = "true"
                                }
                                onConfirmPayment("card", cardData as Map<String, String>)
                            } else {
                                errorMessage = "Por favor verifica los datos de la tarjeta"
                            }
                        }
                        selectedPaymentMethod == "points" -> {
                            if (userPoints > 0) {
                                // Calcular si hay diferencia a pagar
                                val pointsNeeded = (cartTotal * POINTS_CONVERSION_RATE).toInt()
                                val pointsToSpend = minOf(pointsNeeded, userPoints)
                                val discount = pointsToSpend / POINTS_CONVERSION_RATE
                                val finalTotal = (cartTotal - discount).coerceAtLeast(0.0)
                                
                                if (finalTotal > 0) {
                                    // Hay diferencia a pagar → mostrar diálogo
                                    remainingAmount = finalTotal
                                    showCardPaymentDialog = true
                                    Log.d(TAG, "Points partial payment: difference=$finalTotal to pay with card")
                                } else {
                                    // Puntos cubren todo → pagar solo con puntos
                                    Log.d(TAG, "Points full payment: all covered")
                                    onConfirmPayment("points", null)
                                }
                            } else {
                                errorMessage = "No tienes puntos disponibles"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading && (
                    (selectedPaymentMethod == "card" && cardNumber.isNotEmpty()) ||
                    (selectedPaymentMethod == "points" && userPoints > 0)
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
            placeholder = "4532 1234 5678 9010",
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
            placeholder = "JOHN DOE",
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
                placeholder = "12/25",
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
 * Información para pago con puntos (descuento mixto)
 */
@Composable
private fun PointsPaymentInfo(
    userPoints: Int,
    cartTotal: Double
) {
    // Constante de conversión: 1 punto = 1 peso chileno
    val POINTS_CONVERSION_RATE = 1.0
    
    // Calcular puntos necesarios para pagar el monto total
    val pointsNeeded = (cartTotal * POINTS_CONVERSION_RATE).toInt()
    val pointsToSpend = minOf(pointsNeeded, userPoints)  // No gastar más de lo que se tiene
    val discount = pointsToSpend / POINTS_CONVERSION_RATE  // Convertir puntos a dinero
    val finalTotal = (cartTotal - discount).coerceAtLeast(0.0)
    val pointsRemaining = userPoints - pointsToSpend  // Puntos que quedarán después
    val isFullyCovered = discount >= cartTotal

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isFullyCovered) Color(0xFFE8F5E9) else Color.White,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Puntos disponibles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Puntos disponibles:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$userPoints pts",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.orange)
            )
        }

        // Puntos a usar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Puntos a usar:",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "-$pointsToSpend pts = -$${"%.2f".format(discount)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
        }

        if (isFullyCovered) {
            // Compra cubierta completamente por puntos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✅ ¡Compra completamente cubierta!",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = "Total: $0.00",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        } else {
            // Pago mixto: puntos + tarjeta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total original:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$${"%.2f".format(cartTotal)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Aún debes pagar:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${"%.2f".format(finalTotal)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.orange)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💳",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Pagarás $${"%.2f".format(finalTotal)} con tarjeta",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }

        // Puntos que quedarán después del pago
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Te quedarán:",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = "$pointsRemaining pts",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Funciones de Validación

/**
 * Calcula el descuento en dinero que se obtiene con los puntos
 * 1 punto = 1 peso chileno
 */
private fun calculatePointsDiscount(userPoints: Int): Double {
    return userPoints * 1.0  // userPoints / 1
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
