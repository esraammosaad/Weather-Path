package com.example.weatherapp.utilis.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R

@Composable
fun MoreDetailsItem(icon: Int, textOne: String, textTwo: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(icon), contentDescription = stringResource(R.string.wind_icon))
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            CustomText(textOne)
            CustomText(textTwo)
        }

    }
}