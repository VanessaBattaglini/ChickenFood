package com.daniel.chickenfood.presentation.activity.dashboard

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {}
) {

    var text by rememberSaveable {
        mutableStateOf("")
    }

    OutlinedTextField(

        value = text,

        onValueChange = {
            text = it
            onSearch(it)
        },

        placeholder = {

            Text(
                text = "¿Qué deseas comer?",
                fontFamily = FontFamily(Font(R.font.playwrite_ar_guides_regular)),
                color = colorResource(R.color.darkBrown),
                fontSize = 17.sp
            )
        },

        leadingIcon = {

            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "Search",
                tint = colorResource(R.color.orange)
            )
        },

        singleLine = true,

        shape = RoundedCornerShape(18.dp),

        colors = OutlinedTextFieldDefaults.colors(

            focusedContainerColor = colorResource(R.color.grey),
            unfocusedContainerColor = colorResource(R.color.grey),

            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,

            cursorColor = colorResource(R.color.orange),

            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),

        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(58.dp)
    )
}
@Preview(
    showBackground = true,
    backgroundColor = 0xFF1B1B1B
)
@Composable
fun SearchBarPreview(){
    SearchBar()
}