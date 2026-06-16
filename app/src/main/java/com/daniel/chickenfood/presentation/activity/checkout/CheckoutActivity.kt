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
import com.daniel.chickenfood.domain.model.OrderModel
import com.daniel.chickenfood.domain.model.PointsTransactionModel
import com.daniel.chickenfood.helper.ManagmentCart
import com.daniel.chickenfood.helper.AuthHelper
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.dashboard.MainActivity
import com.daniel.chickenfood.presentation.viewModel.OrderViewModel
import com.daniel.chickenfood.presentation.viewModel.RewardsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

private const val TAG = "CheckoutActivity"

class CheckoutActivity : BaseActivity() {

    private lateinit var managmentCart: ManagmentCart
    private val orderViewModel: OrderViewModel by viewModel()
    private val rewardsViewModel: RewardsViewModel by viewModel()

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
                            orderId = UUID.randomUUID().toString()
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
                            
                            // ✅ NUEVO: Guardar orden en Firebase
                            val currentUserId = AuthHelper.getUserId()
                            if (!currentUserId.isNullOrEmpty()) {
                                Log.d(TAG, "Saving order to Firebase for user: $currentUserId")
                                val order = OrderModel(
                                    orderId = orderId,
                                    userId = currentUserId,
                                    items = cartItems.toList(),
                                    totalPrice = cartTotal,
                                    pointsEarned = pointsChange,
                                    orderDate = System.currentTimeMillis(),
                                    status = "completed"
                                )
                                orderViewModel.createOrder(order)
                                
                                // ✅ NUEVO: Registrar puntos en Firebase si el pago fue por tarjeta
                                if (method == "card" && pointsChange > 0) {
                                    Log.d(TAG, "Recording points transaction: +$pointsChange")
                                    val pointsTransaction = PointsTransactionModel(
                                        userId = currentUserId,
                                        orderId = orderId,
                                        points = pointsChange,
                                        type = "purchase",
                                        description = "Compra de $$cartTotal - ${cartItems.size} items",
                                        timestamp = System.currentTimeMillis()
                                    )
                                    rewardsViewModel.recordPointsTransaction(pointsTransaction)
                                }
                            } else {
                                Log.w(TAG, "No user logged in, order will be saved but without user association")
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

