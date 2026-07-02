package com.tasneem.safwa.features.payment.presentation.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun AddCardDialog(
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var number by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    val isNumberValid = number.length == 16 && number.all { it.isDigit() }
    val isFirstNameValid = firstName.isNotBlank()
    val isLastNameValid = lastName.isNotBlank()
    val isMonthValid = month.toIntOrNull() in 1..12
    val isYearValid = year.length == 4 && (year.toIntOrNull() ?: 0) >= 2026
    val isCvvValid = cvv.length in 3..4 && cvv.all { it.isDigit() }
    val isFormValid = isNumberValid && isFirstNameValid && isLastNameValid && isMonthValid && isYearValid && isCvvValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.add_new_card), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it.take(16) },
                    label = { Text(stringResource(R.string.card_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading,
                    isError = showErrors && !isNumberValid,
                    supportingText = if (showErrors && !isNumberValid) { { Text("Must be 16 digits") } } else null
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text(stringResource(R.string.first_name)) },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        isError = showErrors && !isFirstNameValid,
                        supportingText = if (showErrors && !isFirstNameValid) { { Text(stringResource(R.string.required)) } } else null
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text(stringResource(R.string.last_name)) },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        isError = showErrors && !isLastNameValid,
                        supportingText = if (showErrors && !isLastNameValid) { { Text(stringResource(R.string.required)) } } else null
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = it.take(2) },
                        label = { Text(stringResource(R.string.month_mm)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading,
                        isError = showErrors && !isMonthValid,
                        supportingText = if (showErrors && !isMonthValid) { { Text("01-12") } } else null
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it.take(4) },
                        label = { Text(stringResource(R.string.year_yyyy)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoading,
                        isError = showErrors && !isYearValid,
                        supportingText = if (showErrors && !isYearValid) { { Text("Min 2026") } } else null
                    )
                }
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { cvv = it.take(4) },
                    label = { Text(stringResource(R.string.cvv)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading,
                    isError = showErrors && !isCvvValid,
                    supportingText = if (showErrors && !isCvvValid) { { Text("3 or 4 digits") } } else null
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!isLoading) {
                        if (isFormValid) {
                            onSave(number, firstName, lastName, month, year, cvv)
                        } else {
                            showErrors = true
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.save_card))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
