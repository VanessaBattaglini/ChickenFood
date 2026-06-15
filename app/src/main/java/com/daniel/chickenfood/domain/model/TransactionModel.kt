package com.daniel.chickenfood.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionModel(
    @SerializedName("transactionId")
    val transactionId: String = "",
    
    @SerializedName("userId")
    val userId: String = "",
    
    @SerializedName("type")
    val type: String = "", // "earned" or "spent"
    
    @SerializedName("points")
    val points: Int = 0,
    
    @SerializedName("reason")
    val reason: String = "", // "Purchase", "Reward", etc.
    
    @SerializedName("orderId")
    val orderId: String? = null,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
