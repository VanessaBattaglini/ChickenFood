package com.daniel.chickenfood.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.chickenfood.domain.model.OrderModel
import com.daniel.chickenfood.domain.reposity.OrderRepository
import com.daniel.chickenfood.domain.reposity.RewardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "OrderViewModel"

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val rewardsRepository: RewardsRepository
) : ViewModel() {

    private val _orderHistory = MutableStateFlow<List<OrderModel>>(emptyList())
    val orderHistory: StateFlow<List<OrderModel>> = _orderHistory.asStateFlow()

    private val _currentOrder = MutableStateFlow<OrderModel?>(null)
    val currentOrder: StateFlow<OrderModel?> = _currentOrder.asStateFlow()

    private val _totalSpent = MutableStateFlow(0.0)
    val totalSpent: StateFlow<Double> = _totalSpent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _orderCreatedId = MutableStateFlow<String?>(null)
    val orderCreatedId: StateFlow<String?> = _orderCreatedId.asStateFlow()

    /**
     * Crea una nueva orden
     */
    fun createOrder(order: OrderModel) {
        Log.d(TAG, "Creating order for user: ${order.userId}")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                orderRepository.createOrder(order).collect { orderId ->
                    Log.d(TAG, "Order created successfully: $orderId")
                    _orderCreatedId.value = orderId
                    
                    // Agregar puntos por la compra
                    rewardsRepository.addPointsFromPurchase(
                        order.userId,
                        order.totalPrice,
                        orderId
                    ).collect { success ->
                        if (success) {
                            Log.d(TAG, "Points added from purchase")
                        } else {
                            Log.w(TAG, "Failed to add points from purchase")
                        }
                    }
                    
                    _error.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating order: ${e.message}", e)
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga el historial de órdenes del usuario
     */
    fun loadOrderHistory(userId: String) {
        Log.d(TAG, "Loading order history for user: $userId")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                orderRepository.getOrderHistory(userId).collect { orders ->
                    Log.d(TAG, "Order history loaded: ${orders.size} orders")
                    _orderHistory.value = orders
                    _error.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading order history: ${e.message}", e)
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene una orden específica
     */
    fun getOrderById(orderId: String) {
        Log.d(TAG, "Getting order: $orderId")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                orderRepository.getOrderById(orderId).collect { order ->
                    Log.d(TAG, "Order loaded: $orderId")
                    _currentOrder.value = order
                    _error.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting order: ${e.message}", e)
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza el estado de una orden
     */
    fun updateOrderStatus(orderId: String, status: String) {
        Log.d(TAG, "Updating order status: $orderId -> $status")
        viewModelScope.launch {
            try {
                orderRepository.updateOrderStatus(orderId, status).collect { success ->
                    if (success) {
                        Log.d(TAG, "Order status updated successfully")
                        getOrderById(orderId)  // Recargar orden
                        _error.value = null
                    } else {
                        Log.w(TAG, "Failed to update order status")
                        _error.value = "No se pudo actualizar el estado de la orden"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating order status: ${e.message}", e)
                _error.value = e.message
            }
        }
    }

    /**
     * Carga el total gastado por el usuario
     */
    fun loadTotalSpent(userId: String) {
        Log.d(TAG, "Loading total spent for user: $userId")
        viewModelScope.launch {
            try {
                orderRepository.getTotalSpent(userId).collect { total ->
                    Log.d(TAG, "Total spent: $total")
                    _totalSpent.value = total
                    _error.value = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading total spent: ${e.message}", e)
                _error.value = e.message
            }
        }
    }

    /**
     * Limpia el error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Limpia el ID de orden creada
     */
    fun clearOrderCreatedId() {
        _orderCreatedId.value = null
    }
}
