package com.daniel.chickenfood.data.repository

import android.util.Log
import com.daniel.chickenfood.domain.model.PointsTransactionModel
import com.daniel.chickenfood.domain.model.TransactionModel
import com.daniel.chickenfood.domain.model.UserRewardsModel
import com.daniel.chickenfood.domain.reposity.RewardsRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

private const val TAG = "RewardsRepositoryImpl"
private const val POINTS_PERCENTAGE = 0.10  // 10% cashback

class RewardsRepositoryImpl(
    private val database: FirebaseDatabase,
    private val gson: Gson
) : RewardsRepository {

    override fun getUserRewards(userId: String): Flow<UserRewardsModel> = callbackFlow {
        val ref = database.getReference("users/$userId/rewards")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getUserRewards snapshot received for user: $userId")
                    val rewards = if (snapshot.exists()) {
                        val json = gson.toJson(snapshot.value)
                        Log.d(TAG, "Rewards JSON: $json")
                        val parsed = gson.fromJson(json, UserRewardsModel::class.java)
                        parsed.copy(userId = userId)
                    } else {
                        Log.d(TAG, "No rewards found, creating default")
                        UserRewardsModel(userId = userId)
                    }
                    trySend(rewards).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getUserRewards: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getUserRewards cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun updateUserRewards(rewards: UserRewardsModel): Flow<Boolean> = callbackFlow {
        try {
            val ref = database.getReference("users/${rewards.userId}/rewards")
            val updatedRewards = rewards.copy(lastUpdated = System.currentTimeMillis())
            ref.setValue(updatedRewards).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User rewards updated successfully for ${rewards.userId}")
                    trySend(true).isSuccess
                } else {
                    Log.e(TAG, "Failed to update rewards: ${task.exception?.message}")
                    trySend(false).isSuccess
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateUserRewards: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
    }

    override fun addPointsTransaction(transaction: PointsTransactionModel): Flow<Boolean> = callbackFlow {
        try {
            val transactionId = transaction.transactionId.ifEmpty { UUID.randomUUID().toString() }
            val ref = database.getReference("pointsTransactions/$transactionId")
            val newTransaction = transaction.copy(transactionId = transactionId)
            
            ref.setValue(newTransaction).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Points transaction added: $transactionId")
                    trySend(true).isSuccess
                } else {
                    Log.e(TAG, "Failed to add transaction: ${task.exception?.message}")
                    trySend(false).isSuccess
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addPointsTransaction: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
    }

    override fun getPointsHistory(userId: String): Flow<List<PointsTransactionModel>> = callbackFlow {
        val ref = database.getReference("pointsTransactions").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getPointsHistory snapshot received for user: $userId")
                    val transactions = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        gson.fromJson(json, PointsTransactionModel::class.java)
                    }.sortedByDescending { it.timestamp }
                    
                    Log.d(TAG, "Found ${transactions.size} transactions")
                    trySend(transactions).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getPointsHistory: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getPointsHistory cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun redeemPoints(userId: String, points: Int, description: String): Flow<Boolean> = callbackFlow {
        try {
            // Obtener recompensas actuales
            val rewardsRef = database.getReference("users/$userId/rewards")
            rewardsRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val json = gson.toJson(snapshot.value)
                    val currentRewards = gson.fromJson(json, UserRewardsModel::class.java)
                        .copy(userId = userId)
                    
                    if (currentRewards.pointsBalance >= points) {
                        // Actualizar recompensas
                        val newRewards = currentRewards.copy(
                            pointsBalance = currentRewards.pointsBalance - points,
                            pointsSpent = currentRewards.pointsSpent + points,
                            lastUpdated = System.currentTimeMillis()
                        )
                        
                        rewardsRef.setValue(newRewards).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Crear transacción
                                val transaction = PointsTransactionModel(
                                    transactionId = UUID.randomUUID().toString(),
                                    userId = userId,
                                    points = -points,
                                    type = "redemption",
                                    description = description,
                                    balanceBefore = currentRewards.pointsBalance,
                                    balanceAfter = newRewards.pointsBalance,
                                    timestamp = System.currentTimeMillis()
                                )
                                
                                val transRef = database.getReference("pointsTransactions/${transaction.transactionId}")
                                transRef.setValue(transaction).addOnCompleteListener { transTask ->
                                    if (transTask.isSuccessful) {
                                        Log.d(TAG, "Points redeemed successfully: $points points")
                                        trySend(true).isSuccess
                                    } else {
                                        Log.e(TAG, "Failed to create transaction: ${transTask.exception?.message}")
                                        trySend(false).isSuccess
                                    }
                                    close()
                                }
                            } else {
                                Log.e(TAG, "Failed to update rewards: ${updateTask.exception?.message}")
                                trySend(false).isSuccess
                                close()
                            }
                        }
                    } else {
                        Log.w(TAG, "Insufficient points: ${currentRewards.pointsBalance} < $points")
                        trySend(false).isSuccess
                        close()
                    }
                } else {
                    Log.e(TAG, "Failed to get rewards: ${task.exception?.message}")
                    trySend(false).isSuccess
                    close()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in redeemPoints: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
    }

    override fun addPointsFromPurchase(userId: String, orderTotal: Double, orderId: String): Flow<Boolean> = callbackFlow {
        try {
            // Calcular puntos (10% del total)
            val pointsToAdd = (orderTotal * POINTS_PERCENTAGE).toInt()
            
            Log.d(TAG, "Adding points from purchase: $orderTotal USD = $pointsToAdd points")
            
            // Obtener recompensas actuales
            val rewardsRef = database.getReference("users/$userId/rewards")
            rewardsRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val json = gson.toJson(snapshot.value)
                    val currentRewards = if (snapshot.exists()) {
                        gson.fromJson(json, UserRewardsModel::class.java).copy(userId = userId)
                    } else {
                        UserRewardsModel(userId = userId)
                    }
                    
                    // Actualizar recompensas
                    val newRewards = currentRewards.copy(
                        totalPoints = currentRewards.totalPoints + pointsToAdd,
                        pointsBalance = currentRewards.pointsBalance + pointsToAdd,
                        lastUpdated = System.currentTimeMillis()
                    )
                    
                    rewardsRef.setValue(newRewards).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            // Crear transacción
                            val transaction = PointsTransactionModel(
                                transactionId = UUID.randomUUID().toString(),
                                userId = userId,
                                points = pointsToAdd,
                                type = "purchase",
                                description = "Cashback 10% - Compra de $orderTotal USD",
                                orderId = orderId,
                                balanceBefore = currentRewards.pointsBalance,
                                balanceAfter = newRewards.pointsBalance,
                                timestamp = System.currentTimeMillis()
                            )
                            
                            val transRef = database.getReference("pointsTransactions/${transaction.transactionId}")
                            transRef.setValue(transaction).addOnCompleteListener { transTask ->
                                if (transTask.isSuccessful) {
                                    Log.d(TAG, "Points added successfully from purchase")
                                    trySend(true).isSuccess
                                } else {
                                    Log.e(TAG, "Failed to create transaction: ${transTask.exception?.message}")
                                    trySend(false).isSuccess
                                }
                                close()
                            }
                        } else {
                            Log.e(TAG, "Failed to update rewards: ${updateTask.exception?.message}")
                            trySend(false).isSuccess
                            close()
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to get rewards: ${task.exception?.message}")
                    trySend(false).isSuccess
                    close()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addPointsFromPurchase: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
    }

    override fun getPointsBalance(userId: String): Flow<Int> = callbackFlow {
        val ref = database.getReference("users/$userId/rewards/pointsBalance")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val balance = snapshot.getValue(Int::class.java) ?: 0
                    Log.d(TAG, "Points balance for $userId: $balance")
                    trySend(balance).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getPointsBalance: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getPointsBalance cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun addPoints(userId: String, points: Int, reason: String): Flow<Int> = callbackFlow {
        try {
            Log.d(TAG, "Adding $points points to user $userId (reason: $reason)")
            
            // Obtener recompensas actuales
            val rewardsRef = database.getReference("users/$userId/rewards")
            rewardsRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val json = gson.toJson(snapshot.value)
                    val currentRewards = if (snapshot.exists()) {
                        gson.fromJson(json, UserRewardsModel::class.java).copy(userId = userId)
                    } else {
                        UserRewardsModel(userId = userId)
                    }
                    
                    // Actualizar recompensas
                    val newBalance = currentRewards.pointsBalance + points
                    val newRewards = currentRewards.copy(
                        totalPoints = currentRewards.totalPoints + points,
                        pointsBalance = newBalance,
                        lastUpdated = System.currentTimeMillis()
                    )
                    
                    rewardsRef.setValue(newRewards).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Log.d(TAG, "Points added successfully. New balance: $newBalance")
                            trySend(newBalance).isSuccess
                        } else {
                            Log.e(TAG, "Failed to add points: ${updateTask.exception?.message}")
                            close(updateTask.exception ?: Exception("Unknown error"))
                        }
                        close()
                    }
                } else {
                    Log.e(TAG, "Failed to get rewards: ${task.exception?.message}")
                    close(task.exception ?: Exception("Unknown error"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addPoints: ${e.message}", e)
            close(e)
        }
    }

    override fun deductPoints(userId: String, points: Int, reason: String): Flow<Int> = callbackFlow {
        try {
            Log.d(TAG, "Deducting $points points from user $userId (reason: $reason)")
            
            // Obtener recompensas actuales
            val rewardsRef = database.getReference("users/$userId/rewards")
            rewardsRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val json = gson.toJson(snapshot.value)
                    val currentRewards = if (snapshot.exists()) {
                        gson.fromJson(json, UserRewardsModel::class.java).copy(userId = userId)
                    } else {
                        UserRewardsModel(userId = userId)
                    }
                    
                    // Validar saldo suficiente
                    if (currentRewards.pointsBalance < points) {
                        Log.w(TAG, "Insufficient points: ${currentRewards.pointsBalance} < $points")
                        close(Exception("Insufficient points"))
                        return@addOnCompleteListener
                    }
                    
                    // Actualizar recompensas
                    val newBalance = currentRewards.pointsBalance - points
                    val newRewards = currentRewards.copy(
                        pointsBalance = newBalance,
                        pointsSpent = currentRewards.pointsSpent + points,
                        lastUpdated = System.currentTimeMillis()
                    )
                    
                    rewardsRef.setValue(newRewards).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Log.d(TAG, "Points deducted successfully. New balance: $newBalance")
                            trySend(newBalance).isSuccess
                        } else {
                            Log.e(TAG, "Failed to deduct points: ${updateTask.exception?.message}")
                            close(updateTask.exception ?: Exception("Unknown error"))
                        }
                        close()
                    }
                } else {
                    Log.e(TAG, "Failed to get rewards: ${task.exception?.message}")
                    close(task.exception ?: Exception("Unknown error"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in deductPoints: ${e.message}", e)
            close(e)
        }
    }

    override fun getCurrentPoints(userId: String): Flow<Int> = callbackFlow {
        val ref = database.getReference("users/$userId/rewards/pointsBalance")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val balance = snapshot.getValue(Int::class.java) ?: 0
                    Log.d(TAG, "Current points for $userId: $balance")
                    trySend(balance).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getCurrentPoints: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getCurrentPoints cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
