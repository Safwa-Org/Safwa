package com.tasneem.safwa.features.checkout.presentation.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.features.checkout.presentation.state.OrderSummaryUiModel

@Composable
fun OrderSummaryCard(
    summary: OrderSummaryUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryItemRow(
            label = stringResource(id = R.string.checkout_subtotal),
            value = summary.subtotal
        )

        summary.discount?.let { discount ->
            SummaryItemRow(
                label = summary.discountCode
                    ?.let { stringResource(id = R.string.checkout_discount_with_code, it) }
                    ?: stringResource(id = R.string.checkout_discount),
                value = discount,
                valueColor = MaterialTheme.colorScheme.primary
            )
        }

        SummaryItemRow(
            label = stringResource(id = R.string.checkout_shipping),
            value = when {
                summary.isShippingFree -> stringResource(id = R.string.checkout_shipping_free)
                summary.shippingFee != null -> summary.shippingFee
                else -> stringResource(id = R.string.checkout_calculated_later)
            }
        )

        SummaryItemRow(
            label = stringResource(id = R.string.checkout_vat),
            value = summary.vatAmount
                ?: stringResource(id = R.string.checkout_calculated_later)
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.checkout_total),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = MaterialTheme.typography.displayMedium.fontFamily
                ),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = summary.totalAmount,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = MaterialTheme.typography.displayMedium.fontFamily
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SummaryItemRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = MaterialTheme.typography.titleMedium.fontFamily
            ),
            color = valueColor
        )
    }
}
