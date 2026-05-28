package com.daniel.chickenfood.presentation.activity.detailEachFood

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.FoodModel
@Composable
fun HeaderSection(
    item: FoodModel,
    quantity: Int,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Usamos una Column externa para garantizar un flujo vertical responsivo y libre
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // 1. ZONA DE LA IMAGEN Y BOTONES FLOTANTES
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Altura fija y segura para la imagen en cualquier pantalla
        ) {
            AsyncImage(
                model = item.imagePath,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 32.dp,
                            bottomEnd = 32.dp
                        )
                    )
            )

            // Fila de botones de navegación (Aprovecha el statusBarsPadding para no pisar la hora)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = onBackClick)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HomeButton(onClick = onHomeClick)
                    FavoriteButton()
                }
            }
        }

        // 2. CONTENEDOR DE DETALLES (Se posiciona abajo y "muerde" la imagen levemente)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-10).dp), // Efecto de superposición sutil y controlado sobre la imagen
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White // Fondo sólido para que los textos nunca se mezclen con la imagen
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.darkPurple),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                RowDetail(
                    time = item.timeValue,
                    rating = item.star,
                    calories = 250,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(16.dp))

                NumberRow(
                    price = item.price.toDouble(),
                    quantity = quantity,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
@Composable
fun BackButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(
                R.drawable.back_grey
            ),
            contentDescription = "Volver",
            tint = Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun HomeButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = {
            Log.d("HeaderSection", "Home button clicked")
            onClick()
        },
        modifier = Modifier
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(
                R.drawable.btn_1
            ),
            contentDescription = "Home",
            tint = Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun FavoriteButton(
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(
                R.drawable.fav_icon
            ),
            contentDescription = "Favorito",
            tint = Color.Red
        )
    }
}