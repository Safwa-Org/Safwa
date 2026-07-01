package com.tasneem.safwa.features.category.presentation.category_products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.features.category.presentation.category_products.viewmodel.CategoryProductsViewModel
import com.tasneem.safwa.features.category.presentation.category_products.state.CategoryProductsState
import com.tasneem.safwa.features.wishlist.presentation.components.ProductCard
import com.tasneem.safwa.features.core.domain.model.Product
import androidx.compose.ui.res.stringResource
import com.tasneem.safwa.R

@Composable
fun CategoryProductsScreen(
    viewModel: CategoryProductsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CategoryProductsContent(
        state = state,
        categoryName = viewModel.categoryName,
        onNavigateBack = onNavigateBack,
        onNavigateToProductDetails = onNavigateToProductDetails,
        onToggleFavorite = { product -> viewModel.toggleFavorite(product) }
    )
}

@Composable
fun CategoryProductsContent(
    state: CategoryProductsState,
    categoryName: String,
    onNavigateBack: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    onToggleFavorite: (Product) -> Unit
) {
    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(top = 18.dp)) {
                SafwaTopAppBar(
                    title = categoryName,
                    onBackClick = onNavigateBack
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (!state.error.isNullOrEmpty()) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else if (state.products.isEmpty()) {
                Text(text = stringResource(id = R.string.no_products_found), modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = state.favoriteProductIds.contains(product.id),
                            onToggleFavorite = { onToggleFavorite(product) },
                            onClick = { onNavigateToProductDetails(product.handle) }
                        )
                    }
                }
            }
        }
    }
}

