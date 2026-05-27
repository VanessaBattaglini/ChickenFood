package com.daniel.chickenfood.presentation.activity.detailEachFood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.helper.ManagmentCart
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.cart.CartActivity
import com.daniel.chickenfood.presentation.activity.dashboard.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "DetailEachFoodActivity"

class DetailEachFoodActivity : BaseActivity() {

    private lateinit var item: FoodModel
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        item = intent.getSerializableExtra("object") as FoodModel
        managmentCart = ManagmentCart(applicationContext)

        // ✅ SIEMPRE resetear numberInCart a 0 para que DetailScreen lo inicie en 1
        item.numberInCart = 0

        Log.d(TAG, "DetailEachFoodActivity opened with item: ${item.title} (id=${item.id})")

        setContent {
            MaterialTheme {
                DetailScreen(
                    item = item,
                    onBackClick = { finish() },
                    onHomeClick = { navigateToHome() },
                    onAddToCartClick = { quantity ->
                        Log.d(TAG, "onAddToCartClick triggered for: ${item.title}, quantity: $quantity")
                        try {
                            // Actualizar la cantidad del item con la cantidad seleccionada
                            item.numberInCart = quantity
                            Log.d(TAG, "Updated item quantity to: ${item.numberInCart}")
                            
                            managmentCart.insertItem(item)
                            Log.d(TAG, "insertItem completed successfully")
                            
                            // Usar corrutina en lugar de Thread.sleep para no bloquear UI
                            lifecycleScope.launch {
                                delay(500)  // Esperar a que se muestre el Toast
                                navigateToCart()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error in onAddToCartClick: ${e.message}", e)
                            Toast.makeText(this@DetailEachFoodActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }

    private fun navigateToCart() {
        Log.d(TAG, "Navigating to CartActivity")
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        Log.d(TAG, "Navigating to MainActivity")
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }
}