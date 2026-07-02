package com.tasneem.safwa.features.payment.presentation.view

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.R
import kotlinx.coroutines.launch
import com.tasneem.safwa.core.shared_component.CustomButon
import com.tasneem.safwa.core.shared_component.SafwaTopAppBar
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.payment.presentation.state.PaymentEffect
import com.tasneem.safwa.features.payment.presentation.state.PaymentEvent
import com.tasneem.safwa.features.payment.domain.model.PaymentMethodType
import com.tasneem.safwa.features.payment.presentation.state.PaymentState
import com.tasneem.safwa.features.payment.domain.model.SavedCard
import com.tasneem.safwa.features.payment.presentation.view.component.AddCardDialog
import com.tasneem.safwa.features.payment.presentation.view.component.PaymentOptionCard
import com.tasneem.safwa.features.payment.presentation.view.component.SavedCardItem
import com.tasneem.safwa.features.payment.presentation.viewmodel.PaymentViewModel

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PaymentEffect.NavigateBack -> onNavigateBack()
                PaymentEffect.NavigateToHome -> onNavigateToHome()
                is PaymentEffect.ShowSnackBar -> {
                    val message = context.getString(effect.messageRes)
                    scope.launch {
                        snackBarHostState.showSnackbar(message)
                    }
                }
            }
        }
    }

    PaymentContent(
        state = uiState,
        onEvent = viewModel::onEvent,
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun PaymentContent(
    state: PaymentState,
    onEvent: (PaymentEvent) -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    val showContinue = state.selectedMethod == PaymentMethodType.CASH_ON_DELIVERY ||
            (state.selectedMethod == PaymentMethodType.VISA && state.selectedCardId != null)

    Scaffold(
        topBar = {
            SafwaTopAppBar(
                title = stringResource(R.string.payment_methods),
                onBackClick = { onEvent(PaymentEvent.BackClicked) },
                windowInsets = androidx.compose.material3.TopAppBarDefaults.windowInsets
            )
        },
        bottomBar = {
            if (showContinue) {
                CustomButon(
                    title = stringResource(R.string.continue_text),
                    onContinue = { onEvent(PaymentEvent.ContinueClicked) },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.select_payment_method),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Cash on Delivery Option
                    PaymentOptionCard(
                        title = stringResource(R.string.cash_on_delivery),
                        isSelected = state.selectedMethod == PaymentMethodType.CASH_ON_DELIVERY,
                        onClick = { onEvent(PaymentEvent.MethodSelected(PaymentMethodType.CASH_ON_DELIVERY)) }
                    )
                    PaymentOptionCard(
                        title = stringResource(R.string.by_visa),
                        isSelected = state.selectedMethod == PaymentMethodType.VISA,
                        onClick = { onEvent(PaymentEvent.MethodSelected(PaymentMethodType.VISA)) }
                    )

                    AnimatedVisibility(visible = state.selectedMethod == PaymentMethodType.VISA) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            state.savedCards.forEach { card ->
                                SavedCardItem(
                                    card = card,
                                    isSelected = card.id == state.selectedCardId,
                                    onClick = { onEvent(PaymentEvent.CardSelected(card.id)) }
                                )
                            }
                            
                            TextButton(
                                onClick = { onEvent(PaymentEvent.ToggleAddCardDialog(true)) },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add new card",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.add_new_card),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
    
    if (state.showAddCardDialog) {
        AddCardDialog(
            isLoading = state.isLoading,
            onDismiss = { onEvent(PaymentEvent.ToggleAddCardDialog(false)) },
            onSave = { number, firstName, lastName, month, year, cvv ->
                onEvent(PaymentEvent.SaveNewCard(number, firstName, lastName, month, year, cvv))
            }
        )
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
private fun PaymentContentPreview() {
    SafwaTheme {
        PaymentContent(
            state = PaymentState(
                savedCards = listOf(
                    SavedCard("1", "Visa", "4242", "Aisha Al-Marri", "08/28", true),
                    SavedCard("2", "Mada", "1187", "Aisha Al-Marri", "11/27", false)
                ),
                selectedMethod = PaymentMethodType.VISA,
                selectedCardId = "1"
            ),
            onEvent = {}
        )
    }
}
