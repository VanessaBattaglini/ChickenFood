package com.daniel.chickenfood.helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.daniel.chickenfood.domain.model.FoodModel
import kotlin.collections.indexOfFirst

private const val TAG = "ManagmentCart"

class ManagmentCart(val context: Context) {

    private val tinyDB = MyDB(context)

    fun insertItem(item: FoodModel) {
        try {
            Log.d(TAG, "insertItem called for: ${item.title} (id=${item.id}), quantity: ${item.numberInCart}")
            
            var listFood = getListCart()
            Log.d(TAG, "Current cart size: ${listFood.size}")
            
            // ✅ Usar ID en lugar de title para identificar productos
            val existAlready = listFood.any { it.id == item.id }
            val index = listFood.indexOfFirst { it.id == item.id }

            if (existAlready) {
                // ✅ REEMPLAZAR cantidad en lugar de sumar
                Log.d(TAG, "Item already exists at index $index, REPLACING quantity from ${listFood[index].numberInCart} to ${item.numberInCart}")
                listFood[index].numberInCart = item.numberInCart
            } else {
                // Si no existe, agregar el item
                Log.d(TAG, "Adding new item to cart")
                listFood.add(item)
            }
            
            tinyDB.putListObject("CartList", listFood)
            Log.d(TAG, "Item saved to cart, new size: ${listFood.size}")
            
            // Toast con nombre del producto y cantidad
            val message = "${item.title} x${item.numberInCart} agregado al carrito"
            Log.d(TAG, "Showing toast: $message")
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in insertItem: ${e.message}", e)
            Toast.makeText(context, "Error al agregar al carrito: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun getListCart(): ArrayList<FoodModel> {
        try {
            val list = tinyDB.getListObject("CartList")
            Log.d(TAG, "getListCart returned ${list.size} items")
            return list
        } catch (e: Exception) {
            Log.e(TAG, "Error in getListCart: ${e.message}", e)
            return ArrayList()
        }
    }

    fun removeItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
        try {
            if (position < 0 || position >= listFood.size) {
                Log.w(TAG, "Invalid position: $position, list size: ${listFood.size}")
                return
            }
            val itemName = listFood[position].title
            listFood.removeAt(position)
            tinyDB.putListObject("CartList", listFood)
            Log.d(TAG, "Item removed: $itemName, new cart size: ${listFood.size}")
            Toast.makeText(context, "$itemName eliminado del carrito", Toast.LENGTH_LONG).show()
            listener.onChanged()
        } catch (e: Exception) {
            Log.e(TAG, "Error in removeItem: ${e.message}", e)
            Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun minusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
        try {
            if (position < 0 || position >= listFood.size) {
                Log.w(TAG, "Invalid position in minusItem: $position")
                return
            }
            val currentCount = listFood[position].numberInCart
            if (currentCount <= 1) {
                listFood.removeAt(position)
                Log.d(TAG, "Item removed (count was 1)")
            } else {
                listFood[position].numberInCart = currentCount - 1
                Log.d(TAG, "Item quantity decreased to ${currentCount - 1}")
            }
            tinyDB.putListObject("CartList", listFood)
            listener.onChanged()
        } catch (e: Exception) {
            Log.e(TAG, "Error in minusItem: ${e.message}", e)
        }
    }

    fun plusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
        try {
            if (position < 0 || position >= listFood.size) {
                Log.w(TAG, "Invalid position in plusItem: $position")
                return
            }
            listFood[position].numberInCart++
            Log.d(TAG, "Item quantity increased to ${listFood[position].numberInCart}")
            tinyDB.putListObject("CartList", listFood)
            listener.onChanged()
        } catch (e: Exception) {
            Log.e(TAG, "Error in plusItem: ${e.message}", e)
        }
    }

    fun getTotalFee(): Double {
        try {
            val listFood = getListCart()
            var fee = 0.0
            for (item in listFood) {
                fee += item.price * item.numberInCart
            }
            Log.d(TAG, "Total fee calculated: $fee")
            return fee
        } catch (e: Exception) {
            Log.e(TAG, "Error in getTotalFee: ${e.message}", e)
            return 0.0
        }
    }

    fun clearCart() {
        try {
            val emptyList: ArrayList<FoodModel> = ArrayList()
            tinyDB.putListObject("CartList", emptyList)
            Log.d(TAG, "Cart cleared completely")
            Toast.makeText(context, "Carrito limpiado", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error in clearCart: ${e.message}", e)
        }
    }
}