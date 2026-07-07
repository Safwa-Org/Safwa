package com.tasneem.safwa.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tasneem.safwa.R
import com.tasneem.safwa.features.aichat.presentation.view.AiChatBottomSheet
import com.tasneem.safwa.features.aichat.presentation.view.SafwaAiFab
import com.tasneem.safwa.features.aichat.presentation.viewmodel.AiChatViewModel
import com.tasneem.safwa.features.core.presentation.component.AuthGateContent
import com.tasneem.safwa.features.home.presentation.view.HomeScreen
import com.tasneem.safwa.features.home.presentation.view.component.HomeBottomBar
import com.tasneem.safwa.features.search.presentation.SearchScreen
import com.tasneem.safwa.features.settings.profile.presentation.view.ProfileScreen
import com.tasneem.safwa.features.wishlist.presentation.WishlistScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isAuthenticated: Boolean,
    onNavigateToProductDetails: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToOrderHistory: () -> Unit = {},
    onNavigateToSavedAddresses: () -> Unit = {},
    onNavigateToLanguageAndCurrency: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToCategoryProducts: (String) -> Unit = {},
    onNavigateToSignIn: () -> Unit = {},
    onNavigateToCreateAccount: () -> Unit = {},
    onNavigateToBrands: () -> Unit = {},
    onNavigateToBrandProducts: (String) -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showAiChat by rememberSaveable { mutableStateOf(false) }

    val aiChatViewModel: AiChatViewModel = hiltViewModel()
    val aiChatState by aiChatViewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                selectedIndex = selectedTab,
                onItemSelected = { index -> selectedTab = index }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !showAiChat,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                SafwaAiFab(
                    onClick = { showAiChat = true }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onNavigateToProductDetails = onNavigateToProductDetails,
                    onNavigateToCart = onNavigateToCart,
                    onNavigateToCategories = onNavigateToCategories,
                    onNavigateToCategoryProducts = onNavigateToCategoryProducts,
                    onNavigateToBrands = onNavigateToBrands,
                    onNavigateToBrandProducts = onNavigateToBrandProducts
                )

                1 -> SearchScreen(
                    onNavigateToProductDetails = onNavigateToProductDetails
                )

                2 -> if (isAuthenticated) {
                    WishlistScreen(
                        onNavigateToProductDetails = onNavigateToProductDetails
                    )
                } else {
                    AuthGateContent(
                        title = stringResource(R.string.auth_gate_wishlist_title),
                        description = stringResource(R.string.auth_gate_wishlist_desc),
                        icon = Icons.Outlined.FavoriteBorder,
                        onSignIn = onNavigateToSignIn,
                        onCreateAccount = onNavigateToCreateAccount,
                    )
                }

                3 -> if (isAuthenticated) {
                    ProfileScreen(
                        onNavigateToOrderHistory = onNavigateToOrderHistory,
                        onNavigateToSavedAddresses = onNavigateToSavedAddresses,
                        onNavigateToLanguageAndCurrency = onNavigateToLanguageAndCurrency,
                        onNavigateToLogin = onNavigateToLogin,
                    )
                } else {
                    AuthGateContent(
                        title = stringResource(R.string.auth_gate_profile_title),
                        description = stringResource(R.string.auth_gate_profile_desc),
                        icon = Icons.Outlined.Person,
                        onSignIn = onNavigateToSignIn,
                        onCreateAccount = onNavigateToCreateAccount,
                    )
                }
            }
        }
    }

    if (showAiChat) {
        AiChatBottomSheet(
            state = aiChatState,
            onEvent = aiChatViewModel::onEvent,
            onDismiss = { showAiChat = false }
        )
    }
}

