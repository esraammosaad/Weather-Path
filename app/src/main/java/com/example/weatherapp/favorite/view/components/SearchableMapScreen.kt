package com.example.weatherapp.favorite.view.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.utilis.Strings
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch

@Composable
fun SearchableMapScreen(
    cameraPositionState: CameraPositionState,
    markerState: MarkerState,
    showBottomSheet: MutableState<Boolean>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, Strings.GOOGLE_API_KEY)
        }
    }
    val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val place = result.data?.let { Autocomplete.getPlaceFromIntent(it) }
                val latLng = place?.latLng
                scope.launch {
                    latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 12f) }
                        ?.let { cameraPositionState.animate(it, 2000) }
                    if (latLng != null) {
                        markerState.position = latLng
                        showBottomSheet.value = true
                    }
                }

            }
        }
    Box(modifier = Modifier.fillMaxWidth()) {
        FloatingActionButton(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(vertical = 42.dp, horizontal = 12.dp),
            onClick = { launcher.launch(intent) },
            containerColor = PrimaryColor,
            contentColor = Color.White,
            shape = RoundedCornerShape(100.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search)
            )
        }
    }
}