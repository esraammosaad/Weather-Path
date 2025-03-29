package com.example.weatherapp.utilis.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.utilis.Strings

@Composable
fun WeatherStatusImageDisplay(icon: String) {
    Image(
        painter = rememberAsyncImagePainter(
            "${Strings.BASE_IMAGE_URL}${
                icon
            }.png"
        ),
        contentDescription = stringResource(R.string.sun_icon),
        modifier = Modifier
            .height(30.dp)
            .width(30.dp)
            .padding(top = 3.dp),
        contentScale = ContentScale.Crop
    )
}