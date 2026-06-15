package com.daniel.chickenfood.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentModel(
    @SerializedName("method")
    val method: String = "", // "card" or "points"
    
    @SerializedName("cardNumber")
    val cardNumber: String? = null, // Only if method = "card"
    
    @SerializedName("cardHolder")
    val cardHolder: String? = null,
    
    @SerializedName("expiryDate")
    val expiryDate: String? = null, // MM/YY format
    
    @SerializedName("cvc")
    val cvc: String? = null,
    
    @SerializedName("pointsToUse")
    val pointsToUse: Int? = null, // Only if method = "points"
    
    @SerializedName("amount")
    val amount: Double = 0.0
)
