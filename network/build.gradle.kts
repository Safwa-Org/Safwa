import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.apollo)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

val storefrontToken = localProperties.getProperty("STOREFRONT_TOKEN")
    ?: throw Exception(
        "Missing STOREFRONT_TOKEN in local.properties.\n" +
                "Please add:\n" +
                "STOREFRONT_TOKEN=your_storefront_access_token"
    )


val currencyBaseUrl = localProperties.getProperty("Currency_Exchange_BASE_URL")
    ?: throw Exception("Missing Currency_Exchange_BASE_URL in local.properties.")

val locationBaseUrl = localProperties.getProperty("Location_BASE_URL")
    ?: throw Exception("Missing Location_BASE_URL in local.properties.")

val locationApiKey = localProperties.getProperty("LOCATIONIQ_API_KEY")
    ?: throw Exception("Missing LOCATIONIQ_API_KEY in local.properties.")


val adminProxyBaseUrl = localProperties.getProperty("ADMIN_PROXY_BASE_URL")
    ?: throw Exception(
        "Missing ADMIN_PROXY_BASE_URL in local.properties.\n" +
                "Please add the deployed Cloudflare Worker URL\n"
    )

android {
    namespace = "com.tasneem.safwa.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        buildConfigField("String", "STOREFRONT_TOKEN", "\"$storefrontToken\"")
        buildConfigField(
            "String", "SHOPIFY_ENDPOINT",
            "\"https://mad46-and10.myshopify.com/api/2025-04/graphql.json\""
        )

        buildConfigField("String", "COUNTRIES_BASE_URL", "\"$currencyBaseUrl\"")
        buildConfigField("String", "SHOPIFY_DEPOSIT_BASE_URL", "\"https://elb.deposit.shopifycs.com/\"")
        buildConfigField("String", "Currency_Exchange_BASE_URL", "\"$currencyBaseUrl\"")
        buildConfigField("String", "Location_BASE_URL", "\"$locationBaseUrl\"")
        buildConfigField("String", "LOCATIONIQ_API_KEY", "\"$locationApiKey\"")
        // Proxy URL for Shopify Admin API (admin token is kept server-side).
        buildConfigField("String", "ADMIN_PROXY_BASE_URL", "\"$adminProxyBaseUrl\"")
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

apollo {
    service("service") {
        packageName.set("com.tasneem.safwa.network")
        introspection {
            endpointUrl.set("https://mad46-and10.myshopify.com/api/2025-04/graphql.json")
            headers.put("X-Shopify-Storefront-Access-Token", storefrontToken)
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}

dependencies {
    api(libs.apollo.runtime)
    api(libs.apollo.normalized.cache)
    api(libs.apollo.normalized.cache.sqlite)
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    ksp(libs.hilt.android.compiler)

    // Firebase Auth (ID token) + App Check (attestation token) are attached to
    // every call to the admin proxy Worker so it can verify the caller
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.appcheck)
    implementation(libs.kotlinx.coroutines.play.services)

    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.apollo.testing.support)
}