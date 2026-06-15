package com.daniel.chickenfood.data.service

import android.util.Log
import com.daniel.chickenfood.domain.model.PaymentModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

private const val TAG = "MockPaymentServiceImpl"

class MockPaymentServiceImpl : MockPaymentService {

    override fun processCardPayment(
        cardData: PaymentModel,
        amount: Double,
        orderId: String
    ): Flow<MockPaymentResult> = flow {
        try {
            Log.d(TAG, "Processing card payment for order: $orderId, amount: $amount")
            
            // Validar datos de tarjeta
            if (!validateCardData(cardData)) {
                Log.e(TAG, "Invalid card data")
                emit(
                    MockPaymentResult(
                        success = false,
                        error = "Datos de tarjeta inválidos"
                    )
                )
                return@flow
            }
            
            // Simular latencia de red (1-2 segundos)
            delay(1000)
            
            // Validar número de tarjeta (simulación)
            val cardNumber = cardData.cardNumber ?: ""
            val isRejected = cardNumber.startsWith("4000")
            
            if (isRejected) {
                Log.d(TAG, "Payment rejected (simulated)")
                emit(
                    MockPaymentResult(
                        success = false,
                        error = "Su tarjeta fue rechazada. Por favor, intente otra tarjeta."
                    )
                )
            } else {
                // Generar ID de cargo único
                val chargeId = "ch_${UUID.randomUUID().toString().take(16).uppercase()}"
                Log.d(TAG, "Payment processed successfully. Charge ID: $chargeId")
                
                emit(
                    MockPaymentResult(
                        success = true,
                        chargeId = chargeId,
                        message = "Pago procesado exitosamente"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in processCardPayment: ${e.message}", e)
            emit(
                MockPaymentResult(
                    success = false,
                    error = "Error al procesar el pago: ${e.message}"
                )
            )
        }
    }

    override fun processPointsPayment(
        pointsAmount: Int,
        orderId: String
    ): Flow<MockPaymentResult> = flow {
        try {
            Log.d(TAG, "Processing points payment for order: $orderId, points: $pointsAmount")
            
            // Validar cantidad de puntos
            if (pointsAmount <= 0) {
                Log.e(TAG, "Invalid points amount: $pointsAmount")
                emit(
                    MockPaymentResult(
                        success = false,
                        error = "Cantidad de puntos inválida"
                    )
                )
                return@flow
            }
            
            // Simular latencia de red (más rápido que tarjeta, 800ms)
            delay(800)
            
            // Generar ID de pago único
            val paymentId = "pmt_${UUID.randomUUID().toString().take(16).uppercase()}"
            Log.d(TAG, "Points payment processed successfully. Payment ID: $paymentId")
            
            emit(
                MockPaymentResult(
                    success = true,
                    paymentId = paymentId,
                    message = "Pago con puntos procesado exitosamente"
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in processPointsPayment: ${e.message}", e)
            emit(
                MockPaymentResult(
                    success = false,
                    error = "Error al procesar el pago: ${e.message}"
                )
            )
        }
    }

    /**
     * Valida los datos de la tarjeta
     */
    private fun validateCardData(cardData: PaymentModel): Boolean {
        return try {
            // Validar número de tarjeta (16 dígitos)
            val cardNumber = cardData.cardNumber ?: return false
            if (cardNumber.replace(" ", "").length != 16) {
                Log.w(TAG, "Invalid card number length: ${cardNumber.length}")
                return false
            }
            
            // Validar nombre del titular
            if (cardData.cardHolder.isNullOrBlank()) {
                Log.w(TAG, "Card holder is empty")
                return false
            }
            
            // Validar fecha de vencimiento (MM/YY)
            val expiryDate = cardData.expiryDate ?: return false
            val parts = expiryDate.split("/")
            if (parts.size != 2) {
                Log.w(TAG, "Invalid expiry date format: $expiryDate")
                return false
            }
            
            val month = parts[0].toIntOrNull() ?: return false
            if (month < 1 || month > 12) {
                Log.w(TAG, "Invalid month: $month")
                return false
            }
            
            // Validar CVC (3 dígitos)
            val cvc = cardData.cvc ?: return false
            if (cvc.length != 3 || !cvc.all { it.isDigit() }) {
                Log.w(TAG, "Invalid CVC format")
                return false
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error validating card data: ${e.message}", e)
            false
        }
    }
}
