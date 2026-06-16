package com.daniel.chickenfood.presentation.activity.checkout

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.daniel.chickenfood.domain.model.OrderItemModel
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.dashboard.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "OrderDetailActivity"

class OrderDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Recibir datos desde CheckoutActivity
        @Suppress("DEPRECATION")
        val cartItems = intent.getParcelableArrayListExtra<OrderItemModel>("cartItems") ?: mutableListOf()
        val cartTotal = intent.getDoubleExtra("cartTotal", 0.0)
        val paymentMethod = intent.getStringExtra("paymentMethod") ?: "card"
        val pointsBefore = intent.getIntExtra("pointsBefore", 0)
        val pointsChange = intent.getIntExtra("pointsChange", 0)
        val orderId = intent.getStringExtra("orderId") ?: ""
        val orderTimestamp = intent.getLongExtra("orderTimestamp", System.currentTimeMillis())
        val discountUsed = intent.getDoubleExtra("discountUsed", 0.0)
        val pointsUsedForDiscount = intent.getIntExtra("pointsUsedForDiscount", 0)

        Log.d(TAG, "OrderDetailActivity opened - orderId=$orderId, items=${cartItems.size}, total=$cartTotal")

        setContent {
            OrderDetailScreen(
                orderId = orderId,
                cartItems = cartItems,
                cartTotal = cartTotal,
                paymentMethod = paymentMethod,
                pointsBefore = pointsBefore,
                pointsChange = pointsChange,
                orderTimestamp = orderTimestamp,
                discountUsed = discountUsed,
                pointsUsedForDiscount = pointsUsedForDiscount,
                onBackClick = { finish() },
                onShareClick = {
                    Log.d(TAG, "Share clicked")
                    // TODO: Implementar compartir
                }
            )
        }
    }
}

@Composable
fun OrderDetailScreen(
    orderId: String = "",
    cartItems: List<OrderItemModel> = emptyList(),
    cartTotal: Double = 0.0,
    paymentMethod: String = "card",
    pointsBefore: Int = 0,
    pointsChange: Int = 0,
    orderTimestamp: Long = System.currentTimeMillis(),
    discountUsed: Double = 0.0,
    pointsUsedForDiscount: Int = 0,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    val formatter = SimpleDateFormat("dd MMM, yyyy HH:mm", Locale("es", "ES"))
    val dateString = formatter.format(Date(orderTimestamp))

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.back_grey),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Detalle de Orden",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onShareClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Share",
                    tint = colorResource(R.color.orange),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Orden Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Orden #$orderId",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "✅ Completada",
                                color = Color.Green,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "📅 $dateString",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Items Ordenados
            item {
                Text(
                    text = "ITEMS ORDENADOS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(cartItems) { item ->
                OrderItemDetail(item = item)
            }

            // Resumen
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "RESUMEN",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        ResumenRow("Subtotal:", "$${"%.2f".format(cartTotal + discountUsed)}")
                        ResumenRow("Envío:", "$0.00")

                        if (discountUsed > 0) {
                            ResumenRow(
                                "Descuento:",
                                "-$${"%.2f".format(discountUsed)}",
                                color = Color.Green
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TOTAL:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${"%.2f".format(cartTotal)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.orange)
                            )
                        }
                    }
                }
            }

            // Método de Pago
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "MÉTODO DE PAGO",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (paymentMethod == "card") {
                            Text(
                                text = "💳 Tarjeta de Crédito/Débito",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = "💎 Puntos",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Puntos
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💎 PUNTOS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        val pointsAfter = pointsBefore + pointsChange - pointsUsedForDiscount

                        ResumenRow("Saldo anterior:", "$pointsBefore pts")

                        if (pointsChange > 0) {
                            ResumenRow(
                                "Puntos ganados:",
                                "+$pointsChange pts",
                                color = Color.Green
                            )
                        }

                        if (pointsUsedForDiscount > 0) {
                            ResumenRow(
                                "Puntos descuento:",
                                "-$pointsUsedForDiscount pts",
                                color = Color(0xFFFF6B6B)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        ResumenRow("Saldo actual:", "$pointsAfter pts")
                    }
                }
            }

            item {
                Box(modifier = Modifier.height(24.dp))
            }
        }

        // Footer Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.orange)
                )
            ) {
                Text(
                    text = "Volver al Inicio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OrderItemDetail(item: OrderItemModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = item.imagePath,
                contentDescription = item.title,
                modifier = Modifier.size(60.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "Cantidad: ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$${"%.2f".format(item.price)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.orange)
                )
                Text(
                    text = "Subtotal: $${"%.2f".format(item.subtotal)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ResumenRow(label: String, value: String, color: Color = Color.Black) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
