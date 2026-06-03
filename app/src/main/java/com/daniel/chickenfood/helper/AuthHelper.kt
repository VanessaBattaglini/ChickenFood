package com.daniel.chickenfood.helper

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private const val TAG = "AuthHelper"

object AuthHelper {
    
    private val firebaseAuth = FirebaseAuth.getInstance()
    
    /**
     * Obtiene el usuario actual autenticado
     */
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    
    /**
     * Obtiene el email del usuario actual
     */
    fun getUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }
    
    /**
     * Obtiene el nombre del usuario actual
     */
    fun getUserName(): String? {
        return firebaseAuth.currentUser?.displayName
    }
    
    /**
     * Obtiene el UID del usuario actual
     */
    fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
    
    /**
     * Obtiene la URL de la foto del usuario
     */
    fun getUserPhotoUrl(): String? {
        return firebaseAuth.currentUser?.photoUrl?.toString()
    }
    
    /**
     * Verifica si el usuario está autenticado
     */
    fun isUserLoggedIn(): Boolean {
        val isLoggedIn = firebaseAuth.currentUser != null
        Log.d(TAG, "isUserLoggedIn: $isLoggedIn")
        return isLoggedIn
    }
    
    /**
     * Obtiene el token de sesión de Firebase (para autenticación en backend)
     */
    fun getFirebaseToken(callback: (String?) -> Unit) {
        firebaseAuth.currentUser?.getIdToken(false)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result?.token
                Log.d(TAG, "Firebase token obtained: ${token?.take(20)}...")
                callback(token)
            } else {
                Log.e(TAG, "Failed to get Firebase token: ${task.exception?.message}")
                callback(null)
            }
        }
    }
    
    /**
     * Obtiene el refresh token de Firebase
     */
    fun getRefreshToken(): String? {
        return firebaseAuth.currentUser?.getIdToken(false).toString()
    }
    
    /**
     * Cierra la sesión del usuario
     */
    fun signOut() {
        Log.d(TAG, "Cerrando sesión del usuario: ${firebaseAuth.currentUser?.email}")
        firebaseAuth.signOut()
    }
    
    /**
     * Obtiene toda la información del usuario
     */
    fun getUserInfo(): Map<String, Any?> {
        val user = firebaseAuth.currentUser
        return mapOf(
            "uid" to user?.uid,
            "email" to user?.email,
            "displayName" to user?.displayName,
            "photoUrl" to user?.photoUrl?.toString(),
            "isEmailVerified" to user?.isEmailVerified,
            "metadata" to user?.metadata?.creationTimestamp
        )
    }
    
    /**
     * Obtiene el proveedor de autenticación del usuario
     */
    fun getAuthProvider(): String? {
        val user = firebaseAuth.currentUser
        return user?.providerData?.firstOrNull()?.providerId
    }
    
    /**
     * Verifica si el usuario se autenticó con Google
     */
    fun isGoogleAuthenticated(): Boolean {
        val provider = getAuthProvider()
        Log.d(TAG, "Auth provider: $provider")
        return provider == "google.com"
    }
}
