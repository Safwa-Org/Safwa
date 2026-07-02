package com.tasneem.safwa.features.cart.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.CustomButon
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.cart.presentation.state.CartEffect
import com.tasneem.safwa.features.cart.presentation.state.CartEvent
import com.tasneem.safwa.features.cart.presentation.state.CartItem
import com.tasneem.safwa.features.cart.presentation.state.CartState
import com.tasneem.safwa.features.cart.presentation.view.component.CartItemCard
import com.tasneem.safwa.features.cart.presentation.view.component.OrderSummarySection
import com.tasneem.safwa.features.cart.presentation.view.component.PromoCodeSection
import com.tasneem.safwa.features.cart.presentation.viewmodel.CartViewModel
import com.tasneem.safwa.features.settings.savedaddresses.presentation.view.ConfirmDeleteDialog
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToCheckout: () -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CartEffect.NavigateBack -> onNavigateBack()
                CartEffect.NavigateToCheckout -> onNavigateToCheckout()
                CartEffect.NavigateToWishlist -> {}
                is CartEffect.ShowSnackBar -> {
                    launch {
                        snackBarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }
    }

    CartContent(
        state = uiState,
        onEvent = viewModel::onEvent,
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun CartContent(
    state: CartState,
    onEvent: (CartEvent) -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SafwaTopAppBar(
                title = if (state.totalItemCount > 0) {
                    stringResource(id = R.string.your_bag_count, state.totalItemCount)
                } else {
                    stringResource(id = R.string.your_bag)
                },
                onBackClick = { onEvent(CartEvent.BackClicked) },
                windowInsets = androidx.compose.material3.TopAppBarDefaults.windowInsets
            )
        },
        bottomBar = {
            if (!state.isEmpty) {
                CustomButon(
                    title = stringResource(id = R.string.proceed_to_checkout),
                    onContinue = { onEvent(CartEvent.ProceedToCheckout) },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
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

                state.isEmpty -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.empty_cart),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(id = R.string.empty_cart_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))

                        state.items.forEach { item ->
                            CartItemCard(
                                item = item,
                                isRemoving = state.removingItemIds.contains(item.id),
                                isUpdating = state.updatingItemIds.contains(item.id),
                                onIncreaseQuantity = { onEvent(CartEvent.IncreaseQuantity(item.id)) },
                                onDecreaseQuantity = { onEvent(CartEvent.DecreaseQuantity(item.id)) },
                                onRemoveItem = { onEvent(CartEvent.RemoveItemClicked(item.id)) },
                                onApplyUpdate = { onEvent(CartEvent.ApplyQuantityUpdate(item.id)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        PromoCodeSection(
                            promoCode = state.promoCode,
                            onPromoCodeChanged = { onEvent(CartEvent.PromoCodeChanged(it)) },
                            onApplyClick = { onEvent(CartEvent.ApplyPromoCode) }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OrderSummarySection(
                            subtotal = state.subtotal,
                            vat = state.vat,
                            promoCode = state.appliedPromoCode,
                            promoDiscount = state.promoDiscount,
                            total = state.total,
                            currency = state.items.firstOrNull()?.currency ?: "SAR"
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        
        if (state.itemPendingRemoval != null) {
            ConfirmDeleteDialog(
                title = stringResource(id = R.string.remove_item),
                message = stringResource(id = R.string.confirm_remove_item),
                onConfirm = { onEvent(CartEvent.ConfirmRemoveItem) },
                onDismiss = { onEvent(CartEvent.CancelRemoveItem) }
            )
        }
    }
}

@Preview(name = "Light", showBackground = true, showSystemUi = true)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun CartContentPreview() {
    SafwaTheme {
        CartContent(
            state = CartState(
                items = listOf(
                    CartItem("1", "p1", "Nuit d'Or EDP", "50 ml", "MAISON", 480.0, "SAR", 1, ""),
                    CartItem("2", "p2", "Vermilion Bifold", "Saddle", "ATELIER", 320.0, "SAR", 2, "")
                ),
                appliedPromoCode = "ELITE10",
                promoDiscount = 50.0
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Empty Cart", showBackground = true, showSystemUi = true)
@Composable
private fun CartEmptyPreview() {
    SafwaTheme {
        CartContent(
            state = CartState(),
            onEvent = {}
        )
    }
}
