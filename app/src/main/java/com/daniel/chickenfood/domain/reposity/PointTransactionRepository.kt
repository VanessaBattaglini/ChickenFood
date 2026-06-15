package com.daniel.chickenfood.domain.reposity

import com.daniel.chickenfood.domain.model.TransactionModel
import kotlinx.coroutines.flow.Flow

interface PointTransactionRepository {
    
    /**
     * Guarda una transacción de puntos
     */
    fun recordTransaction(transaction: TransactionModel): Flow<String> // Retorna transactionId
    
    /**
     * Obtiene el historial de transacciones del usuario
     */
    fun getTransactionHistory(userId: String): Flow<List<TransactionModel>>
    
    /**
     * Obtiene las transacciones de tipo "earned"
     */
    fun getEarnedTransactions(userId: String): Flow<List<TransactionModel>>
    
    /**
     * Obtiene las transacciones de tipo "spent"
     */
    fun getSpentTransactions(userId: String): Flow<List<TransactionModel>>
    
    /**
     * Obtiene el total de puntos ganados
     */
    fun getTotalEarned(userId: String): Flow<Int>
    
    /**
     * Obtiene el total de puntos gastados
     */
    fun getTotalSpent(userId: String): Flow<Int>
}
