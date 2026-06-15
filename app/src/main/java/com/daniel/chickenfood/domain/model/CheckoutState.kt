package com.daniel.chickenfood.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutState(
    val cartItems: List<OrderItemModel> = emptyList(),
    val cartTotal: Double = 0.0,
    val userPoints: Int = 0,
    val selectedPaymentMethod: String = "card", // "card" or "points"
    val isLoading: Boolean = false,
    val error: String? = null,
    val paymentStatus: String = "IDLE", // IDLE, PROCESSING, SUCCESS, ERROR
    val orderData: OrderModel? = null
)
