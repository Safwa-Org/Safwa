package com.tasneem.safwa.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tasneem.safwa.core.domain.model.AuthState
import com.tasneem.safwa.features.auth.presentation.login.view.LoginScreen
import com.tasneem.safwa.features.auth.presentation.register.view.RegisterScreen
import com.tasneem.safwa.features.cart.presentation.view.CartScreen
import com.tasneem.safwa.features.onboarding.OnboardingScreen
import com.tasneem.safwa.features.payment.presentation.view.PaymentScreen
import com.tasneem.safwa.features.productdetails.presentation.view.ProductDetailsScreen
import com.tasneem.safwa.features.search.presentation.SearchScreen
import com.tasneem.safwa.features.settings.languageandcurrency.presentation.presentation.LanguageAndCurrencyScreen
import com.tasneem.safwa.features.settings.orderhistory.presentation.view.OrderHistoryScreen
import com.tasneem.safwa.features.settings.savedaddresses.presentation.view.SavedAddressesScreen
import com.tasneem.safwa.features.wishlist.presentation.WishlistScreen

@Composable
fun SafwaNavHost(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val authState by appViewModel.authState.collectAsState()
    val isAuthenticated = authState is AuthState.Authenticated

    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Splash,
    ) {

        composable<ScreenRoute.Splash> {
            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Authenticated -> navController.navigate(ScreenRoute.Home) {
                        popUpTo(ScreenRoute.Splash) { inclusive = true }
                    }

                    is AuthState.Unauthenticated -> navController.navigate(ScreenRoute.Onboarding) {
                        popUpTo(ScreenRoute.Splash) { inclusive = true }
                    }

                    AuthState.Loading -> Unit
                }
            }
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
                    val popped = navController.popBackStack(ScreenRoute.Home, inclusive = false)
                    if (!popped) {
                        navController.navigate(ScreenRoute.Home) {
                            popUpTo(0) { inclusive = true }
                        }
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
                    val popped = navController.popBackStack(ScreenRoute.Home, inclusive = false)
                    if (!popped) {
                        navController.navigate(ScreenRoute.Home) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<ScreenRoute.ForgotPassword> {
            // Forgot Password implementation
        }

        composable<ScreenRoute.Home> {
            MainScreen(
                isAuthenticated = isAuthenticated,
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
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(ScreenRoute.Login)
                },
                onNavigateToCreateAccount = {
                    navController.navigate(ScreenRoute.Register)
                },
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

        composable<ScreenRoute.Payment> {
            PaymentScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(ScreenRoute.Home) {
                        popUpTo(ScreenRoute.Checkout) { inclusive = true }
                    }
                }
            )
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
                // Assuming you add an onNavigateBack callback to this screen
                // onNavigateBack = { navController.popBackStack() }
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
