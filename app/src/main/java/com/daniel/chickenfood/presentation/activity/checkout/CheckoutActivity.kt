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
import com.daniel.chickenfood.helper.ManagmentCart
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.dashboard.MainActivity

private const val TAG = "CheckoutActivity"

class CheckoutActivity : BaseActivity() {

    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        managmentCart = ManagmentCart(applicationContext)

        // Obtener datos del carrito desde el Intent usando Parcelable (seguro)
        @Suppress("DEPRECATION")
        val cartItems = intent.getParcelableArrayListExtra<OrderItemModel>("cartItems") ?: mutableListOf()
        val cartTotal = intent.getDoubleExtra("cartTotal", 0.0)
        val userPoints = intent.getIntExtra("userPoints", 0)

        Log.d(TAG, "CheckoutActivity opened with ${cartItems.size} items, total=$cartTotal, points=$userPoints")

        if (cartItems.isEmpty()) {
            Log.w(TAG, "No items in cart, finishing activity")
            android.widget.Toast.makeText(
                this,
                "El carrito está vacío",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

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
                            Log.d(TAG, "Navigating back to MainActivity and clearing cart")
                            managmentCart.clearCart()
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

