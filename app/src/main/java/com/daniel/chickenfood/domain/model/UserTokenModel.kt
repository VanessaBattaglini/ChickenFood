package com.daniel.chickenfood.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserTokenModel(
    @SerializedName("userId")
    val userId: String = "",  // UID de Firebase Auth
    
    @SerializedName("email")
    val email: String = "",  // Email del usuario
    
    @SerializedName("googleIdToken")
    val googleIdToken: String = "",  // ID Token de Google (JWT)
    
    @SerializedName("firebaseToken")
    val firebaseToken: String = "",  // Token de sesión de Firebase
    
    @SerializedName("refreshToken")
    val refreshToken: String = "",  // Refresh token de Firebase
    
    @SerializedName("displayName")
    val displayName: String = "",  // Nombre del usuario
    
    @SerializedName("photoUrl")
    val photoUrl: String = "",  // URL de foto de perfil
    
    @SerializedName("isEmailVerified")
    val isEmailVerified: Boolean = false,
    
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    
    @SerializedName("lastLoginAt")
    val lastLoginAt: Long = System.currentTimeMillis(),
    
    @SerializedName("expiresAt")
    val expiresAt: Long = 0,  // Fecha de expiración del token
    
    @SerializedName("isActive")
    val isActive: Boolean = true  // Si la sesión está activa
)
