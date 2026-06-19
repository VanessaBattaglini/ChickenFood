package com.daniel.chickenfood.presentation.activity.checkout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
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
private const val POINTS_CONVERSION_RATE = 1.0  // 1 punto = 1 peso chileno

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

        Log.d(TAG, "CheckoutActivity opened with ${cartItems.size} items, total=$cartTotal")

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

        // ✅ Cargar puntos del usuario actual directamente desde RewardsViewModel
        val currentUserId = AuthHelper.getUserId()
        if (!currentUserId.isNullOrEmpty()) {
            Log.d(TAG, "Loading user rewards for: $currentUserId")
            rewardsViewModel.loadUserRewards(currentUserId)
            // ✅ TEMPORAL: Dar 500 puntos de bienvenida al usuario si es su primer checkout
            // (Para testing, ya que Firebase puede estar vacío)
        } else {
            Log.w(TAG, "No user ID found")
        }

        setContent {
            // ✅ Obtener puntos en tiempo real
            val userPoints by rewardsViewModel.pointsBalance.collectAsState()
            
            Log.d(TAG, "🎯 CheckoutScreen rendering - userPoints=$userPoints (type: ${userPoints.javaClass.simpleName})")
            Log.d(TAG, "🎯 User ID: $currentUserId")
            
            var currentScreen by remember { mutableStateOf("checkout") }
            var orderId by remember { mutableStateOf("") }
            var paymentMethod by remember { mutableStateOf("") }
            var pointsChange by remember { mutableStateOf(0) }
            var pointsAfter by remember { mutableStateOf(userPoints) }

            // ✅ LaunchedEffect para forzar recarga cuando userPoints = 0 después de 2 segundos
            LaunchedEffect(Unit) {
                // Cargar puntos inicial
                if (!currentUserId.isNullOrEmpty()) {
                    rewardsViewModel.loadUserRewards(currentUserId)
                    Log.d(TAG, "LaunchedEffect: loadUserRewards called")
                }
            }

            when (currentScreen) {
                "checkout" -> {
                    CheckoutScreen(
                        cartItems = cartItems,
                        cartTotal = cartTotal,
                        userPoints = userPoints,  // ✅ Puntos en tiempo real del ViewModel
                        isUserRegistered = !currentUserId.isNullOrEmpty(),  // ✨ NUEVO: Pasar si el usuario está registrado
                        onBackClick = { finish() },
                        onConfirmPayment = { method, cardData ->
                            Log.d(TAG, "Payment confirmed with method=$method")
                            // Simular pago exitoso
                            paymentMethod = method
                            orderId = UUID.randomUUID().toString()
                            
                            // Verificar si es pago mixto (puntos + tarjeta)
                            val isMixedPayment = cardData?.get("paymentType") == "mixed"
                            
                            if (method == "card") {
                                if (isMixedPayment) {
                                    // Pago MIXTO: Puntos + Tarjeta
                                    Log.d(TAG, "Mixed payment detected: points + card")
                                    paymentMethod = "mixed"  // ✅ Cambiar a "mixed" para identificar en ConfirmationScreen
                                    val pointsNeeded = (cartTotal * POINTS_CONVERSION_RATE).toInt()
                                    val pointsToSpend = minOf(pointsNeeded, userPoints)
                                    val discount = pointsToSpend / POINTS_CONVERSION_RATE
                                    val cardAmount = cartTotal - discount
                                    
                                    pointsChange = -pointsToSpend  // Negativos porque se gastan
                                    pointsAfter = userPoints - pointsToSpend
                                    Log.d(TAG, "Mixed payment: spend $pointsToSpend points, card charge: $cardAmount, remaining: $pointsAfter")
                                } else {
                                    // Pago SOLO con tarjeta: GANAR puntos (10% cashback)
                                    pointsChange = (cartTotal * 0.10).toInt()
                                    pointsAfter = userPoints + pointsChange
                                    Log.d(TAG, "Card payment: earn $pointsChange points, total: $pointsAfter")
                                }
                            } else {
                                // Pago con puntos: GASTAR SOLO los puntos necesarios para el descuento
                                // 1 punto = 1 peso chileno
                                val pointsNeeded = (cartTotal * POINTS_CONVERSION_RATE).toInt()
                                // Usar min() para no gastar más puntos de los que se tienen
                                val pointsToSpend = minOf(pointsNeeded, userPoints)
                                pointsChange = -pointsToSpend  // ✅ NEGATIVO porque se gastan
                                pointsAfter = userPoints - pointsToSpend
                                Log.d(TAG, "Payment with points: spend $pointsToSpend points, remaining: $pointsAfter")
                            }
                            
                            // ✅ NUEVO: Guardar orden en Firebase
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
                                
                                // ✅ REGISTRAR TRANSACCIÓN DE PUNTOS para AMBOS métodos de pago
                                if (pointsChange != 0) {
                                    Log.d(TAG, "Recording points transaction: $pointsChange points (method=$method)")
                                    val pointsTransaction = if (method == "card" && isMixedPayment) {
                                        // Pago MIXTO: Tarjeta + Puntos
                                        PointsTransactionModel(
                                            userId = currentUserId,
                                            orderId = orderId,
                                            points = pointsChange,  // Negativos
                                            type = "mixed_payment",
                                            description = "Compra de $$cartTotal - ${cartItems.size} items (Puntos + Tarjeta)",
                                            timestamp = System.currentTimeMillis()
                                        )
                                    } else if (method == "card") {
                                        // Pago SOLO con tarjeta: ganar puntos (+10% cashback)
                                        PointsTransactionModel(
                                            userId = currentUserId,
                                            orderId = orderId,
                                            points = pointsChange,
                                            type = "purchase",
                                            description = "Compra de $$cartTotal - ${cartItems.size} items (Tarjeta)",
                                            timestamp = System.currentTimeMillis()
                                        )
                                    } else {
                                        // Pago SOLO con puntos: gastar puntos (puntos serán negativos)
                                        PointsTransactionModel(
                                            userId = currentUserId,
                                            orderId = orderId,
                                            points = pointsChange,  // Negativo porque se gastan
                                            type = "purchase",
                                            description = "Compra de $$cartTotal - ${cartItems.size} items (Puntos)",
                                            timestamp = System.currentTimeMillis()
                                        )
                                    }
                                    rewardsViewModel.recordPointsTransaction(pointsTransaction)
                                }
                            } else {
                                Log.w(TAG, "No user logged in, order will be saved but without user association")
                            }
                            
                            // ✅ IMPORTANTE: Limpiar carrito DESPUÉS de guardar orden
                            Log.d(TAG, "Clearing cart after payment confirmed")
                            managmentCart.clearCart()
                            
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
                        isUserRegistered = !currentUserId.isNullOrEmpty(),  // ✨ NUEVO: Pasar si el usuario está registrado
                        onBackClick = {
                            Log.d(TAG, "Back to Dashboard clicked - clearing cart")
                            managmentCart.clearCart()
                            val intent = Intent(this@CheckoutActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                            finish()
                        },
                        onViewOrderClick = {
                            Log.d(TAG, "View order clicked")
                            // ✅ IMPORTANTE: Limpiar carrito antes de navegar
                            managmentCart.clearCart()
                            // ✨ NUEVO: Navegar a OrderDetailActivity
                            val intent = Intent(this@CheckoutActivity, OrderDetailActivity::class.java).apply {
                                putParcelableArrayListExtra("cartItems", ArrayList(cartItems))
                                putExtra("cartTotal", cartTotal)
                                putExtra("paymentMethod", paymentMethod)
                                putExtra("pointsBefore", userPoints)
                                putExtra("pointsChange", pointsChange)
                                putExtra("orderId", orderId)
                                putExtra("orderTimestamp", System.currentTimeMillis())
                                putExtra("discountUsed", 0.0)  // Para cuando implementemos descuento
                                putExtra("pointsUsedForDiscount", 0)  // Para cuando implementemos descuento
                            }
                            startActivity(intent)
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

