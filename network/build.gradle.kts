import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.apollo)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val storefrontToken: String = localProperties.getProperty("STOREFRONT_TOKEN") ?: ""

android {
    namespace = "com.tasneem.safwa.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
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
}