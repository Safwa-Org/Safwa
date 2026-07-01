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
import androidx.compose.foundation.layout.Row
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

enum class OrderStatus {
    ALL, IN_TRANSIT, DELIVERED, CANCELLED
}

data class OrderHistoryItem(
    val id: String,
    val orderNumber: String,
    val status: OrderStatus,
    val images: List<String>,
    val itemCount: Int,
    val date: String,
    val totalPrice: String
)

@Composable
fun OrderHistoryScreen(
    // viewModel: ProfileViewModel = hiltViewModel(),
    // onNavigateBack: () -> Unit = {}
) {
    // Dummy Data - Will eventually come from ViewModel/DB
    val dummyOrders = remember {
        listOf(
            OrderHistoryItem(
                id = "1",
                orderNumber = "#SAF-204891",
                status = OrderStatus.IN_TRANSIT,
                images = listOf(
                    "https://images.unsplash.com/photo-1523293115678-d2900f52f5d2?q=80&w=200",
                    "https://images.unsplash.com/photo-1627123424574-724758594e93?q=80&w=200"
                ),
                itemCount = 2,
                date = "24 Nov 2025",
                totalPrice = "SAR 1,238"
            ),
            OrderHistoryItem(
                id = "2",
                orderNumber = "#SAF-203114",
                status = OrderStatus.DELIVERED,
                images = listOf(
                    "https://images.unsplash.com/photo-1524592094714-0f0654e20314?q=80&w=200"
                ),
                itemCount = 1,
                date = "08 Nov 2025",
                totalPrice = "SAR 2,150"
            ),
            OrderHistoryItem(
                id = "3",
                orderNumber = "#SAF-201044",
                status = OrderStatus.DELIVERED,
                images = listOf(
                    "https://images.unsplash.com/photo-1511499767150-a48a237f0083?q=80&w=200"
                ),
                itemCount = 1,
                date = "21 Oct 2025",
                totalPrice = "SAR 540"
            )
        )
    }

    // Local State for Search and Filter
    var selectedFilter by remember { mutableStateOf(OrderStatus.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    // Filter Logic
    val filteredOrders = dummyOrders.filter { order ->
        val matchesFilter = selectedFilter == OrderStatus.ALL || order.status == selectedFilter
        val matchesSearch = searchQuery.isBlank() || order.orderNumber.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Custom Top Bar layout to support the search icon action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SafwaTopAppBar(title = stringResource(R.string.order_history))
            }
            IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.search_orders)
                )
            }
        }

        // Animated Search Field
        AnimatedVisibility(
            visible = isSearchVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
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

        // Filter Chips
        OrderFilterRow(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Order List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredOrders, key = { it.id }) { order ->
                OrderCard(
                    order = order,
                    onClick = { /* TODO: Navigate to Details */ }
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