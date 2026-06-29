package com.tasneem.safwa.features.home.presentation.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaLogo
import com.tasneem.safwa.core.theme.SafwaTheme

@Composable
fun HomeTopBar(
    onCartClick: () -> Unit,
    cartItemCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SafwaLogo(size = 24.dp)
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(
            onClick = onCartClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {


            if (cartItemCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(text = cartItemCount.toString())
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = stringResource(id = R.string.cart_icon_content_desc),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = stringResource(id = R.string.cart_icon_content_desc),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(
    name = "Empty Cart",
    showBackground = true
)
@Composable
private fun HomeTopBarEmptyCartPreview() {
    SafwaTheme {
        HomeTopBar(
            onCartClick = {},
            cartItemCount = 0
        )
    }
}

@Preview(
    name = "Cart With Items",
    showBackground = true
)
@Composable
private fun HomeTopBarWithItemsPreview() {
    SafwaTheme {
        HomeTopBar(
            onCartClick = {},
            cartItemCount = 3
        )
    }
}