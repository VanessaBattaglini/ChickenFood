package com.daniel.chickenfood.presentation.activity.dashboard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.UserRewardsModel
import com.daniel.chickenfood.helper.RewardsHelper

private const val TAG = "PointsCard"

/**
 * Componente que muestra el saldo de puntos del usuario
 * 
 * Características:
 * - Muestra saldo de puntos actual
 * - Muestra nivel del usuario (Regular, Bronce, Plata, Oro, Platino)
 * - Muestra progreso hacia el siguiente nivel
 * - Diseño responsive y atractivo
 */
@Composable
fun PointsCard(
    userRewards: UserRewardsModel?,
    modifier: Modifier = Modifier
) {
    if (userRewards == null) {
        Log.d(TAG, "userRewards is null, showing placeholder")
        PointsCardPlaceholder(modifier = modifier)
        return
    }

    Log.d(TAG, "Rendering PointsCard with rewards: $userRewards")

    val currentLevel = RewardsHelper.getUserLevel(userRewards.totalPoints)
    val levelPercentage = RewardsHelper.getLevelProgress(userRewards.totalPoints)
    val pointsToNextLevel = RewardsHelper.getPointsToNextLevel(userRewards.totalPoints)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.orange)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Encabezado con saldo y emoji
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Mis Puntos",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${userRewards.pointsBalance} puntos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Emoji del nivel
                Text(
                    text = when (currentLevel) {
                        "regular" -> "👤"
                        "bronce" -> "🥉"
                        "plata" -> "🥈"
                        "oro" -> "🏆"
                        "platino" -> "👑"
                        else -> "✨"
                    },
                    fontSize = 40.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Nivel y progreso
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nivel actual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nivel: ${getNivelDisplay(currentLevel)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "(${userRewards.totalPoints} totales)",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Barra de progreso
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(levelPercentage.coerceIn(0f, 1f))
                            .height(6.dp)
                            .background(Color.White, RoundedCornerShape(3.dp))
                    )
                }

                // Texto de progreso
                Text(
                    text = if (currentLevel == "platino") {
                        "🎉 ¡Nivel Máximo Alcanzado!"
                    } else {
                        "Falta $pointsToNextLevel puntos para el siguiente nivel"
                    },
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
            }

            // Tabla de información adicional
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InformationItem(
                    label = "Disponibles",
                    value = "${userRewards.pointsBalance}",
                    textColor = Color.White
                )
                InformationItem(
                    label = "Gastados",
                    value = "${userRewards.pointsSpent}",
                    textColor = Color.White
                )
                InformationItem(
                    label = "Equivalencia",
                    value = "$${userRewards.pointsBalance * 0.01}",
                    textColor = Color.White
                )
            }
        }
    }
}

/**
 * Placeholder mientras se cargan los puntos
 */
@Composable
fun PointsCardPlaceholder(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.orange)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 3.dp
                )
                Text(
                    text = "Cargando puntos...",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

/**
 * Componente auxiliar para mostrar información
 */
@Composable
fun InformationItem(
    label: String,
    value: String,
    textColor: Color = Color.Black
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = textColor.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Obtiene el nombre del nivel en español
 */
private fun getNivelDisplay(level: String): String {
    return when (level) {
        "regular" -> "Regular 👤"
        "bronce" -> "Bronce 🥉"
        "plata" -> "Plata 🥈"
        "oro" -> "Oro 🏆"
        "platino" -> "Platino 👑"
        else -> "Desconocido"
    }
}
