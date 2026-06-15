package com.daniel.chickenfood.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.chickenfood.data.service.MockPaymentService
import com.daniel.chickenfood.domain.model.CheckoutState
import com.daniel.chickenfood.domain.model.OrderItemModel
import com.daniel.chickenfood.domain.model.PaymentModel
import com.daniel.chickenfood.domain.reposity.OrderRepository
import com.daniel.chickenfood.domain.reposity.RewardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "CheckoutViewModel"

class CheckoutViewModel(
    private val orderRepository: OrderRepository,
    private val rewardsRepository: RewardsRepository,
    private val mockPaymentService: MockPaymentService
) : ViewModel() {

    private val _checkoutState = MutableStateFlow(CheckoutState())
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Inicializa el estado del checkout con los datos del carrito
     */
    fun initializeCheckout(
        cartItems: List<OrderItemModel>,
        cartTotal: Double,
        userPoints: Int
    ) {
        Log.d(TAG, "Initializing checkout with ${cartItems.size} items, total=$cartTotal, points=$userPoints")
        
        _checkoutState.value = CheckoutState(
            cartItems = cartItems,
            cartTotal = cartTotal,
            userPoints = userPoints,
            selectedPaymentMethod = "card",
            paymentStatus = "IDLE"
        )
    }

    /**
     * Selecciona el método de pago
     */
    fun selectPaymentMethod(method: String) {
        Log.d(TAG, "Payment method selected: $method")
        _checkoutState.value = _checkoutState.value.copy(
            selectedPaymentMethod = method
        )
        _error.value = null
    }

    /**
     * Valida los datos de la tarjeta
     */
    fun validateCardData(
        cardNumber: String,
        cardHolder: String,
        expiryDate: String,
        cvc: String
    ): Boolean {
        val isValid = isValidCardNumber(cardNumber) &&
                isValidCardHolder(cardHolder) &&
                isValidExpiryDate(expiryDate) &&
                isValidCVC(cvc)
        
        Log.d(TAG, "Card validation: $isValid")
        return isValid
    }

    /**
     * Valida si hay suficientes puntos
     */
    fun validatePointsAvailability(pointsNeeded: Int): Boolean {
        val canPay = _checkoutState.value.userPoints >= pointsNeeded
        Log.d(TAG, "Points validation: available=${_checkoutState.value.userPoints}, needed=$pointsNeeded, result=$canPay")
        return canPay
    }

    /**
     * Procesa el pago - método principal
     */
    fun processPayment(
        paymentMethod: String,
        cardData: Map<String, String>? = null
    ) {
        Log.d(TAG, "Processing payment with method: $paymentMethod")
        
        _isLoading.value = true
        _error.value = null
        _checkoutState.value = _checkoutState.value.copy(paymentStatus = "PROCESSING")

        viewModelScope.launch {
            try {
                when (paymentMethod) {
                    "card" -> processCardPayment(cardData)
                    "points" -> processPointsPayment()
                    else -> {
                        Log.e(TAG, "Unknown payment method: $paymentMethod")
                        _error.value = "Método de pago desconocido"
                        _checkoutState.value = _checkoutState.value.copy(paymentStatus = "ERROR")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing payment: ${e.message}", e)
                _error.value = "Error al procesar el pago: ${e.message}"
                _checkoutState.value = _checkoutState.value.copy(paymentStatus = "ERROR")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Procesa pago con tarjeta
     */
    private suspend fun processCardPayment(cardData: Map<String, String>?) {
        Log.d(TAG, "Processing card payment")

        if (cardData == null) {
            _error.value = "Datos de tarjeta no proporcionados"
            _checkoutState.value = _checkoutState.value.copy(paymentStatus = "ERROR")
            return
        }

        val paymentModel = PaymentModel(
            method = "card",
            cardNumber = cardData["cardNumber"],
            cardHolder = cardData["cardHolder"],
            expiryDate = cardData["expiryDate"],
            cvc = cardData["cvc"],
            amount = _checkoutState.value.cartTotal
        )

        val orderId = generateOrderId()

        try {
            mockPaymentService.processCardPayment(paymentModel, _checkoutState.value.cartTotal, orderId).collect { result ->
                if (result.success) {
                    Log.d(TAG, "Card payment successful: ${result.chargeId}")
                    
                    val pointsEarned = calculatePointsEarned(_checkoutState.value.cartTotal)
                    _checkoutState.value = _checkoutState.value.copy(
                        paymentStatus = "SUCCESS",
                        orderData = null, // TODO: Crear orden en Firebase
                        cartItems = emptyList()
                    )
                } else {
                    Log.w(TAG, "Card payment failed: ${result.error}")
                    _error.value = result.error ?: "El pago fue rechazado"
                    _checkoutState.value = _checkoutState.value.copy(paymentStatus = "ERROR")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in card payment: ${e.message}", e)
            _error.value = "Error al procesar tarjeta: ${e.message}"
            _checkoutState.value = _checkoutState.value.copy(paymentStatus = "ERROR")
        }
    }

    /**
     * Procesa pago con puntos
     */
    private suspend fun processPointsPayment() {
        Log.d(TAG, "Processing points payment")

        val pointsNeeded = calculatePointsNeeded(_checkoutState.value.cartTotal)
        val userPoints = _checkoutState.value.userPoints

        if (userPoints < pointsNeeded) {
            Log.w(TAG, "Insufficient points: have=$userPoints, need=$pointsNeeded")
            _error.value = "No tienes suficientes puntos para esta compra"
            _checkoutState.value = _checkoutState.value.copy(paymentStatus = "ERROR")
            return
        }

        val orderId = generateOrderId()

        try {
            mockPaymentService.processPointsPayment(pointsNeeded, orderId).collect { result ->
                if (result.success) {
                    Log.d(TAG, "Points payment successful: ${result.paymentId}")
                    _checkoutState.value = _checkoutState.value.copy(
                        paymentStatus = "SUCCESS",
                        orderData = null, // TODO: Crear orden en Firebase
                        cartItems = emptyList()
                    )
                } else {
                    Log.w(TAG, "Points payment failed: ${result.error}")
                    _error.value = result.error ?: "El pago con puntos falló"
                    _checkoutState.value = _checkoutState.value.copy(paymentStatus = "ERROR")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in points payment: ${e.message}", e)
            _error.value = "Error al procesar puntos: ${e.message}"
            _checkoutState.value = _checkoutState.value.copy(paymentStatus = "ERROR")
        }
    }

    /**
     * Limpia el estado de error
     */
    fun clearError() {
        _error.value = null
    }

    // Helper functions for validation

    private fun isValidCardNumber(cardNumber: String): Boolean {
        val cleaned = cardNumber.replace(" ", "")
        return cleaned.length == 16 && cleaned.all { it.isDigit() }
    }

    private fun isValidCardHolder(cardHolder: String): Boolean {
        return cardHolder.trim().length >= 2
    }

    private fun isValidExpiryDate(expiryDate: String): Boolean {
        if (expiryDate.length != 5) return false
        val parts = expiryDate.split("/")
        if (parts.size != 2) return false

        val month = parts[0].toIntOrNull() ?: return false
        val year = parts[1].toIntOrNull() ?: return false

        return month in 1..12 && year >= 24
    }

    private fun isValidCVC(cvc: String): Boolean {
        return cvc.length == 3 && cvc.all { it.isDigit() }
    }

    // Helper functions for calculations

    private fun calculatePointsNeeded(amount: Double): Int {
        return (amount * 100).toInt()
    }

    private fun calculatePointsEarned(amount: Double): Int {
        return (amount * 100 * 0.10).toInt() // 10% cashback
    }

    private fun generateOrderId(): String {
        return "ORD_${UUID.randomUUID().toString().take(8).uppercase()}_${System.currentTimeMillis()}"
    }
}
