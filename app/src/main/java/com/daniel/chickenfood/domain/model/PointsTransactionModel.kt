package com.daniel.chickenfood.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PointsTransactionModel(
    @SerializedName("transactionId")
    val transactionId: String = "",
    
    @SerializedName("userId")
    val userId: String = "",
    
    @SerializedName("points")
    val points: Int = 0,  // Positivo = ganados, Negativo = gastados
    
    @SerializedName("type")
    val type: String = "",  // "purchase", "redemption", "bonus", "adjustment"
    
    @SerializedName("description")
    val description: String = "",
    
    @SerializedName("orderId")
    val orderId: String = "",  // Referencia a la orden (si aplica)
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @SerializedName("balanceBefore")
    val balanceBefore: Int = 0,  // Saldo antes de la transacción
    
    @SerializedName("balanceAfter")
    val balanceAfter: Int = 0  // Saldo después de la transacción
)
