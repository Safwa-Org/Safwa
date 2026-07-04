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


val countriesBaseUrl = localProperties.getProperty("Countries_BASE_URL")
    ?: throw Exception("Missing Countries_BASE_URL in local.properties.")

val countriesApiKey = localProperties.getProperty("RESTCOUNTRIES_API_KEY")
    ?: throw Exception("Missing RESTCOUNTRIES_API_KEY in local.properties.")

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

        buildConfigField("String", "Currency_Exchange_BASE_URL", "\"$currencyBaseUrl\"")
        buildConfigField("String", "Location_BASE_URL", "\"$locationBaseUrl\"")
        buildConfigField("String", "LOCATIONIQ_API_KEY", "\"$locationApiKey\"")
        buildConfigField("String", "Countries_BASE_URL", "\"$countriesBaseUrl\"")
        buildConfigField("String", "RESTCOUNTRIES_API_KEY", "\"$countriesApiKey\"")


    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    implementation(libs.apollo.runtime)
    implementation(libs.apollo.normalized.cache.sqlite)
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    ksp(libs.hilt.android.compiler)
}