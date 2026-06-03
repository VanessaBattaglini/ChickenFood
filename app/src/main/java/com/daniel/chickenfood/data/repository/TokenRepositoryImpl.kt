package com.daniel.chickenfood.data.repository

import android.util.Log
import com.daniel.chickenfood.domain.model.UserTokenModel
import com.daniel.chickenfood.domain.reposity.TokenRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

private const val TAG = "TokenRepositoryImpl"

class TokenRepositoryImpl(
    private val database: FirebaseDatabase,
    private val gson: Gson
) : TokenRepository {

    override fun saveUserToken(token: UserTokenModel): Flow<Boolean> = callbackFlow {
        try {
            val ref = database.getReference("users/${token.userId}/tokens/${UUID.randomUUID()}")
            
            ref.setValue(token).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User token saved successfully for ${token.userId}")
                    trySend(true).isSuccess
                } else {
                    Log.e(TAG, "Failed to save user token: ${task.exception?.message}")
                    trySend(false).isSuccess
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in saveUserToken: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
        awaitClose { }
    }

    override fun getUserToken(userId: String): Flow<UserTokenModel> = callbackFlow {
        val ref = database.getReference("users/$userId/tokens")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getUserToken snapshot received for user: $userId")
                    
                    // Obtener el token más reciente (último login)
                    var latestToken: UserTokenModel? = null
                    var latestTime = 0L
                    
                    snapshot.children.forEach { child ->
                        val json = gson.toJson(child.value)
                        val token = gson.fromJson(json, UserTokenModel::class.java)
                        
                        if (token.isActive && token.lastLoginAt > latestTime) {
                            latestToken = token
                            latestTime = token.lastLoginAt
                        }
                    }
                    
                    if (latestToken != null) {
                        Log.d(TAG, "Latest token found for $userId")
                        trySend(latestToken!!).isSuccess
                    } else {
                        Log.w(TAG, "No active token found for $userId")
                        close(Exception("No active token found"))
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getUserToken: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getUserToken cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun updateFirebaseToken(userId: String, firebaseToken: String, refreshToken: String): Flow<Boolean> = callbackFlow {
        try {
            val ref = database.getReference("users/$userId/tokens")
            
            ref.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    
                    // Actualizar todos los tokens activos del usuario
                    snapshot.children.forEach { child ->
                        val tokenRef = child.ref
                        val json = gson.toJson(child.value)
                        val token = gson.fromJson(json, UserTokenModel::class.java)
                        
                        if (token.isActive) {
                            val updatedToken = token.copy(
                                firebaseToken = firebaseToken,
                                refreshToken = refreshToken,
                                lastLoginAt = System.currentTimeMillis()
                            )
                            tokenRef.setValue(updatedToken)
                        }
                    }
                    
                    Log.d(TAG, "Firebase token updated for $userId")
                    trySend(true).isSuccess
                } else {
                    Log.e(TAG, "Failed to update firebase token: ${task.exception?.message}")
                    trySend(false).isSuccess
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateFirebaseToken: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
        awaitClose { }
    }

    override fun updateLastLogin(userId: String): Flow<Boolean> = callbackFlow {
        try {
            val ref = database.getReference("users/$userId/lastLogin")
            ref.setValue(System.currentTimeMillis()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Last login updated for $userId")
                    trySend(true).isSuccess
                } else {
                    Log.e(TAG, "Failed to update last login: ${task.exception?.message}")
                    trySend(false).isSuccess
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateLastLogin: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
        awaitClose { }
    }

    override fun isTokenValid(userId: String): Flow<Boolean> = callbackFlow {
        try {
            val ref = database.getReference("users/$userId/tokens")
            ref.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val currentTime = System.currentTimeMillis()
                    
                    var isValid = false
                    snapshot.children.forEach { child ->
                        val json = gson.toJson(child.value)
                        val token = gson.fromJson(json, UserTokenModel::class.java)
                        
                        // Token es válido si está activo y no ha expirado
                        if (token.isActive && (token.expiresAt == 0L || token.expiresAt > currentTime)) {
                            isValid = true
                        }
                    }
                    
                    Log.d(TAG, "Token valid for $userId: $isValid")
                    trySend(isValid).isSuccess
                } else {
                    Log.e(TAG, "Failed to check token validity: ${task.exception?.message}")
                    trySend(false).isSuccess
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in isTokenValid: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
        awaitClose { }
    }

    override fun deleteUserToken(userId: String): Flow<Boolean> = callbackFlow {
        try {
            val ref = database.getReference("users/$userId/tokens")
            
            ref.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    
                    // Marcar todos los tokens como inactivos (logout)
                    snapshot.children.forEach { child ->
                        val tokenRef = child.ref
                        val json = gson.toJson(child.value)
                        val token = gson.fromJson(json, UserTokenModel::class.java)
                        
                        val inactiveToken = token.copy(isActive = false)
                        tokenRef.setValue(inactiveToken)
                    }
                    
                    Log.d(TAG, "User tokens deleted for $userId")
                    trySend(true).isSuccess
                } else {
                    Log.e(TAG, "Failed to delete user tokens: ${task.exception?.message}")
                    trySend(false).isSuccess
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in deleteUserToken: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
        awaitClose { }
    }

    override fun getActiveTokens(userId: String): Flow<List<UserTokenModel>> = callbackFlow {
        val ref = database.getReference("users/$userId/tokens")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getActiveTokens snapshot received for user: $userId")
                    
                    val activeTokens = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        val token = gson.fromJson(json, UserTokenModel::class.java)
                        if (token.isActive) token else null
                    }.sortedByDescending { it.lastLoginAt }
                    
                    Log.d(TAG, "Found ${activeTokens.size} active tokens")
                    trySend(activeTokens).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getActiveTokens: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getActiveTokens cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun revokeToken(userId: String, tokenId: String): Flow<Boolean> = callbackFlow {
        try {
            val ref = database.getReference("users/$userId/tokens/$tokenId")
            
            ref.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    if (snapshot.exists()) {
                        val json = gson.toJson(snapshot.value)
                        val token = gson.fromJson(json, UserTokenModel::class.java)
                        
                        val revokedToken = token.copy(isActive = false)
                        ref.setValue(revokedToken).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Log.d(TAG, "Token revoked: $tokenId")
                                trySend(true).isSuccess
                            } else {
                                Log.e(TAG, "Failed to revoke token: ${updateTask.exception?.message}")
                                trySend(false).isSuccess
                            }
                            close()
                        }
                    } else {
                        Log.w(TAG, "Token not found: $tokenId")
                        trySend(false).isSuccess
                        close()
                    }
                } else {
                    Log.e(TAG, "Failed to get token: ${task.exception?.message}")
                    trySend(false).isSuccess
                    close()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in revokeToken: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
        awaitClose { }
    }
}
