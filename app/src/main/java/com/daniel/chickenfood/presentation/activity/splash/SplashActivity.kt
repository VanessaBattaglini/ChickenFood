package com.daniel.chickenfood.presentation.activity.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.presentation.activity.dashboard.MainActivity
import com.daniel.chickenfood.R
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.auth.SignUpActivity
import com.daniel.chickenfood.helper.AuthHelper

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.TRANSPARENT,
            )
        )
        setContent {
            SplashScreen(
                onGetStartedClick = {
                    navigateToDashboard()
                },
                onSignUpClick = {
                    navigateToSignUp()
                }
            )
        }
    }

    private fun navigateToDashboard() {
        Log.d("SplashActivity", "Navegando al Dashboard")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSignUp() {
        Log.d("SplashActivity", "Navegando a SignUpActivity")
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun SplashScreen(
    onGetStartedClick: () -> Unit,
    onSignUpClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.darkBrown))
    ) {
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                Image(
                    painter = painterResource(R.drawable.intro_pic),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )
                Image(
                    painter = painterResource(R.drawable.pollo),
                    contentDescription = "splash",
                    modifier = Modifier
                        .size(380.dp)
                        .padding(top = 40.dp)

                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 44.dp, vertical = 20.dp)
                    .safeDrawingPadding()
            ) {
                val styledText = buildAnnotatedString {

                    append("El sabor que convierte ")

                    withStyle(
                        style = SpanStyle(
                            color = colorResource(R.color.orange)
                        )
                    ) {
                        append("cada comida en")
                    }
                    append("\nun crujiente placer")
                }
                Text(
                    text = styledText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 40.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.subSubtitle),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.playwrite_ar_guides_regular)),
                    color = Color.Yellow.copy(alpha = 3.8f),
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                GetStartedButtons(
                    onSignUpClick = onSignUpClick,
                    onGetStartedClick = onGetStartedClick,
                    modifier = Modifier.padding(top = 30.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SplashScreenPreview() {
    SplashScreen(
        onGetStartedClick = {}
    )
}