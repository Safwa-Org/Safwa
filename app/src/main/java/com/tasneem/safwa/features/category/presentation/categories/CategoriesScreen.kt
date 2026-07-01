package com.tasneem.safwa.features.category.presentation.categories


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.features.category.presentation.categories.viewmodel.CategoriesViewModel
import com.tasneem.safwa.features.category.presentation.categories.state.CategoriesState
import androidx.compose.ui.res.stringResource
import com.tasneem.safwa.R
import com.tasneem.safwa.features.category.presentation.categories.components.CategoryItem

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onCategoryClicked: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CategoriesContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onCategoryClicked = onCategoryClicked
    )
}

@Composable
fun CategoriesContent(
    state: CategoriesState,
    onNavigateBack: () -> Unit,
    onCategoryClicked: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(top = 18.dp)) {
                SafwaTopAppBar(
                    title = stringResource(id = R.string.categories),
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 32.dp, bottom = 16.dp)
                ) {
                    items(state.categories) { category ->
                        CategoryItem(category = category, onClick = { onCategoryClicked(category.handle) })
                    }
                }
            }
        }
    }
}

