package com.example.weatherapp.favorite.view.components

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.example.weatherapp.R
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.favorite.view.screens.deleteAlarm
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.utilis.Strings
import kotlinx.coroutines.CoroutineScope

@Composable
fun CustomSwitch(
    selectedAlarm: AlarmModel?,
    context: Context,
    isDialog: MutableState<Boolean>,
    dialogTitle: MutableState<String>,
    dialogText: MutableState<String>,
    onConfirmation: MutableState<() -> Unit>,
    showDatePicker: MutableState<Boolean>,
    datePickerTitle: MutableState<String>,
    selectedWeather: CurrentWeatherResponse?,
    favoriteViewModel: FavoriteViewModelImpl,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState
) {
    Switch(
        checked = selectedAlarm != null,
        onCheckedChange = {
            onSwitchCheckChange(
                context = context,
                isDialog = isDialog,
                dialogTitle = dialogTitle,
                dialogText = dialogText,
                onConfirmation = onConfirmation,
                switchChecked = it,
                showDatePicker = showDatePicker,
                datePickerTitle = datePickerTitle,
                selectedWeather = selectedWeather,
                selectedAlarm = selectedAlarm,
                favoriteViewModel = favoriteViewModel,
                coroutineScope = coroutineScope,
                snackBarHostState = snackBarHostState
            )
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = PrimaryColor,
            checkedTrackColor = OffWhite,
            uncheckedThumbColor = PrimaryColor,
            uncheckedTrackColor = OffWhite,
        )
    )
}

private fun onSwitchCheckChange(
    context: Context,
    isDialog: MutableState<Boolean>,
    dialogTitle: MutableState<String>,
    dialogText: MutableState<String>,
    onConfirmation: MutableState<() -> Unit>,
    switchChecked: Boolean,
    showDatePicker: MutableState<Boolean>,
    datePickerTitle: MutableState<String>,
    selectedWeather: CurrentWeatherResponse?,
    selectedAlarm: AlarmModel?,
    favoriteViewModel: FavoriteViewModelImpl,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState
) {
    if (!Settings.canDrawOverlays(context)) {
        isDialog.value = true
        dialogTitle.value =
            context.resources.getString(R.string.warning)
        dialogText.value =
            context.getString(R.string.you_need_to_allow_draw_over_other_apps_permission)
        onConfirmation.value = {
            val permissionIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse(Strings.OPEN_OVER_APP_URI)
            ).addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(permissionIntent)
        }
    } else {
        if (switchChecked) {
            showDatePicker.value = true
            datePickerTitle.value =
                selectedWeather?.cityName ?: ""
        } else {
            isDialog.value = true
            dialogTitle.value =
                context.resources.getString(R.string.warning)
            dialogText.value =
                context.getString(R.string.you_sure_you_want_to_reset_the_alarm)
            onConfirmation.value = {
                deleteAlarm(
                    selectedAlarm = selectedAlarm,
                    favoriteViewModel = favoriteViewModel,
                    context = context,
                    coroutineScope = coroutineScope,
                    snackBarHostState = snackBarHostState
                )
                isDialog.value = false
            }
        }
    }
}