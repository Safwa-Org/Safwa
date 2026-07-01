package com.tasneem.safwa.features.cart.presentation.view.component

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.theme.SafwaTheme

@Composable
fun OrderSummarySection(
    subtotal: Double,
    vat: Double,
    promoCode: String?,
    promoDiscount: Double,
    total: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryRow(
            label = stringResource(id = R.string.subtotal),
            value = "$currency ${subtotal.toInt()}",
            labelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        SummaryRow(
            label = stringResource(id = R.string.vat_label),
            value = "$currency ${vat.toInt()}",
            labelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        if (promoCode != null && promoDiscount > 0) {
            SummaryRow(
                label = stringResource(id = R.string.promo_discount_label, promoCode),
                value = "– $currency ${promoDiscount.toInt()}",
                labelColor = MaterialTheme.colorScheme.primary,
                valueColor = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.total),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$currency ${total.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    labelColor: androidx.compose.ui.graphics.Color,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderSummarySectionPreview() {
    SafwaTheme {
        OrderSummarySection(
            subtotal = 1120.0,
            vat = 168.0,
            promoCode = "ELITE10",
            promoDiscount = 50.0,
            total = 1238.0,
            currency = "SAR",
            modifier = Modifier.padding(16.dp)
        )
    }
}
