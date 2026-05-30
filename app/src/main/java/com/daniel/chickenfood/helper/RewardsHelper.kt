package com.daniel.chickenfood.helper

import android.util.Log
import com.daniel.chickenfood.domain.model.OrderItemModel
import com.daniel.chickenfood.domain.model.OrderModel
import com.daniel.chickenfood.domain.model.FoodModel
import java.util.UUID

private const val TAG = "RewardsHelper"
private const val POINTS_PERCENTAGE = 0.10  // 10% cashback

object RewardsHelper {
    
    /**
     * Calcula los puntos a ganar basado en el total de la compra
     * 10% del total = puntos ganados
     * Ejemplo: $100 USD = 10 puntos
     */
    fun calculatePointsFromTotal(totalPrice: Double): Int {
        val points = (totalPrice * POINTS_PERCENTAGE).toInt()
        Log.d(TAG, "Calculated points: $totalPrice USD = $points points")
        return points
    }
    
    /**
     * Convierte una lista de FoodModel a OrderItemModel
     */
    fun convertFoodToOrderItems(foodList: List<FoodModel>): List<OrderItemModel> {
        return foodList.map { food ->
            OrderItemModel(
                foodId = food.id,
                title = food.title,
                price = food.price.toDouble(),
                quantity = food.numberInCart,
                subtotal = (food.price * food.numberInCart).toDouble(),
                imagePath = food.imagePath
            )
        }
    }
    
    /**
     * Crea una orden a partir del carrito
     */
    fun createOrderFromCart(
        userId: String,
        cartItems: List<FoodModel>,
        totalPrice: Double,
        deliveryAddress: String = "",
        notes: String = ""
    ): OrderModel {
        val orderId = UUID.randomUUID().toString()
        val items = convertFoodToOrderItems(cartItems)
        val pointsEarned = calculatePointsFromTotal(totalPrice)
        
        Log.d(TAG, "Creating order: $orderId with $pointsEarned points")
        
        return OrderModel(
            orderId = orderId,
            userId = userId,
            items = items,
            totalPrice = totalPrice,
            pointsEarned = pointsEarned,
            orderDate = System.currentTimeMillis(),
            status = "pending",
            deliveryAddress = deliveryAddress,
            notes = notes
        )
    }
    
    /**
     * Calcula el descuento en dinero basado en puntos canjeados
     * 1 punto = $0.01
     */
    fun calculateDiscountFromPoints(points: Int): Double {
        val discount = points * 0.01
        Log.d(TAG, "Calculated discount: $points points = $$discount")
        return discount
    }
    
    /**
     * Verifica si el usuario es premium (tiene puntos acumulados)
     */
    fun isPremiumUser(totalPoints: Int): Boolean {
        return totalPoints > 0
    }
    
    /**
     * Obtiene el nivel de usuario basado en puntos totales
     */
    fun getUserLevel(totalPoints: Int): String {
        return when {
            totalPoints >= 1000 -> "Platino"
            totalPoints >= 500 -> "Oro"
            totalPoints >= 100 -> "Plata"
            totalPoints > 0 -> "Bronce"
            else -> "Regular"
        }
    }
    
    /**
     * Obtiene el porcentaje de cashback basado en el nivel
     */
    fun getCashbackPercentage(userLevel: String): Double {
        return when (userLevel) {
            "Platino" -> 0.15  // 15%
            "Oro" -> 0.12      // 12%
            "Plata" -> 0.11    // 11%
            "Bronce" -> 0.10   // 10%
            else -> 0.10       // 10% por defecto
        }
    }
    
    /**
     * Formatea los puntos para mostrar en la UI
     */
    fun formatPoints(points: Int): String {
        return "$points pts"
    }
    
    /**
     * Formatea el dinero equivalente a puntos
     */
    fun formatPointsAsMoney(points: Int): String {
        val money = calculateDiscountFromPoints(points)
        return String.format("$%.2f", money)
    }
}
