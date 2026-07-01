package com.tasneem.safwa.features.settings.orderhistory.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val backgroundColor = when (status) {
        OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.primaryContainer
        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primaryContainer
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when (status) {
        OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val textRes = when (status) {
        OrderStatus.IN_TRANSIT -> R.string.filter_in_transit
        OrderStatus.DELIVERED -> R.string.filter_delivered
        OrderStatus.CANCELLED -> R.string.filter_cancelled
        else -> R.string.filter_all
    }

    Box(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = stringResource(textRes).uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}