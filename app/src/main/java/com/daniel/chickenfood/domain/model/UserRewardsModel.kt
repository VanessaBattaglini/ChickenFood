package com.daniel.chickenfood.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserRewardsModel(
    @SerializedName("userId")
    val userId: String = "",
    
    @SerializedName("totalPoints")
    val totalPoints: Int = 0,  // Puntos totales acumulados
    
    @SerializedName("pointsBalance")
    val pointsBalance: Int = 0,  // Puntos disponibles para canjear
    
    @SerializedName("pointsSpent")
    val pointsSpent: Int = 0,  // Puntos ya gastados
    
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    
    @SerializedName("lastUpdated")
    val lastUpdated: Long = System.currentTimeMillis(),
    
    @SerializedName("isPremium")
    val isPremium: Boolean = false  // Indica si es usuario premium
)
