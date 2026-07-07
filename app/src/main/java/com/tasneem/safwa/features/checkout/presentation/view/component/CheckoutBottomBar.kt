package com.tasneem.safwa.features.checkout.presentation.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R

@Composable
fun CheckoutBottomBar(
    totalAmount: String,
    onPlaceOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Button(
            onClick = onPlaceOrderClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = stringResource(id = R.string.checkout_place_order_format, totalAmount),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = MaterialTheme.typography.titleMedium.fontFamily
                )
            )
        }
    }
}