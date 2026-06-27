package com.tasneem.safwa.core.models

import com.tasneem.safwa.R

data class OnboardingPage(
    val tag: String,
    val title: String,
    val description: String,
    val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        tag = "WELCOME TO SAFWA",
        title = "Curated luxury,\nquietly delivered.",
        description = "A hand-picked selection of fragrances, leather, and timepieces from ateliers we trust.",
        imageRes = R.drawable.onboarding_1
    ),
    OnboardingPage(
        tag = "EXCLUSIVE COLLECTION",
        title = "Discover the\nfinest craft.",
        description = "Explore our exclusive collections crafted with precision and passion.",
        imageRes = R.drawable.onboarding_2
    ),
    OnboardingPage(
        tag = "FAST DELIVERY",
        title = "Swift and secure\nto your door.",
        description = "Enjoy peace of mind with our secure and fast delivery services.",
        imageRes = R.drawable.onboarding_3
    )
)
