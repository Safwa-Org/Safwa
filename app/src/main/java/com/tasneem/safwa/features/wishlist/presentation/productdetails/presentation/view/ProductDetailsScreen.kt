package com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state.ProductDetailsEffect
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state.ProductDetailsEvent
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state.ProductDetailsState
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.state.mapper.ProductUiModel
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.view.component.ProductDescriptionSection
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.view.component.ProductImageHeader
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.view.component.ProductInfoSection
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.view.component.StickyBottomActionBar
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.viewmodel.ProductDetailsViewModel

@Composable
fun ProductDetailsScreen(
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProductDetailsEffect.NavigateBack -> onNavigateBack
                is ProductDetailsEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }
            }
        }
    }
    ProductDetailsContent(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ProductDetailsContent(
    state: ProductDetailsState,
    onEvent: (ProductDetailsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            state.product?.let { product ->
                StickyBottomActionBar(
                    priceFormatted = product.priceFormatted,
                    onAddToCart = { onEvent(ProductDetailsEvent.AddToCartClicked) }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null -> {
                    Text(text = state.errorMessage, modifier = Modifier.align(Alignment.Center))
                }

                state.product != null -> {
                    val product = state.product
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ProductImageHeader(
                            productImages = product.imageUrls,
                            imageAltTexts = product.imageLabels,
                            title = product.title,
                            isWishlisted = state.isWishlisted,
                            onBack = { onEvent(ProductDetailsEvent.BackClicked) },
                            onWishlistToggle = { onEvent(ProductDetailsEvent.ToggleWishlist) },
                            onShare = { onEvent(ProductDetailsEvent.ShareClicked) }
                        )

                        ProductInfoSection(
                            title = product.title,
                            vendor = product.vendor,
                            rating = product.rating,
                            reviewCount = product.reviewCount,
                            priceFormatted = product.priceFormatted,
                            sizes = product.sizes,
                            selectedSize = state.selectedSize,
                            onSizeSelected = { size -> onEvent(ProductDetailsEvent.SizeSelected(size)) }
                        )

                        ProductDescriptionSection(
                            description = product.description,
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
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
private fun ProductDetailsContentPreview() {
    SafwaTheme {
        ProductDetailsContent(
            state = ProductDetailsState(
                isLoading = false,
                isWishlisted = true,
                selectedSize = "M",
                product = ProductUiModel(
                    id = "1",
                    title = "Nike Air Max 270",
                    vendor = "Nike",
                    description = "The Nike Air Max 270 delivers visible cushioning under every step. Designed for everyday comfort, it features a lightweight upper and responsive Air unit for all-day wear.",
                    priceFormatted = "$149.99",
                    imageUrls = listOf(
                        "https://picsum.photos/600/600?1",
                        "https://picsum.photos/600/600?1",
                        "https://picsum.photos/600/600?2",
                    ),
                    imageLabels = listOf(
                        "Front view",
                        "Side view",
                        "Back view"
                    ),
                    rating = 4.8f,
                    reviewCount = 236,
                    sizes = listOf("S", "M", "L", "XL")
                )
            ),
            onEvent = {}
        )
    }
}