package com.daniel.chickenfood.presentation.activity.dashboard

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniel.chickenfood.R

@Composable
fun BottomBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val bottomMenuItems = bottomMenuItems()

    NavigationBar(
        containerColor = colorResource(R.color.darkGray),
        tonalElevation = 8.dp
    ) {
        bottomMenuItems.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item.label,
                onClick = {
                    onItemSelected(item.label)
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                        tint =
                            if (selectedItem == item.label)
                                colorResource(R.color.orange)
                            else
                                Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}

// ------------------------------------
// DATA CLASS
// ------------------------------------

data class BottomMenuItem(
    val label: String,
    val icon: Int
)

// ------------------------------------
// LISTA DE ITEMS
// ------------------------------------

fun bottomMenuItems(): List<BottomMenuItem> {
    return listOf(
        BottomMenuItem(
            label = "Home",
            icon = R.drawable.btn_1
        ),
        BottomMenuItem(
            label = "Cart",
            icon = R.drawable.btn_2
        ),
        BottomMenuItem(
            label = "Favorite",
            icon = R.drawable.btn_3
        ),
        BottomMenuItem(
            label = "Orders",
            icon = R.drawable.btn_4
        ),
        BottomMenuItem(
            label = "Profile",
            icon = R.drawable.btn_5
        )
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1B1B1B
)
@Composable
fun MyBottomBarPreview() {
    var selectedItem by rememberSaveable {
        mutableStateOf("Home")
    }
    BottomBar(
        selectedItem = selectedItem,
        onItemSelected = {
            selectedItem = it
        }
    )
}