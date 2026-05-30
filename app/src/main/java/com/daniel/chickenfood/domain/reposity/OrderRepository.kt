package com.daniel.chickenfood.domain.reposity

import com.daniel.chickenfood.domain.model.OrderModel
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    
    /**
     * Crea una nueva orden
     */
    fun createOrder(order: OrderModel): Flow<String>  // Retorna orderId
    
    /**
     * Obtiene el historial de órdenes del usuario
     */
    fun getOrderHistory(userId: String): Flow<List<OrderModel>>
    
    /**
     * Obtiene una orden específica por ID
     */
    fun getOrderById(orderId: String): Flow<OrderModel>
    
    /**
     * Actualiza el estado de una orden
     */
    fun updateOrderStatus(orderId: String, status: String): Flow<Boolean>
    
    /**
     * Obtiene las órdenes completadas del usuario
     */
    fun getCompletedOrders(userId: String): Flow<List<OrderModel>>
    
    /**
     * Obtiene el total gastado por el usuario
     */
    fun getTotalSpent(userId: String): Flow<Double>
}
