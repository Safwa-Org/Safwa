package com.tasneem.safwa.features.settings.orderhistory.presentation.view
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.core.theme.SafwaTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.features.settings.orderhistory.presentation.OrderHistoryViewModel
import com.tasneem.safwa.features.settings.orderhistory.presentation.OrderHistoryIntent
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    var isSearchVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            SafwaTopAppBar(
                title = stringResource(R.string.order_history),
                modifier = Modifier.fillMaxWidth()
            )
            
            IconButton(
                onClick = { isSearchVisible = !isSearchVisible },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.search_orders)
                )
            }
        }

        AnimatedVisibility(
            visible = isSearchVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.processIntent(OrderHistoryIntent.SearchOrders(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_orders)) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OrderFilterRow(
            selectedFilter = state.selectedFilter,
            onFilterSelected = { viewModel.processIntent(OrderHistoryIntent.FilterOrders(it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (!state.isLoading && state.filteredOrders.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No orders found.",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Try adjusting your filters or search query.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.filteredOrders, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            onClick = { /* TODO: Navigate to Details */ }
                        )
                    }
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun OrderHistoryScreenPreview() {
    SafwaTheme {
        OrderHistoryScreen()
    }
}