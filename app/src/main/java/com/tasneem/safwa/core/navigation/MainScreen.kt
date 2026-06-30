package com.tasneem.safwa.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tasneem.safwa.features.profile.presentation.view.languageandcurrency.LanguageAndCurrencyScreen
import com.tasneem.safwa.features.profile.presentation.view.savedaddresses.SavedAddressesScreen

@Composable
fun MainScreen(
    onNavigateToProductDetails: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        /*bottomBar = {
            HomeBottomBar(
                selectedIndex = selectedTab,
                onItemSelected = { index -> selectedTab = index }
            )
        }*/
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
          /*  when (selectedTab) {
                0 -> HomeScreen(
                    onNavigateToProductDetails = onNavigateToProductDetails,
                    onNavigateToCart = onNavigateToCart
                )
                1 -> SearchScreen()
                2 -> WishlistScreen()
                3 -> ProfileScreen()
            }*/
            SavedAddressesScreen()
        }
    }
}
