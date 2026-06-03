package com.daniel.chickenfood.domain.reposity

import com.daniel.chickenfood.domain.model.UserTokenModel
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    
    /**
     * Guarda el token del usuario después de autenticarse con Google
     */
    fun saveUserToken(token: UserTokenModel): Flow<Boolean>
    
    /**
     * Obtiene el token guardado del usuario
     */
    fun getUserToken(userId: String): Flow<UserTokenModel>
    
    /**
     * Actualiza el token de Firebase (cuando se renueva)
     */
    fun updateFirebaseToken(userId: String, firebaseToken: String, refreshToken: String): Flow<Boolean>
    
    /**
     * Actualiza la fecha del último login
     */
    fun updateLastLogin(userId: String): Flow<Boolean>
    
    /**
     * Verifica si el token es válido (no expirado)
     */
    fun isTokenValid(userId: String): Flow<Boolean>
    
    /**
     * Elimina el token del usuario (logout)
     */
    fun deleteUserToken(userId: String): Flow<Boolean>
    
    /**
     * Obtiene todos los tokens activos del usuario (para múltiples dispositivos)
     */
    fun getActiveTokens(userId: String): Flow<List<UserTokenModel>>
    
    /**
     * Revoca un token específico
     */
    fun revokeToken(userId: String, tokenId: String): Flow<Boolean>
}
