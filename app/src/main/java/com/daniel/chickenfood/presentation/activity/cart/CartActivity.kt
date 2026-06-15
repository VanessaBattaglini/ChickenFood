package com.daniel.chickenfood.presentation.activity.cart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.domain.model.OrderItemModel
import com.daniel.chickenfood.helper.ChangeNumberItemsListener
import com.daniel.chickenfood.helper.ManagmentCart
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.checkout.CheckoutActivity
import com.daniel.chickenfood.presentation.activity.dashboard.MainActivity

private const val TAG = "CartActivity"

class CartActivity : BaseActivity() {

    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        managmentCart = ManagmentCart(applicationContext)
        
        Log.d(TAG, "CartActivity opened")
        val cartItems = managmentCart.getListCart()
        Log.d(TAG, "Cart items loaded: ${cartItems.size} items")
        for (item in cartItems) {
            Log.d(TAG, "  - ${item.title} x${item.numberInCart} = $${item.price * item.numberInCart}")
        }

        setContent {
            CartScreen(
                managmentCart = managmentCart,
                onBackClick = { finish() },
                onHomeClick = { navigateToHome() },
                onContinueShoppingClick = { navigateToHome() },
                onCheckoutClick = { navigateToCheckout() }
            )
        }
    }

    private fun navigateToHome() {
        Log.d(TAG, "Navigating to MainActivity")
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToCheckout() {
        Log.d(TAG, "Navigating to CheckoutActivity")
        val cartItems = managmentCart.getListCart()
        val cartTotal = managmentCart.getTotalFee()
        
        Log.d(TAG, "Cart items: ${cartItems.size}, total: $cartTotal")
        
        // Crear OrderItemModels desde los FoodModels del carrito
        val orderItems = cartItems.map { food ->
            OrderItemModel(
                foodId = food.id,
                title = food.title,
                price = food.price.toDouble(),
                quantity = food.numberInCart,
                subtotal = (food.price * food.numberInCart).toDouble(),
                imagePath = food.imagePath
            )
        }
        
        val intent = Intent(this, CheckoutActivity::class.java).apply {
            // Pasar datos como Parceable o reconvertir en CheckoutActivity
            putExtra("itemCount", orderItems.size)
            putExtra("cartTotal", cartTotal)
            putExtra("userPoints", 500) // TODO: Obtener del RewardsViewModel
            
            // Pasar cada item como strings serializados
            orderItems.forEachIndexed { index, item ->
                putExtra("item_$index", "${item.title}|${item.price}|${item.quantity}|${item.subtotal}|${item.foodId}")
            }
        }
        startActivity(intent)
    }
}

@Composable
fun CartScreen(
    managmentCart: ManagmentCart,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onContinueShoppingClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    var cartItems by remember { mutableStateOf(managmentCart.getListCart()) }
    var totalPrice by remember { mutableStateOf(managmentCart.getTotalFee()) }
    
    Log.d(TAG, "CartScreen composing with ${cartItems.size} items, total: $totalPrice")

    val changeListener = object : ChangeNumberItemsListener {
        override fun onChanged() {
            Log.d(TAG, "changeListener.onChanged() called")
            cartItems = managmentCart.getListCart()
            totalPrice = managmentCart.getTotalFee()
            Log.d(TAG, "Updated cart: ${cartItems.size} items, total: $totalPrice")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.darkBrown))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                Log.d(TAG, "Home button clicked in CartScreen")
                onHomeClick()
            }) {
                Icon(
                    painter = painterResource(R.drawable.back_grey),
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Mi Carrito",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        if (cartItems.isEmpty()) {
            // Empty Cart
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tu carrito está vacío",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Button(
                    onClick = {
                        Log.d(TAG, "Continue shopping button clicked from empty cart")
                        onContinueShoppingClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.orange)
                    )
                ) {
                    Text(
                        text = "Continuar Comprando",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Cart Items
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(cartItems) { index, item ->
                    CartItemCard(
                        item = item,
                        index = index,
                        managmentCart = managmentCart,
                        changeListener = changeListener
                    )
                }
            }

            // Footer with Total
            CartFooter(
                totalPrice = totalPrice,
                itemCount = cartItems.size,
                managmentCart = managmentCart,
                onContinueShoppingClick = onContinueShoppingClick,
                onCheckoutClick = onCheckoutClick
            )
        }
    }
}

@Composable
fun CartItemCard(
    item: FoodModel,
    index: Int,
    managmentCart: ManagmentCart,
    changeListener: ChangeNumberItemsListener
) {
    Log.d(TAG, "CartItemCard rendering: ${item.title} at index $index, quantity: ${item.numberInCart}")

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image
            AsyncImage(
                model = item.imagePath,
                contentDescription = item.title,
                modifier = Modifier
                    .size(80.dp)
            )

            // Item Details
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "$$${item.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.orange),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Cantidad: ${item.numberInCart}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Subtotal: $$${item.price * item.numberInCart}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Delete Button - Elimina el item COMPLETAMENTE
            Button(
                onClick = {
                    Log.d(TAG, "Delete button clicked for: ${item.title} at index $index")
                    val currentList = managmentCart.getListCart()
                    // Eliminar el item completamente del carrito
                    if (index >= 0 && index < currentList.size) {
                        managmentCart.removeItem(currentList, index, changeListener)
                        Log.d(TAG, "Item deleted completely, remaining items: ${currentList.size - 1}")
                    }
                },
                modifier = Modifier.size(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("✕", fontSize = 20.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun CartFooter(
    totalPrice: Double,
    itemCount: Int,
    managmentCart: ManagmentCart,
    onContinueShoppingClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Subtotal ($itemCount items):",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$$${"%,.2f".format(totalPrice)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Envío:",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$0.00",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total:",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$$${"%,.2f".format(totalPrice)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.orange)
            )
        }

        // Botones
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { 
                    Log.d(TAG, "Proceder al Pago clicked")
                    onCheckoutClick()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.orange)
                )
            ) {
                Text(
                    text = "Proceder al Pago",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                onClick = {
                    Log.d(TAG, "Continue shopping button clicked")
                    onContinueShoppingClick()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray
                )
            ) {
                Text(
                    text = "Continuar Comprando",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
