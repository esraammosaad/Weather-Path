package com.example.weatherapp.home_settings_feature.settings.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.RadioButtonSingleSelection

@Composable
fun CustomPreferencesCard(
    text: String,
    radioButtonList: List<String>,
    icon: Int,
    backgroundColor: String,
    defaultSelectedItem: String,
    onOptionClicked: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(
                brush = getWeatherGradient(backgroundColor),
                shape = RoundedCornerShape(25.dp)
            )
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(),
        colors = CardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White
        )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(120.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = "Icon"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = text,
                    style = Styles.textStyleSemiBold20,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.65f)
                )

            }
            Spacer(modifier = Modifier.height(18.dp))
            RadioButtonSingleSelection(onOptionClicked = { selectedOption ->
                onOptionClicked.invoke(selectedOption)
            }, radioButtonList, defaultSelectedItem)

        }
    }
}