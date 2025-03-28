package com.example.weatherapp.utilis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.PrimaryColor

@Composable
fun ConfirmationDialog(
    onConfirmation: () -> Unit,
    onDismiss: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    showRadioButton: Boolean,
    onOptionClicked: (String) -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.baseline_location_pin_24),
                contentDescription = stringResource(R.string.confirmation_icon),
                tint = PrimaryColor
            )
        },
        title = {
            Text(text = dialogTitle, textAlign = TextAlign.Center)
        },
        text = {
            Column {
                Text(text = dialogText, textAlign = TextAlign.Center)
                if (showRadioButton) {
                    Spacer(modifier = Modifier.height(8.dp))
                    RadioButtonSingleSelection(onOptionClicked)
                }
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.confirm), color = PrimaryColor)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.dismiss), color = PrimaryColor)
            }
        }

    )
}