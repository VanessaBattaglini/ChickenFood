package com.daniel.chickenfood.domain.reposity

import com.daniel.chickenfood.domain.model.PointsTransactionModel
import com.daniel.chickenfood.domain.model.UserRewardsModel
import kotlinx.coroutines.flow.Flow

interface RewardsRepository {
    
    /**
     * Obtiene los puntos de recompensa del usuario
     */
    fun getUserRewards(userId: String): Flow<UserRewardsModel>
    
    /**
     * Crea o actualiza los puntos de recompensa del usuario
     */
    fun updateUserRewards(rewards: UserRewardsModel): Flow<Boolean>
    
    /**
     * Agrega una transacción de puntos
     */
    fun addPointsTransaction(transaction: PointsTransactionModel): Flow<Boolean>
    
    /**
     * Obtiene el historial de transacciones del usuario
     */
    fun getPointsHistory(userId: String): Flow<List<PointsTransactionModel>>
    
    /**
     * Canjea puntos del usuario
     */
    fun redeemPoints(userId: String, points: Int, description: String): Flow<Boolean>
    
    /**
     * Agrega puntos por una compra
     */
    fun addPointsFromPurchase(userId: String, orderTotal: Double, orderId: String): Flow<Boolean>
    
    /**
     * Obtiene el saldo de puntos disponibles
     */
    fun getPointsBalance(userId: String): Flow<Int>
    
    /**
     * Suma puntos al usuario (por compra con tarjeta)
     */
    fun addPoints(userId: String, points: Int, reason: String): Flow<Int> // Retorna nuevo saldo
    
    /**
     * Resta puntos del usuario (pago con puntos)
     */
    fun deductPoints(userId: String, points: Int, reason: String): Flow<Int> // Retorna nuevo saldo
    
    /**
     * Obtiene los puntos actuales del usuario
     */
    fun getCurrentPoints(userId: String): Flow<Int>
}
