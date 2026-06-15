package com.daniel.chickenfood.data.service

import com.daniel.chickenfood.domain.model.PaymentModel
import kotlinx.coroutines.flow.Flow

interface MockPaymentService {
    
    /**
     * Procesa un pago con tarjeta de crédito (simulado)
     */
    fun processCardPayment(cardData: PaymentModel, amount: Double, orderId: String): Flow<MockPaymentResult>
    
    /**
     * Procesa un pago con puntos
     */
    fun processPointsPayment(pointsAmount: Int, orderId: String): Flow<MockPaymentResult>
}

data class MockPaymentResult(
    val success: Boolean,
    val chargeId: String? = null,
    val paymentId: String? = null,
    val message: String = "",
    val error: String? = null
)
