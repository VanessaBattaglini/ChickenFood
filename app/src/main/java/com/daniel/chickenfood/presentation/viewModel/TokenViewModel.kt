package com.daniel.chickenfood.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.chickenfood.domain.model.UserTokenModel
import com.daniel.chickenfood.domain.reposity.TokenRepository
import com.daniel.chickenfood.helper.AppConfigs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "TokenViewModel"

/**
 * ViewModel para gestionar tokens de usuario
 * 
 * Responsabilidades:
 * - Guardar tokens después de autenticación con Google
 * - Obtener tokens guardados
 * - Actualizar tokens cuando se renuevan
 * - Verificar validez de tokens
 * - Gestionar logout (eliminar tokens)
 * - Soportar múltiples dispositivos
 */
class TokenViewModel(
    private val tokenRepository: TokenRepository
) : ViewModel() {

    // Estado para guardar token
    private val _saveTokenState = MutableStateFlow<TokenState>(TokenState.Idle)
    val saveTokenState: StateFlow<TokenState> = _saveTokenState.asStateFlow()

    // Estado para obtener token
    private val _getUserTokenState = MutableStateFlow<TokenState>(TokenState.Idle)
    val getUserTokenState: StateFlow<TokenState> = _getUserTokenState.asStateFlow()

    // Estado para actualizar token
    private val _updateTokenState = MutableStateFlow<TokenState>(TokenState.Idle)
    val updateTokenState: StateFlow<TokenState> = _updateTokenState.asStateFlow()

    // Estado para verificar validez
    private val _isTokenValidState = MutableStateFlow<TokenValidState>(TokenValidState.Idle)
    val isTokenValidState: StateFlow<TokenValidState> = _isTokenValidState.asStateFlow()

    // Estado para obtener tokens activos
    private val _activeTokensState = MutableStateFlow<ActiveTokensState>(ActiveTokensState.Idle)
    val activeTokensState: StateFlow<ActiveTokensState> = _activeTokensState.asStateFlow()

    // Estado para logout
    private val _logoutState = MutableStateFlow<TokenState>(TokenState.Idle)
    val logoutState: StateFlow<TokenState> = _logoutState.asStateFlow()

    /**
     * Guarda el token del usuario después de autenticarse con Google
     */
    fun saveUserToken(token: UserTokenModel) {
        Log.d(TAG, "Guardando token para usuario: ${token.userId}")
        _saveTokenState.value = TokenState.Loading

        viewModelScope.launch {
            try {
                tokenRepository.saveUserToken(token).collect { success ->
                    if (success) {
                        Log.d(TAG, "Token guardado exitosamente para ${token.userId}")
                        _saveTokenState.value = TokenState.Success(token)
                        AppConfigs.saveToken(token)
                    } else {
                        Log.e(TAG, "Error al guardar token")
                        _saveTokenState.value = TokenState.Error("Error al guardar token")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en saveUserToken: ${e.message}", e)
                _saveTokenState.value = TokenState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Obtiene el token guardado del usuario
     */
    fun getUserToken(userId: String) {
        Log.d(TAG, "Obteniendo token para usuario: $userId")
        _getUserTokenState.value = TokenState.Loading

        viewModelScope.launch {
            try {
                tokenRepository.getUserToken(userId).collect { token ->
                    Log.d(TAG, "Token obtenido para $userId: ${token.email}")
                    _getUserTokenState.value = TokenState.Success(token)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en getUserToken: ${e.message}", e)
                _getUserTokenState.value = TokenState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Actualiza el token de Firebase cuando se renueva
     */
    fun updateFirebaseToken(userId: String, firebaseToken: String, refreshToken: String) {
        Log.d(TAG, "Actualizando Firebase token para usuario: $userId")
        _updateTokenState.value = TokenState.Loading

        viewModelScope.launch {
            try {
                tokenRepository.updateFirebaseToken(userId, firebaseToken, refreshToken).collect { success ->
                    if (success) {
                        Log.d(TAG, "Firebase token actualizado para $userId")
                        _updateTokenState.value = TokenState.Success(UserTokenModel())
                    } else {
                        Log.e(TAG, "Error al actualizar Firebase token")
                        _updateTokenState.value = TokenState.Error("Error al actualizar token")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en updateFirebaseToken: ${e.message}", e)
                _updateTokenState.value = TokenState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Verifica si el token es válido (no expirado)
     */
    fun isTokenValid(userId: String) {
        Log.d(TAG, "Verificando validez del token para usuario: $userId")
        _isTokenValidState.value = TokenValidState.Loading

        viewModelScope.launch {
            try {
                tokenRepository.isTokenValid(userId).collect { isValid ->
                    Log.d(TAG, "Token válido para $userId: $isValid")
                    _isTokenValidState.value = TokenValidState.Success(isValid)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en isTokenValid: ${e.message}", e)
                _isTokenValidState.value = TokenValidState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Obtiene todos los tokens activos del usuario (para múltiples dispositivos)
     */
    fun getActiveTokens(userId: String) {
        Log.d(TAG, "Obteniendo tokens activos para usuario: $userId")
        _activeTokensState.value = ActiveTokensState.Loading

        viewModelScope.launch {
            try {
                tokenRepository.getActiveTokens(userId).collect { tokens ->
                    Log.d(TAG, "Se encontraron ${tokens.size} tokens activos para $userId")
                    _activeTokensState.value = ActiveTokensState.Success(tokens)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en getActiveTokens: ${e.message}", e)
                _activeTokensState.value = ActiveTokensState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Elimina el token del usuario (logout)
     */
    fun logout(userId: String) {
        Log.d(TAG, "Realizando logout para usuario: $userId")
        _logoutState.value = TokenState.Loading

        viewModelScope.launch {
            try {
                tokenRepository.deleteUserToken(userId).collect { success ->
                    if (success) {
                        Log.d(TAG, "Logout exitoso para $userId")
                        _logoutState.value = TokenState.Success(UserTokenModel())
                    } else {
                        Log.e(TAG, "Error al realizar logout")
                        _logoutState.value = TokenState.Error("Error al realizar logout")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en logout: ${e.message}", e)
                _logoutState.value = TokenState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Revoca un token específico (para múltiples dispositivos)
     */
    fun revokeToken(userId: String, tokenId: String) {
        Log.d(TAG, "Revocando token: $tokenId para usuario: $userId")
        _logoutState.value = TokenState.Loading

        viewModelScope.launch {
            try {
                tokenRepository.revokeToken(userId, tokenId).collect { success ->
                    if (success) {
                        Log.d(TAG, "Token revocado exitosamente: $tokenId")
                        _logoutState.value = TokenState.Success(UserTokenModel())
                    } else {
                        Log.e(TAG, "Error al revocar token")
                        _logoutState.value = TokenState.Error("Error al revocar token")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en revokeToken: ${e.message}", e)
                _logoutState.value = TokenState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Limpia los estados
     */
    fun clearStates() {
        _saveTokenState.value = TokenState.Idle
        _getUserTokenState.value = TokenState.Idle
        _updateTokenState.value = TokenState.Idle
        _isTokenValidState.value = TokenValidState.Idle
        _activeTokensState.value = ActiveTokensState.Idle
        _logoutState.value = TokenState.Idle
    }
}

/**
 * Estados para operaciones de token
 */
sealed class TokenState {
    object Idle : TokenState()
    object Loading : TokenState()
    data class Success(val data: UserTokenModel) : TokenState()
    data class Error(val message: String) : TokenState()
}

/**
 * Estados para verificación de validez
 */
sealed class TokenValidState {
    object Idle : TokenValidState()
    object Loading : TokenValidState()
    data class Success(val isValid: Boolean) : TokenValidState()
    data class Error(val message: String) : TokenValidState()
}

/**
 * Estados para obtener tokens activos
 */
sealed class ActiveTokensState {
    object Idle : ActiveTokensState()
    object Loading : ActiveTokensState()
    data class Success(val tokens: List<UserTokenModel>) : ActiveTokensState()
    data class Error(val message: String) : ActiveTokensState()
}
