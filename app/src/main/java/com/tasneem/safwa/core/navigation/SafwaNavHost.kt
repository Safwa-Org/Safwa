package com.tasneem.safwa.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tasneem.safwa.features.auth.presentation.login.view.LoginScreen
import com.tasneem.safwa.features.auth.presentation.register.view.RegisterScreen
import com.tasneem.safwa.features.wishlist.presentation.WishlistScreen
import com.tasneem.safwa.features.onboarding.OnboardingScreen
import com.tasneem.safwa.features.productdetails.presentation.view.ProductDetailsScreen
import com.tasneem.safwa.features.search.presentation.SearchScreen
import com.tasneem.safwa.features.settings.languageandcurrency.presentation.presentation.LanguageAndCurrencyScreen
import com.tasneem.safwa.features.settings.orderhistory.presentation.view.OrderHistoryScreen
import com.tasneem.safwa.features.settings.savedaddresses.presentation.view.SavedAddressesScreen
import com.tasneem.safwa.features.cart.presentation.view.CartScreen
import com.tasneem.safwa.features.wishlist.presentation.WishlistScreen

@Composable
fun SafwaNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Onboarding
    ) {

        composable<ScreenRoute.Splash> {
            // Splash implementation
        }

        composable<ScreenRoute.Onboarding> {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(ScreenRoute.Login) {
                        popUpTo(ScreenRoute.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<ScreenRoute.Login> {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(ScreenRoute.Home) {
                        popUpTo(ScreenRoute.Login) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(ScreenRoute.Register)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(ScreenRoute.ForgotPassword)
                }
            )
        }

        composable<ScreenRoute.Register> {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(ScreenRoute.Login) {
                        popUpTo(ScreenRoute.Register) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(ScreenRoute.Home) {
                        popUpTo(ScreenRoute.Register) { inclusive = true }
                    }
                }
            )
        }

        composable<ScreenRoute.ForgotPassword> {
            // Forgot Password implementation
        }

        composable<ScreenRoute.Home> {
            MainScreen(
                onNavigateToProductDetails = {
                    // navController.navigate(ScreenRoute.ProductDetails)
                },
                onNavigateToCart = {
                    navController.navigate(ScreenRoute.Cart)
                },
                onNavigateToOrderHistory = {
                    navController.navigate(ScreenRoute.OrderHistory)
                },
                onNavigateToSavedAddresses = {
                    navController.navigate(ScreenRoute.SavedAddresses)
                },
                onNavigateToLanguageAndCurrency = {
                    navController.navigate(ScreenRoute.Language)
                },
                onNavigateToLogin = {
                    navController.navigate(ScreenRoute.Login) {
                        // Clear the entire backstack when logging out
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<ScreenRoute.Search> {
            SearchScreen()
        }

        composable<ScreenRoute.Wishlist> {
            WishlistScreen()
        }

        composable<ScreenRoute.Profile> {
            // Handled inside MainScreen bottom nav
        }

        composable<ScreenRoute.ProductDetails> {
            ProductDetailsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<ScreenRoute.Cart> {
            // Cart implementation
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { navController.navigate(ScreenRoute.Checkout) }
            )
        }

        composable<ScreenRoute.Checkout> {
            // Checkout implementation
        }

        composable<ScreenRoute.OrderConfirmation> {
            // Order Confirmation implementation
        }

        composable<ScreenRoute.OrderHistory> {
            OrderHistoryScreen(
                // Assuming you add an onNavigateBack callback to this screen
                // onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<ScreenRoute.SavedAddresses> {
            SavedAddressesScreen(
                 onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<ScreenRoute.PaymentMethods> {
            // Payment Methods implementation
        }

        composable<ScreenRoute.Notifications> {
            // Notifications implementation
        }

        composable<ScreenRoute.Language> {
            LanguageAndCurrencyScreen(
                // Assuming you add an onNavigateBack callback to this screen
                // onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}