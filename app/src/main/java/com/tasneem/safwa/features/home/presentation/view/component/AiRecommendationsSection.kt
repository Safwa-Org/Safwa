package com.tasneem.safwa.features.home.presentation.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.wishlist.presentation.components.ProductCard

@Composable
fun AiRecommendationsSection(
    recommendations: List<Product>,
    isAiLoading: Boolean,
    aiErrorMessage: String?,
    favoriteProductIds: Set<String>,
    onToggleFavorite: (Product) -> Unit,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isAiLoading) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (aiErrorMessage != null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error: $aiErrorMessage", color = Color.Red)
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = modifier
        ) {
            items(count = recommendations.size, key = { recommendations[it].id }) { index ->
                val product = recommendations[index]
                Box(modifier = Modifier.width(160.dp)) {
                    ProductCard(
                        product = product,
                        isFavorite = favoriteProductIds.contains(product.id),
                        onToggleFavorite = { onToggleFavorite(product) },
                        onClick = { onProductClick(product) }
                    )
                }
            }
        }
    }
}
