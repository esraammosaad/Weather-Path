package com.example.weatherapp.utilis.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.utilis.isMorning

@Composable
fun ImageDisplay() {
    Image(
        painter = painterResource(
            if (isMorning()) R.drawable.sun
            else R.drawable.moon
        ),
        contentDescription = stringResource(R.string.sun_or_moon_icon),
        modifier = Modifier
            .size(150.dp)
            .padding(top = 36.dp, end = 16.dp)
    )
}