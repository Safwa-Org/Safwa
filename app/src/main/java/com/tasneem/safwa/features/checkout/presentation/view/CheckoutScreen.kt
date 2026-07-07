package com.tasneem.safwa.features.checkout.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.R
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.checkout.presentation.state.AddressUiModel
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutEffect
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutEvent
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutState
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutStatus
import com.tasneem.safwa.features.checkout.presentation.state.OrderSummaryUiModel
import com.tasneem.safwa.features.checkout.presentation.state.PaymentMethodUiModel
import com.tasneem.safwa.features.checkout.presentation.state.PlaceOrderStep
import com.tasneem.safwa.features.checkout.presentation.state.RetryAction
import com.tasneem.safwa.features.checkout.presentation.view.component.AddressPickerSheet
import com.tasneem.safwa.features.checkout.presentation.view.component.CheckoutBottomBar
import com.tasneem.safwa.features.checkout.presentation.view.component.CheckoutItemRow
import com.tasneem.safwa.features.checkout.presentation.view.component.DeliveryAddressCard
import com.tasneem.safwa.features.checkout.presentation.view.component.OrderSummaryCard
import com.tasneem.safwa.features.checkout.presentation.view.component.SectionHeader
import com.tasneem.safwa.features.checkout.presentation.viewmodel.CheckoutViewModel

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToSavedAddresses: () -> Unit,
    onNavigateToOrderConfirmed: (orderId: String, totalAmount: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckoutEffect.NavigateBack -> onNavigateBack()
                is CheckoutEffect.NavigateToSavedAddresses -> onNavigateToSavedAddresses()
                is CheckoutEffect.NavigateToOrderConfirmed ->
                    onNavigateToOrderConfirmed(effect.orderId, effect.totalAmount)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(CheckoutEvent.OnScreenResumed)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    CheckoutContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = Modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    state: CheckoutState,
    onEvent: (CheckoutEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.checkout_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CheckoutEvent.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (state.status is CheckoutStatus.Ready) {
                CheckoutBottomBar(
                    totalAmount = state.summary.totalAmount,
                    onPlaceOrderClick = { onEvent(CheckoutEvent.OnPlaceOrderClick) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.status) {
                is CheckoutStatus.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> CheckoutSections(state = state, onEvent = onEvent)
            }

            if (state.status is CheckoutStatus.PlacingOrder) {
                PlacingOrderOverlay(step = state.status.step)
            }
        }
    }

    if (state.showAddressPicker) {
        AddressPickerSheet(
            addresses = state.addresses,
            selectedAddressId = state.selectedAddressId,
            onAddressSelected = { onEvent(CheckoutEvent.OnAddressSelected(it)) },
            onManageAddresses = { onEvent(CheckoutEvent.OnManageAddressesClick) },
            onDismiss = { onEvent(CheckoutEvent.OnDismissAddressPicker) }
        )
    }

    (state.status as? CheckoutStatus.Failure)?.let { failure ->
        CheckoutErrorDialog(failure = failure, onEvent = onEvent)
    }
}

@Composable
private fun CheckoutSections(
    state: CheckoutState,
    onEvent: (CheckoutEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SectionHeader(
            step = stringResource(id = R.string.checkout_delivery_step),
            title = stringResource(id = R.string.checkout_delivery_title)
        )
        val selectedAddress = state.selectedAddress
        if (selectedAddress != null) {
            DeliveryAddressCard(
                address = selectedAddress,
                onChangeClick = { onEvent(CheckoutEvent.OnChangeAddressClick) }
            )
        } else {
            NoAddressCard(onAddAddress = { onEvent(CheckoutEvent.OnManageAddressesClick) })
        }

        SectionHeader(
            step = stringResource(id = R.string.checkout_payment_step),
            title = stringResource(id = R.string.checkout_payment_title)
        )
        state.paymentMethod?.let { method ->
            SelectedPaymentMethodCard(
                method = method,
                onChangeClick = { onEvent(CheckoutEvent.OnChangePaymentMethodClick) }
            )
        }

        SectionHeader(
            step = stringResource(id = R.string.checkout_summary_step),
            title = stringResource(id = R.string.checkout_summary_title)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.cartItems.forEach { item ->
                CheckoutItemRow(item = item)
            }
        }
        OrderSummaryCard(summary = state.summary)

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SelectedPaymentMethodCard(
    method: PaymentMethodUiModel,
    onChangeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = method.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = method.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Text(
            text = stringResource(id = R.string.checkout_change_action),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onChangeClick() }
        )
    }
}

@Composable
private fun NoAddressCard(onAddAddress: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.checkout_no_address),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        OutlinedButton(onClick = onAddAddress) {
            Text(text = stringResource(id = R.string.checkout_add_address))
        }
    }
}

@Composable
private fun PlacingOrderOverlay(step: PlaceOrderStep) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(
                    id = when (step) {
                        PlaceOrderStep.CREATING_ORDER -> R.string.checkout_step_creating_order
                        PlaceOrderStep.PROCESSING_PAYMENT -> R.string.checkout_step_processing_payment
                        PlaceOrderStep.CONFIRMING_PAYMENT -> R.string.checkout_step_confirming_payment
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun CheckoutErrorDialog(
    failure: CheckoutStatus.Failure,
    onEvent: (CheckoutEvent) -> Unit
) {
    val message = failure.message
        ?: failure.messageRes?.let { stringResource(id = it) }
        ?: stringResource(id = R.string.checkout_error_generic)

    AlertDialog(
        onDismissRequest = { onEvent(CheckoutEvent.OnErrorDismiss) },
        title = { Text(text = stringResource(id = R.string.checkout_error_title)) },
        text = { Text(text = message) },
        confirmButton = {
            if (failure.retry != RetryAction.NONE) {
                TextButton(onClick = { onEvent(CheckoutEvent.OnRetry) }) {
                    Text(text = stringResource(id = R.string.checkout_retry))
                }
            } else {
                TextButton(onClick = { onEvent(CheckoutEvent.OnErrorDismiss) }) {
                    Text(text = stringResource(id = R.string.checkout_dismiss))
                }
            }
        },
        dismissButton = {
            if (failure.retry != RetryAction.NONE) {
                TextButton(onClick = { onEvent(CheckoutEvent.OnErrorDismiss) }) {
                    Text(text = stringResource(id = R.string.checkout_dismiss))
                }
            }
        }
    )
}

// fake data for preview
private val fakeState = CheckoutState(
    status = CheckoutStatus.Ready,
    addresses = listOf(
        AddressUiModel(
            id = "1",
            tag = "Home",
            recipientName = "Aisha Al-Marri",
            detailedAddress = "Al Olaya, Riyadh 12241, KSA"
        )
    ),
    selectedAddressId = "1",
    paymentMethod = PaymentMethodUiModel("card:1", "Visa", "•••• 4242", true),
    summary = OrderSummaryUiModel(
        subtotal = "USD 1,120.00",
        discount = "- USD 50.00",
        discountCode = "SAVE50",
        shippingFee = null,
        isShippingFree = true,
        vatAmount = "USD 168.00",
        totalAmount = "USD 1,238.00"
    )
)

@Preview(showBackground = true, name = "Checkout Light Theme")
@Composable
fun CheckoutContentLightPreview() {
    SafwaTheme(darkTheme = false) {
        CheckoutContent(state = fakeState, onEvent = {})
    }
}

@Preview(showBackground = true, name = "Checkout Dark Theme")
@Composable
fun CheckoutContentDarkPreview() {
    SafwaTheme(darkTheme = true) {
        CheckoutContent(state = fakeState, onEvent = {})
    }
}
