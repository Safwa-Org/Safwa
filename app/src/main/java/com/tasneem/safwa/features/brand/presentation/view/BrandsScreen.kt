package com.tasneem.safwa.features.brand.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.R
import com.tasneem.safwa.features.brand.presentation.state.BrandsUiEffect
import com.tasneem.safwa.features.brand.presentation.state.BrandsUiIntent
import com.tasneem.safwa.features.brand.presentation.view.component.BrandCard
import com.tasneem.safwa.features.brand.presentation.view.component.BrandsTopBar
import com.tasneem.safwa.features.brand.presentation.viewmodel.BrandsViewModel
import com.tasneem.safwa.features.core.presentation.component.ErrorContentWithRetry
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BrandsScreen(
    viewModel: BrandsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToBrandProducts: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is BrandsUiEffect.NavigateBack -> onNavigateBack()
                is BrandsUiEffect.NavigateToBrandDetails -> onNavigateToBrandProducts(effect.brandName)
            }
        }
    }

    Scaffold(
        topBar = {
            BrandsTopBar(
                onBackClick = { viewModel.handleIntent(BrandsUiIntent.OnBackClick) },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                uiState.error != null -> {
                    ErrorContentWithRetry(
                        title = stringResource(uiState.error!!.titleRes),
                        description = stringResource(uiState.error!!.descriptionRes),
                        onRetry = { viewModel.handleIntent(BrandsUiIntent.LoadBrands) },
                    )
                }

                uiState.brands.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(id = R.string.no_brands_found))
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.brands, key = { it.name }) { brand ->
                            BrandCard(
                                brandName = brand.name,
                                onClick = { viewModel.handleIntent(BrandsUiIntent.OnBrandClick(brand)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
