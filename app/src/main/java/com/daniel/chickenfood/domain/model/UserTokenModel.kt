package com.daniel.chickenfood.domain.model

data class UserTokenModel(
    val userId: String = "",  // UID de Firebase Auth
    val email: String = "",  // Email del usuario
    val googleIdToken: String = "",  // ID Token de Google (JWT)
    val firebaseToken: String = "",  // Token de sesión de Firebase
    val refreshToken: String = "",  // Refresh token de Firebase
    val displayName: String = "",  // Nombre del usuario
    val photoUrl: String = "",  // URL de foto de perfil
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0,  // Fecha de expiración del token
    val isActive: Boolean = true  // Si la sesión está activa
)
