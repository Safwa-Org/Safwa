package com.tasneem.safwa.features.checkout.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.R
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.checkout.presentation.state.AddressUiModel
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutEffect
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutEvent
import com.tasneem.safwa.features.checkout.presentation.state.CheckoutState
import com.tasneem.safwa.features.checkout.presentation.state.PaymentMethodUiModel
import com.tasneem.safwa.features.checkout.presentation.view.component.CheckoutBottomBar
import com.tasneem.safwa.features.checkout.presentation.view.component.DeliveryAddressCard
import com.tasneem.safwa.features.checkout.presentation.view.component.OrderSummaryCard
import com.tasneem.safwa.features.checkout.presentation.view.component.PaymentMethodRow
import com.tasneem.safwa.features.checkout.presentation.view.component.SectionHeader
import com.tasneem.safwa.features.checkout.presentation.viewmodel.CheckoutViewModel

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToSavedAddresses: () -> Unit,
    onNavigateToPayment: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckoutEffect.NavigateBack -> onNavigateBack()
                is CheckoutEffect.NavigateToSavedAddresses -> onNavigateToSavedAddresses()
                is CheckoutEffect.NavigateToPaymentConfirmation -> onNavigateToPayment()
                is CheckoutEffect.ShowError -> {
                }
            }
        }
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
            CheckoutBottomBar(
                totalAmount = state.totalAmount,
                onPlaceOrderClick = { onEvent(CheckoutEvent.OnPlaceOrderClick) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionHeader(
                step = stringResource(id = R.string.checkout_delivery_step),
                title = stringResource(id = R.string.checkout_delivery_title)
            )
            state.deliveryAddress?.let { address ->
                DeliveryAddressCard(
                    address = address,
                    onChangeClick = { onEvent(CheckoutEvent.OnChangeAddressClick) }
                )
            }

            SectionHeader(
                step = stringResource(id = R.string.checkout_payment_step),
                title = stringResource(id = R.string.checkout_payment_title)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.paymentMethods.forEach { method ->
                    PaymentMethodRow(
                        paymentMethod = method,
                        onSelect = { onEvent(CheckoutEvent.OnPaymentMethodSelected(method.id)) }
                    )
                }
            }

            // Step 3: Summary Section
            SectionHeader(
                step = stringResource(id = R.string.checkout_summary_step),
                title = stringResource(id = R.string.checkout_summary_title)
            )
            OrderSummaryCard(state = state)

            Spacer(modifier = Modifier.height(80.dp)) // Extra padding below scroll view
        }
    }
}

private val fakeState = CheckoutState(
    deliveryAddress = AddressUiModel(
        tag = "Home",
        recipientName = "Aisha Al-Marri",
        detailedAddress = "Al Olaya, Riyadh 12241, KSA"
    ),
    paymentMethods = listOf(
        PaymentMethodUiModel("1", "Online Payment", "Visa • 4242", true),
        PaymentMethodUiModel("2", "Cash on Delivery", "Pay when it arrives", false)
    ),
    subtotal = "SAR 1,120",
    shippingFee = "Free",
    isShippingFree = true,
    vatAmount = "SAR 168",
    totalAmount = "SAR 1,238"
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