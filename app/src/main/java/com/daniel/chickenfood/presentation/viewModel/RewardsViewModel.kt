package com.daniel.chickenfood.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.chickenfood.domain.model.PointsTransactionModel
import com.daniel.chickenfood.domain.model.UserRewardsModel
import com.daniel.chickenfood.domain.reposity.RewardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "RewardsViewModel"

class RewardsViewModel(
    private val rewardsRepository: RewardsRepository
) : ViewModel() {

    private val _userRewards = MutableStateFlow<UserRewardsModel?>(null)
    val userRewards: StateFlow<UserRewardsModel?> = _userRewards.asStateFlow()

    private val _pointsBalance = MutableStateFlow(0)
    val pointsBalance: StateFlow<Int> = _pointsBalance.asStateFlow()

    private val _pointsHistory = MutableStateFlow<List<PointsTransactionModel>>(emptyList())
    val pointsHistory: StateFlow<List<PointsTransactionModel>> = _pointsHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Carga los puntos de recompensa del usuario
     */
    fun loadUserRewards(userId: String) {
        Log.d(TAG, "Loading rewards for user: $userId")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                rewardsRepository.getUserRewards(userId).collect { rewards ->
                    Log.d(TAG, "Rewards loaded: $rewards")
                    _userRewards.value = rewards
                    _pointsBalance.value = rewards.pointsBalance
                    _error.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading rewards: ${e.message}", e)
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga el historial de transacciones de puntos
     */
    fun loadPointsHistory(userId: String) {
        Log.d(TAG, "Loading points history for user: $userId")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                rewardsRepository.getPointsHistory(userId).collect { transactions ->
                    Log.d(TAG, "Points history loaded: ${transactions.size} transactions")
                    _pointsHistory.value = transactions
                    _error.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading points history: ${e.message}", e)
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * Canjea puntos
     */
    fun redeemPoints(userId: String, points: Int, description: String) {
        Log.d(TAG, "Redeeming $points points for user: $userId")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                rewardsRepository.redeemPoints(userId, points, description).collect { success ->
                    if (success) {
                        Log.d(TAG, "Points redeemed successfully")
                        loadUserRewards(userId)  // Recargar recompensas
                        _error.value = null
                    } else {
                        Log.w(TAG, "Failed to redeem points")
                        _error.value = "No se pudieron canjear los puntos"
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error redeeming points: ${e.message}", e)
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * Agrega puntos por una compra
     */
    fun addPointsFromPurchase(userId: String, orderTotal: Double, orderId: String) {
        Log.d(TAG, "Adding points from purchase: $orderTotal USD for user: $userId")
        viewModelScope.launch {
            try {
                rewardsRepository.addPointsFromPurchase(userId, orderTotal, orderId).collect { success ->
                    if (success) {
                        Log.d(TAG, "Points added successfully from purchase")
                        loadUserRewards(userId)  // Recargar recompensas
                        _error.value = null
                    } else {
                        Log.w(TAG, "Failed to add points from purchase")
                        _error.value = "No se pudieron agregar los puntos"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding points from purchase: ${e.message}", e)
                _error.value = e.message
            }
        }
    }

    /**
     * Registra una transacción de puntos
     */
    fun recordPointsTransaction(transaction: PointsTransactionModel) {
        Log.d(TAG, "Recording points transaction: ${transaction.points} points for user: ${transaction.userId}")
        viewModelScope.launch {
            try {
                rewardsRepository.addPointsTransaction(transaction).collect { success ->
                    if (success) {
                        Log.d(TAG, "Points transaction recorded successfully")
                        // Recargar recompensas del usuario
                        loadUserRewards(transaction.userId)
                        _error.value = null
                    } else {
                        Log.w(TAG, "Failed to record points transaction")
                        _error.value = "No se pudo registrar la transacción de puntos"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error recording points transaction: ${e.message}", e)
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
}
