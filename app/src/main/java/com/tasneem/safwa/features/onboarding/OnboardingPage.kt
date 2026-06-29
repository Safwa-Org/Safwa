package com.tasneem.safwa.features.onboarding

import com.tasneem.safwa.R

data class OnboardingPage(
    val tagRes: Int,
    val titleRes: Int,
    val descriptionRes: Int,
    val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        tagRes = R.string.onboarding_welcome_title,
        titleRes = R.string.onboarding_welcome_heading,
        descriptionRes = R.string.onboarding_welcome_desc,
        imageRes = R.drawable.onboarding_1
    ),
    OnboardingPage(
        tagRes = R.string.onboarding_collection_title,
        titleRes = R.string.onboarding_collection_heading,
        descriptionRes = R.string.onboarding_collection_desc,
        imageRes = R.drawable.onboarding_2
    ),
    OnboardingPage(
        tagRes = R.string.onboarding_delivery_title,
        titleRes = R.string.onboarding_delivery_heading,
        descriptionRes = R.string.onboarding_delivery_desc,
        imageRes = R.drawable.onboarding_3
    )

)
