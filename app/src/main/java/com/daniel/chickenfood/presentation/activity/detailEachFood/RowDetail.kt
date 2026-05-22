package com.daniel.chickenfood.presentation.activity.detailEachFood

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.R

@Composable
fun RowDetail(
    time: Int,
    rating: Double,
    calories: Int,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        DetailItem(
            icon = R.drawable.time_color,
            value = "$time min"
        )

        DetailItem(
            icon = R.drawable.star,
            value = rating.toString()
        )

        DetailItem(
            icon = R.drawable.flame,
            value = "$calories kcal"
        )
    }
}
@Composable
private fun DetailItem(
    icon: Int,
    value: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(icon),
            contentDescription = null
        )

        Text(
            text = value,
            modifier = Modifier.padding(start = 6.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.darkPurple)
        )
    }
}