package com.daniel.chickenfood.ui.activity.splash

import android.R.attr.logo
import android.R.attr.onClick
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.MainActivity
import com.daniel.chickenfood.R
import com.daniel.chickenfood.ui.activity.BaseActivity
import com.google.common.io.Files.append
import kotlin.jvm.java

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen(
                onGetStartedClick = {
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                }
            )
        }
    }
}
@Composable
fun SplashScreen(
    onGetStartedClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.darkBrown))
    )
    {
        Image(
            painter = painterResource(R.drawable.intro_pic),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.TopCenter)
        )
        Image(
            painter = painterResource(R.drawable.pollo),
            contentDescription = "logo",
            modifier = Modifier
                .size(380.dp)
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 55.dp)
        ){
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
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.subSubtitle),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 24.sp
            )

            GetStartedButtons(onSignUpClick = {},
                onGetStartedClick = {},
                modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SplashScreenPreview(){
    SplashScreen(
        onGetStartedClick = {}
    )}