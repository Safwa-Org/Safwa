package com.tasneem.safwa.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
@Composable
fun SafwaNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Splash
    ) {

        composable<ScreenRoute.Splash> {

        }

        composable<ScreenRoute.Onboarding> {
        }

        composable<ScreenRoute.Login> {
        }

        composable<ScreenRoute.Register> {
        }

        composable<ScreenRoute.ForgotPassword> {
        }

        composable<ScreenRoute.Home> {
        }

        composable<ScreenRoute.Search> {
        }

        composable<ScreenRoute.Wishlist> {
        }

        composable<ScreenRoute.Profile> {
        }

        composable<ScreenRoute.ProductDetails> {
        }

        composable<ScreenRoute.Cart> {
        }

        composable<ScreenRoute.Checkout> {
        }

        composable<ScreenRoute.OrderConfirmation> {
        }

        composable<ScreenRoute.OrderHistory> {
        }

        composable<ScreenRoute.SavedAddresses> {
        }

        composable<ScreenRoute.PaymentMethods> {
        }

        composable<ScreenRoute.Notifications> {
        }

        composable<ScreenRoute.Language> {
        }
    }
}