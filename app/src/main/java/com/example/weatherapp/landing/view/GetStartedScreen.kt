package com.example.weatherapp.landing.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.poppinsFontFamily

@Composable
fun GetStartedScreen(onClick: () -> Unit) {

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.onboarding),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(alignment = Alignment.Center)
                .padding(top = 74.dp, start = 9.dp, end = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(190.dp))
            Text(
                "Weather Path", color = Color.White, fontSize = 33.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 10.dp)

            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                "Follow the weather wherever",
                color = OffWhite,
                fontSize = 14.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 17.dp)



            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Stay ahead with real-time forecasts, interactive maps, and smart alerts—so you’re always prepared for what’s next.",
                color = Color.Gray,
                fontSize = 16.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center


            )
            Spacer(modifier = Modifier.height(38.dp))
            CustomButton(text = "Get Started") {
                onClick.invoke()
            }

        }


    }


}

@Composable
fun CustomButton(text:String,onClick:()->Unit){
    Button(
        onClick = {onClick.invoke()},
        modifier = Modifier.fillMaxWidth(0.8f),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.Black,

            )
    ) {
        Text(
            text,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = poppinsFontFamily
        )
    }

}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun GetStartedScreenPreview() {

    GetStartedScreen {}

}