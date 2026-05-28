package com.daniel.chickenfood.presentation.activity.dashboard

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.BannerModel
import kotlinx.coroutines.delay

@Composable
fun Banner(
    banners: List<BannerModel>,
    isLoading: Boolean,
    height: Int = 260
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = colorResource(R.color.orange)
            )
        }
    } else {
        BannerCarousel(
            banners = banners,
            height = height
        )
    }
}

@Composable
fun BannerCarousel(
    banners: List<BannerModel>,
    height: Int = 200
) {
    val pagerState = rememberPagerState(
        pageCount = { banners.size }
    )
    
    // Auto Slide
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            if (banners.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .align(Alignment.CenterHorizontally)
                    .height(height.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colorResource(R.color.grey))
            ) {
                val imageRequest = ImageRequest.Builder(LocalContext.current)
                    .data(banners[page].image)
                    .crossfade(true)
                    .listener(
                        onStart = { request ->
                            Log.d("Coil_Debug", "Comenzando a cargar: ${request.data}")
                        },
                        onSuccess = { request, result ->
                            Log.d("Coil_Debug", "¡Imagen cargada con éxito!")
                        },
                        onError = { request, result ->
                            // AQUÍ CAPTURAS EL ERROR
                            val errorException = result.throwable
                            Log.e("Coil_Debug", "Error al cargar la imagen: ${errorException.localizedMessage}")
                            errorException.printStackTrace() // Imprime todo el rastro del error en consola
                        }
                    )
                    .build()

                SubcomposeAsyncImage(
                    model = imageRequest,
                    contentDescription = "Banner ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = {
                        Box {
                            Image(
                                painter = painterResource(R.drawable.error),
                                contentDescription = ""
                            )
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Indicadores
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            if (pagerState.currentPage == index) 10.dp else 8.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                colorResource(R.color.orange)
                            else
                                Color.Gray
                        )
                )
            }
        }
    }
}


