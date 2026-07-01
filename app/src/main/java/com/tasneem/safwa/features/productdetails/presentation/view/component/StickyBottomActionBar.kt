package com.tasneem.safwa.features.productdetails.presentation.view.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.theme.SafwaTheme

@Composable
fun StickyBottomActionBar(
    priceFormatted: String,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
    isAvailable: Boolean = true,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onAddToCart,
                enabled = isAvailable,
                modifier = Modifier
                    .size(width = 44.dp, height = 56.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(36.dp))
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = stringResource(R.string.shopping_bag),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Button(
                onClick = onAddToCart,
                enabled = isAvailable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(36.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAvailable)
                            stringResource(R.string.add_to_cart_format, priceFormatted)
                        else
                            stringResource(R.string.out_of_stock),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}


@Preview(
    name = "Light",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun StickyBottomActionBarPreview() {
    SafwaTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                StickyBottomActionBar(
                    priceFormatted = "$149.99",
                    onAddToCart = {}
                )
            }
        ) { innerPadding ->
            Spacer(modifier = Modifier.padding(innerPadding))
        }
    }
}