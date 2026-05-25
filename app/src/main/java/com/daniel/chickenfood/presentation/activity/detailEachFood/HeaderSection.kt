package com.daniel.chickenfood.presentation.activity.detailEachFood

import android.R.attr.bottom
import android.R.attr.top
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import coil.compose.AsyncImage
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.FoodModel

@Composable
fun HeaderSection(
    item: FoodModel,
    quantity: Int,
    onBackClick: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {

        AsyncImage(
            model = item.imagePath,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 48.dp
                ),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {
            BackButton(
                onClick = onBackClick
            )
            FavoriteButton()
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = item.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(
                    R.color.darkPurple
                )
            )
            RowDetail(
                time = item.timeValue,
                rating = item.star,
                calories = 250,
                modifier = Modifier
            )

            NumberRow(
                price = item.price.toDouble(),
                quantity = quantity,
                onIncrement = onIncrement,
                onDecrement = onDecrement,
                modifier = Modifier.padding(
                    top = 20.dp
                )
            )
        }
    }
}
@Composable
fun BackButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(
                R.drawable.back
            ),
            contentDescription = "Volver",
            tint = Color.White
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