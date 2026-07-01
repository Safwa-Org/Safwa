package com.tasneem.safwa.features.settings.profile.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R

@Composable
fun ProfileStatsRow(
    ordersCount: String,
    wishlistCount: String,
    points: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatItem(
            value = ordersCount,
            label = stringResource(R.string.orderscapital),
            modifier = Modifier.weight(1f)
        )
        StatItem(
            value = wishlistCount,
            label = stringResource(R.string.wishlistcapital),
            modifier = Modifier.weight(1f)
        )
        StatItem(
            value = points,
            label = stringResource(R.string.pointscapital),
            modifier = Modifier.weight(1f)
        )
    }
}