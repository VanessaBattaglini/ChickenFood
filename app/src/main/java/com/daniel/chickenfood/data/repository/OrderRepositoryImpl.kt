package com.daniel.chickenfood.data.repository

import android.util.Log
import com.daniel.chickenfood.domain.model.OrderModel
import com.daniel.chickenfood.domain.reposity.OrderRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

private const val TAG = "OrderRepositoryImpl"

class OrderRepositoryImpl(
    private val database: FirebaseDatabase,
    private val gson: Gson
) : OrderRepository {

    override fun createOrder(order: OrderModel): Flow<String> = callbackFlow {
        try {
            val orderId = order.orderId.ifEmpty { UUID.randomUUID().toString() }
            val ref = database.getReference("orders/$orderId")
            val newOrder = order.copy(orderId = orderId)
            
            ref.setValue(newOrder).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Order created successfully: $orderId")
                    trySend(orderId).isSuccess
                } else {
                    Log.e(TAG, "Failed to create order: ${task.exception?.message}")
                    close(task.exception ?: Exception("Unknown error"))
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in createOrder: ${e.message}", e)
            close(e)
        }
    }

    override fun getOrderHistory(userId: String): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getOrderHistory snapshot received for user: $userId")
                    val orders = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        gson.fromJson(json, OrderModel::class.java)
                    }.sortedByDescending { it.orderDate }
                    
                    Log.d(TAG, "Found ${orders.size} orders")
                    trySend(orders).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getOrderHistory: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getOrderHistory cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getOrderById(orderId: String): Flow<OrderModel> = callbackFlow {
        val ref = database.getReference("orders/$orderId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getOrderById snapshot received for order: $orderId")
                    if (snapshot.exists()) {
                        val json = gson.toJson(snapshot.value)
                        val order = gson.fromJson(json, OrderModel::class.java)
                        trySend(order).isSuccess
                    } else {
                        Log.w(TAG, "Order not found: $orderId")
                        close(Exception("Order not found"))
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getOrderById: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getOrderById cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun updateOrderStatus(orderId: String, status: String): Flow<Boolean> = callbackFlow {
        try {
            val ref = database.getReference("orders/$orderId/status")
            ref.setValue(status).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Order status updated: $orderId -> $status")
                    trySend(true).isSuccess
                } else {
                    Log.e(TAG, "Failed to update order status: ${task.exception?.message}")
                    trySend(false).isSuccess
                }
                close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateOrderStatus: ${e.message}", e)
            trySend(false).isSuccess
            close(e)
        }
    }

    override fun getCompletedOrders(userId: String): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getCompletedOrders snapshot received for user: $userId")
                    val orders = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        val order = gson.fromJson(json, OrderModel::class.java)
                        if (order.status == "completed") order else null
                    }.sortedByDescending { it.orderDate }
                    
                    Log.d(TAG, "Found ${orders.size} completed orders")
                    trySend(orders).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error in getCompletedOrders: ${e.message}", e)
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "getCompletedOrders cancelled: ${error.message}")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getTotalSpent(userId: String): Flow<Double> = callbackFlow {
        val ref = database.getReference("orders").orderByChild("userId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "getTotalSpent snapshot received for user: $userId")
                    var totalSpent = 0.0
                    
                    snapshot.children.forEach { child ->
                        val json = gson.toJson(child.value)
                        val order = gson.fromJson(json, OrderModel::class.java)
                        if (order.status == "completed") {
                            totalSpent += order.totalPrice
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
