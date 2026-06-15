package com.daniel.chickenfood.data.repository

import android.util.Log
import com.daniel.chickenfood.domain.model.TransactionModel
import com.daniel.chickenfood.domain.reposity.PointTransactionRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

private const val TAG = "PointTransactionRepositoryImpl"

class PointTransactionRepositoryImpl(
    private val database: FirebaseDatabase,
    private val gson: Gson
) : PointTransactionRepository {

    override fun recordTransaction(transaction: TransactionModel): Flow<String> = callbackFlow {
        try {
            val transactionId = transaction.transactionId.ifEmpty { UUID.randomUUID().toString() }
            val ref = database.getReference("pointTransactions/$transactionId")
            val newTransaction = transaction.copy(transactionId = transactionId)
            
            ref.setValue(newTransaction).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Transaction recorded successfully: $transactionId")
                    trySend(transactionId).isSuccess
                } else {
                    Log.e(TAG, "Failed to record transaction: ${task.exception?.message}")
                    close(task.exception ?: Exception("Unknown error"))
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in recordTransaction: ${e.message}", e)
            close(e)
        }
    }

    override fun getTransactionHistory(userId: String): Flow<List<TransactionModel>> = callbackFlow {
        val ref = database.getReference("pointTransactions").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getTransactionHistory snapshot received for user: $userId")
                    val transactions = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        gson.fromJson(json, TransactionModel::class.java)
                    }.sortedByDescending { it.timestamp }
                    
                    Log.d(TAG, "Found ${transactions.size} transactions")
                    trySend(transactions).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getTransactionHistory: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getTransactionHistory cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getEarnedTransactions(userId: String): Flow<List<TransactionModel>> = callbackFlow {
        val ref = database.getReference("pointTransactions").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getEarnedTransactions snapshot received for user: $userId")
                    val transactions = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        val transaction = gson.fromJson(json, TransactionModel::class.java)
                        if (transaction.type == "earned") transaction else null
                    }.sortedByDescending { it.timestamp }
                    
                    Log.d(TAG, "Found ${transactions.size} earned transactions")
                    trySend(transactions).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getEarnedTransactions: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getEarnedTransactions cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getSpentTransactions(userId: String): Flow<List<TransactionModel>> = callbackFlow {
        val ref = database.getReference("pointTransactions").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getSpentTransactions snapshot received for user: $userId")
                    val transactions = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        val transaction = gson.fromJson(json, TransactionModel::class.java)
                        if (transaction.type == "spent") transaction else null
                    }.sortedByDescending { it.timestamp }
                    
                    Log.d(TAG, "Found ${transactions.size} spent transactions")
                    trySend(transactions).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getSpentTransactions: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getSpentTransactions cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getTotalEarned(userId: String): Flow<Int> = callbackFlow {
        val ref = database.getReference("pointTransactions").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getTotalEarned snapshot received for user: $userId")
                    var totalEarned = 0
                    
                    snapshot.children.forEach { child ->
                        val json = gson.toJson(child.value)
                        val transaction = gson.fromJson(json, TransactionModel::class.java)
                        if (transaction.type == "earned") {
                            totalEarned += transaction.points
                        }
                    }
                    
                    Log.d(TAG, "Total earned by $userId: $totalEarned")
                    trySend(totalEarned).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getTotalEarned: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getTotalEarned cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getTotalSpent(userId: String): Flow<Int> = callbackFlow {
        val ref = database.getReference("pointTransactions").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getTotalSpent snapshot received for user: $userId")
                    var totalSpent = 0
                    
                    snapshot.children.forEach { child ->
                        val json = gson.toJson(child.value)
                        val transaction = gson.fromJson(json, TransactionModel::class.java)
                        if (transaction.type == "spent") {
                            totalSpent += transaction.points
                        }
                    }
                    
                    Log.d(TAG, "Total spent by $userId: $totalSpent")
                    trySend(totalSpent).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getTotalSpent: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getTotalSpent cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
