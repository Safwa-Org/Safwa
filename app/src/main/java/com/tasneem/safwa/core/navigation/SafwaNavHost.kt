package com.tasneem.safwa.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tasneem.safwa.core.domain.model.AuthState
import com.tasneem.safwa.core.presentation.mainactivity.viewmodel.MainViewModel
import com.tasneem.safwa.features.auth.presentation.login.view.LoginScreen
import com.tasneem.safwa.features.auth.presentation.register.view.RegisterScreen
import com.tasneem.safwa.features.cart.presentation.view.CartScreen
import com.tasneem.safwa.features.category.presentation.categories.CategoriesScreen
import com.tasneem.safwa.features.category.presentation.category_products.CategoryProductsScreen
import com.tasneem.safwa.features.checkout.presentation.view.CheckoutScreen
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
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val authState by mainViewModel.authState.collectAsState()
    val isAuthenticated = authState is AuthState.Authenticated
    val appStartDestination by mainViewModel.startDestination.collectAsStateWithLifecycle()

    val navStartRoute: Any = when (appStartDestination) {
        StartDestination.Home -> ScreenRoute.Home
        StartDestination.Onboarding -> ScreenRoute.Onboarding
        StartDestination.Loading -> return
    }

    NavHost(
        navController = navController,
        startDestination = navStartRoute,
    ) {

        composable<ScreenRoute.Onboarding> {
            OnboardingScreen(
                onFinish = {
                    mainViewModel.markOnboardingCompleted()
                    navController.navigate(ScreenRoute.Home) {
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
                onNavigateToProductDetails = { handle ->
                    navController.navigate(ScreenRoute.ProductDetails(handle))
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
                onNavigateToCategories = {
                    navController.navigate(ScreenRoute.Categories)
                },
                onNavigateToCategoryProducts = { categoryName ->
                    navController.navigate(ScreenRoute.CategoryProducts(categoryName))
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
                onNavigateToCheckout = { navController.navigate(ScreenRoute.Payment) }
            )
        }

        composable<ScreenRoute.Checkout> {
            CheckoutScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSavedAddresses = { navController.navigate(ScreenRoute.SavedAddresses) },
                onNavigateToPayment = { navController.navigate(ScreenRoute.Payment) },
            )
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

        composable<ScreenRoute.Categories> {
            CategoriesScreen(
                onNavigateBack = { navController.popBackStack() },
                onCategoryClicked = { categoryName ->
                    navController.navigate(ScreenRoute.CategoryProducts(categoryName))
                }
            )
        }

        composable<ScreenRoute.CategoryProducts> { backStackEntry ->
            CategoryProductsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProductDetails = { handle ->
                    navController.navigate(ScreenRoute.ProductDetails(handle))
                }
            )
        }
    }
}
