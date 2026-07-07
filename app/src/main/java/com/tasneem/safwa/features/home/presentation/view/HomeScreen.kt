package com.tasneem.safwa.features.home.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.R
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.home.presentation.state.GreetingType
import com.tasneem.safwa.features.home.presentation.state.HomeEffect
import com.tasneem.safwa.features.home.presentation.state.HomeEvent
import com.tasneem.safwa.features.home.presentation.state.HomeState
import com.tasneem.safwa.features.home.presentation.view.component.BrandsRow
import com.tasneem.safwa.features.home.presentation.view.component.PromoBannerPager
import com.tasneem.safwa.features.home.presentation.view.component.GreetingSection
import com.tasneem.safwa.features.home.presentation.view.component.HomeTopBar
import com.tasneem.safwa.features.home.presentation.view.component.SectionHeader
import com.tasneem.safwa.features.home.presentation.viewmodel.HomeViewModel
import com.tasneem.safwa.features.core.domain.model.Product
import com.tasneem.safwa.features.wishlist.presentation.components.CategoriesRow
import com.tasneem.safwa.features.wishlist.presentation.components.ProductCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToProductDetails: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToCategoryProducts: (String) -> Unit = {},
    onNavigateToBrands: () -> Unit = {},
    onNavigateToBrandProducts: (String) -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToSearch -> {}
                HomeEffect.NavigateToCart -> onNavigateToCart()
                HomeEffect.NavigateToCategories -> onNavigateToCategories()
                is HomeEffect.NavigateToCategoryProducts -> onNavigateToCategoryProducts(effect.categoryName)
                is HomeEffect.NavigateToProductDetails -> onNavigateToProductDetails(effect.handle)
                is HomeEffect.NavigateToBrand -> onNavigateToBrandProducts(effect.brand)
                HomeEffect.NavigateToBrands -> onNavigateToBrands()
            }
        }
    }

    HomeContent(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun HomeContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.errorMessage)
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(span = { GridItemSpan(2) }) {
                            HomeTopBar(
                                onCartClick = { onEvent(HomeEvent.CartClicked) },
                                cartItemCount = state.cartItemCount
                            )
                        }

                        item(span = { GridItemSpan(2) }) {
                            GreetingSection(
                                greeting = state.greeting,
                                userName = state.userName
                            )
                        }

                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        item(span = { GridItemSpan(2) }) {
                            PromoBannerPager(
                                banners = state.promoBanners
                            )
                        }

                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        item(span = { GridItemSpan(2) }) {
                            SectionHeader(
                                title = stringResource(id = R.string.categories),
                                actionText = stringResource(id = R.string.view_all),
                                onActionClick = { onEvent(HomeEvent.ViewAllCategories) }
                            )
                        }
                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        item(span = { GridItemSpan(2) }) {
                            CategoriesRow(
                                categories = state.categories,
                                selectedCategory = state.selectedCategory,
                                onCategorySelected = { category ->
                                    onEvent(HomeEvent.CategorySelected(category))
                                },
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }

                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        item(span = { GridItemSpan(2) }) {
                            SectionHeader(
                                title = stringResource(id = R.string.best_sellers)
                            )
                        }
                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        items(
                            items = state.filteredProducts,
                            key = { it.id }
                        ) { product ->
                            ProductCard(
                                product = product,
                                isFavorite = state.favoriteProductIds.contains(product.id),
                                onToggleFavorite = { onEvent(HomeEvent.ToggleFavorite(product)) },
                                onClick = { onEvent(HomeEvent.ProductClicked(product)) },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                            )
                        }

                        // Always show AI section for debugging
                        if (true) {
                            item(span = { GridItemSpan(2) }) {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                            item(span = { GridItemSpan(2) }) {
                                SectionHeader(
                                    title = "AI Recommendations \u2728"
                                )
                            }
                            item(span = { GridItemSpan(2) }) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            item(span = { GridItemSpan(2) }) {
                                if (state.isAiLoading) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                } else if (state.errorMessage != null) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(100.dp).padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "Error: ${state.errorMessage}", color = androidx.compose.ui.graphics.Color.Red)
                                    }
                                } else if (state.aiRecommendations.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "No recommendations found")
                                    }
                                } else {
                                    androidx.compose.foundation.lazy.LazyRow(
                                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        items(count = state.aiRecommendations.size, key = { state.aiRecommendations[it].id }) { index ->
                                            val product = state.aiRecommendations[index]
                                            Box(modifier = Modifier.width(160.dp)) {
                                                ProductCard(
                                                    product = product,
                                                    isFavorite = state.favoriteProductIds.contains(product.id),
                                                    onToggleFavorite = { onEvent(HomeEvent.ToggleFavorite(product)) },
                                                    onClick = { onEvent(HomeEvent.ProductClicked(product)) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        item(span = { GridItemSpan(2) }) {
                            SectionHeader(
                                title = stringResource(id = R.string.shop_by_brand),
                                actionText = stringResource(id = R.string.view_all),
                                onActionClick = { onEvent(HomeEvent.ViewAllBrands) }
                            )
                        }
                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        item(span = { GridItemSpan(2) }) {
                            BrandsRow(
                                brands = state.brands,
                                onBrandClick = { onEvent(HomeEvent.BrandClicked(it)) }
                            )
                        }

                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(16.dp))
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
private fun HomeContentPreview() {
    val sampleProducts = listOf(
        Product("1", "Nuit d'Or Eau de...", "nuit-dor", "Desc", "MAISON", "Fragrance", "480", "SAR", listOf(""), listOf("")),
        Product("2", "Vermilion Bifold", "vermilion", "Desc", "ATELIER", "Leather", "320", "SAR", listOf(""), listOf("")),
        Product("3", "Onyx Chrono...", "onyx", "Desc", "NOIR", "Watches", "2150", "SAR", listOf(""), listOf("")),
        Product("4", "Halcyon Round", "halcyon", "Desc", "LUMIÈRE", "Accessories", "540", "SAR", listOf(""), listOf(""))
    )
    SafwaTheme {
        HomeContent(
            state = HomeState(
                userName = "Ashraf",
                greeting = GreetingType.EVENING,
                products = sampleProducts,
                filteredProducts = sampleProducts,
                categories = listOf(
                    com.tasneem.safwa.features.category.domain.model.Category("1", "All", "all", null),
                    com.tasneem.safwa.features.category.domain.model.Category("2", "Fragrance", "fragrance", null),
                    com.tasneem.safwa.features.category.domain.model.Category("3", "Leather", "leather", null)
                ),
                selectedCategory = com.tasneem.safwa.features.category.domain.model.Category("1", "All", "all", null),
                brands = listOf("Maison", "Atelier", "Noir")
            ),
            onEvent = {}
        )
    }
}
