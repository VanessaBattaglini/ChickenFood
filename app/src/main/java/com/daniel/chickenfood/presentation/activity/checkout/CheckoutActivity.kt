package com.daniel.chickenfood.presentation.activity.checkout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.daniel.chickenfood.domain.model.OrderItemModel
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.dashboard.MainActivity

private const val TAG = "CheckoutActivity"

class CheckoutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Obtener datos del carrito desde el Intent
        val itemCount = intent.getIntExtra("itemCount", 0)
        val cartTotal = intent.getDoubleExtra("cartTotal", 0.0)
        val userPoints = intent.getIntExtra("userPoints", 0)

        Log.d(TAG, "CheckoutActivity opened with $itemCount items, total=$cartTotal, points=$userPoints")

        // Reconstruir cartItems desde los strings
        val cartItems = mutableListOf<OrderItemModel>()
        for (i in 0 until itemCount) {
            val itemString = intent.getStringExtra("item_$i")
            if (itemString != null) {
                val parts = itemString.split("|")
                if (parts.size == 5) {
                    try {
                        val item = OrderItemModel(
                            title = parts[0],
                            price = parts[1].toDouble(),
                            quantity = parts[2].toInt(),
                            subtotal = parts[3].toDouble(),
                            foodId = parts[4].toInt()
                        )
                        cartItems.add(item)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing item $i: ${e.message}")
                    }
                }
            }
        }

        Log.d(TAG, "Reconstructed ${cartItems.size} items from Intent")

        setContent {
            var currentScreen by remember { mutableStateOf("checkout") }
            var orderId by remember { mutableStateOf("") }
            var paymentMethod by remember { mutableStateOf("") }
            var pointsChange by remember { mutableStateOf(0) }
            var pointsAfter by remember { mutableStateOf(userPoints) }

            when (currentScreen) {
                "checkout" -> {
                    CheckoutScreen(
                        cartItems = cartItems,
                        cartTotal = cartTotal,
                        userPoints = userPoints,
                        onBackClick = { finish() },
                        onConfirmPayment = { method, cardData ->
                            Log.d(TAG, "Payment confirmed with method=$method")
                            // Simular pago exitoso
                            paymentMethod = method
                            orderId = generateOrderId()
                            pointsChange = if (method == "card") {
                                (cartTotal * 0.10).toInt() // 10% cashback
                            } else {
                                (cartTotal * 100).toInt() // Puntos gastados
                            }
                            pointsAfter = if (method == "card") {
                                userPoints + pointsChange
                            } else {
                                userPoints - pointsChange
                            }
                            currentScreen = "confirmation"
                        }
                    )
                }
                "confirmation" -> {
                    ConfirmationScreen(
                        orderId = orderId,
                        cartItems = cartItems,
                        cartTotal = cartTotal,
                        paymentMethod = paymentMethod,
                        pointsBefore = userPoints,
                        pointsChange = pointsChange,
                        pointsAfter = pointsAfter,
                        onBackClick = {
                            Log.d(TAG, "Navigating back to MainActivity")
                            val intent = Intent(this@CheckoutActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                            finish()
                        },
                        onViewOrderClick = {
                            Log.d(TAG, "View order clicked")
                            // TODO: Implementar navegación a detalle de orden
                        }
                    )
                }
            }
        }
    }

    private fun generateOrderId(): String {
        return "ORD_${System.currentTimeMillis()}"
    }
}

