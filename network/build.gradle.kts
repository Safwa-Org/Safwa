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

val countriesApiKey = localProperties.getProperty("COUNTRIES_API_KEY")
    ?: throw Exception("Missing COUNTRIES_API_KEY in local.properties.")

val countriesBaseUrl = localProperties.getProperty("COUNTRIES_BASE_URL")
    ?: throw Exception("Missing COUNTRIES_BASE_URL in local.properties.")

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

        buildConfigField("String", "COUNTRIES_API_KEY", "\"$countriesApiKey\"")
        buildConfigField("String", "COUNTRIES_BASE_URL", "\"$countriesBaseUrl\"")
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