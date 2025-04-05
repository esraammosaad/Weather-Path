package com.example.weatherapp.favorite_alarm_features.favorite.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.ui.theme.PrimaryColor

@Composable
fun CustomTextField(
    textFieldValue: MutableState<String>,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl
) {
    OutlinedTextField(
        value = textFieldValue.value,
        onValueChange = {
            textFieldValue.value = it
            favoriteViewModel.onSearchTextChange(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = "", tint = Color.White)
        },

        placeholder = {
            Text(stringResource(R.string.search), color = Color.White)
        },
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = PrimaryColor,
            focusedBorderColor = PrimaryColor,
            unfocusedBorderColor = PrimaryColor,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(15.dp),
    )
}