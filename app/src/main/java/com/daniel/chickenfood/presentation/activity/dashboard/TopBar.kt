package com.daniel.chickenfood.presentation.activity.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daniel.chickenfood.R

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    showLogout: Boolean = false,
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = onMenuClick
        ) {

            Icon(
                painter = painterResource(R.drawable.settings_icon),
                contentDescription = "Settings",
                tint = Color.Unspecified
            )
        }
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = buildAnnotatedString {

                        withStyle(
                            style = SpanStyle(
                                color = colorResource(R.color.orange)
                            )
                        ) {
                            append("CHICKEN")
                        }

                        withStyle(
                            style = SpanStyle(
                                color = Color.White
                            )
                        ) {
                            append("FOOD")
                        }
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Tienda Online",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        Row {
            IconButton(
                onClick = onNotificationClick
            ) {

                Icon(
                    painter = painterResource(R.drawable.bell_icon),
                    contentDescription = "Notifications",
                    tint = Color.Unspecified
                )
            }
            // Solo mostrar botón Logout si está autenticado
            if (showLogout) {
                IconButton(
                    onClick = onLogoutClick
                ) {

                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1B1B1B
)
@Composable
fun TopBarPreview() {
    TopBar()
}