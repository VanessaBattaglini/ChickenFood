package com.daniel.chickenfood.helper

import android.content.Context
import android.util.Log
import com.daniel.chickenfood.domain.model.UserTokenModel
import com.google.gson.Gson

private const val TAG = "AppConfigs"
private const val PREFS_NAME = "chicken_food_prefs"
private const val KEY_TOKEN = "app_token"

object AppConfigs {
    var appToken: UserTokenModel? = null
    
    private var context: Context? = null
    private val gson = Gson()

    /**
     * Inicializa AppConfigs con el contexto de la aplicación
     * DEBE llamarse en Application.onCreate() o al iniciar la app
     */
    fun init(applicationContext: Context) {
        Log.d(TAG, "Initializing AppConfigs")
        this.context = applicationContext
        appToken = loadTokenFromStorage()
        Log.d(TAG, "AppConfigs initialized, token loaded: ${appToken?.userId ?: "null"}")
    }

    /**
     * Guarda el token en memoria Y en SharedPreferences (persistencia)
     */
    fun saveToken(token: UserTokenModel) {
        Log.d(TAG, "Saving token for user: ${token.userId}")
        appToken = token
        context?.let { ctx ->
            try {
                val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val tokenJson = gson.toJson(token)
                prefs.edit().putString(KEY_TOKEN, tokenJson).apply()
                Log.d(TAG, "Token persisted to SharedPreferences for user: ${token.userId}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving token to SharedPreferences: ${e.message}", e)
            }
        }
    }

    /**
     * Carga el token desde SharedPreferences (persistencia entre aperturas del app)
     */
    private fun loadTokenFromStorage(): UserTokenModel? {
        return context?.let { ctx ->
            try {
                val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val tokenJson = prefs.getString(KEY_TOKEN, null)
                if (tokenJson != null) {
                    val token = gson.fromJson(tokenJson, UserTokenModel::class.java)
                    Log.d(TAG, "Token loaded from SharedPreferences for user: ${token.userId}")
                    return token
                }
                Log.d(TAG, "No token found in SharedPreferences")
                return null
            } catch (e: Exception) {
                Log.e(TAG, "Error loading token from SharedPreferences: ${e.message}", e)
                return null
            }
        }
    }

    /**
     * Limpia el token de memoria Y SharedPreferences (logout)
     */
    fun clearToken() {
        Log.d(TAG, "Clearing token")
        appToken = null
        context?.let { ctx ->
            try {
                val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().remove(KEY_TOKEN).apply()
                Log.d(TAG, "Token cleared from SharedPreferences")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing token from SharedPreferences: ${e.message}", e)
            }
        }
    }

    /**
     * Obtiene el estado del token de forma segura
     */
    fun isTokenValid(): Boolean {
        return appToken != null && appToken?.userId?.isNotEmpty() == true
    }

    /**
     * Obtiene el userId actual de forma segura
     */
    fun getCurrentUserId(): String? {
        return appToken?.userId
    }
}