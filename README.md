<div align="center">

# ✨ Safwa

### Premium E-Commerce Shopping Experience

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Shopify](https://img.shields.io/badge/Shopify-Storefront_API-96BF48?style=for-the-badge&logo=shopify&logoColor=white)](https://shopify.dev/docs/api/storefront)
[![Firebase](https://img.shields.io/badge/Firebase-Auth_&_AppCheck-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com)
[![Cloudflare Workers](https://img.shields.io/badge/Cloudflare-Workers-F38020?style=for-the-badge&logo=cloudflare&logoColor=white)](https://workers.cloudflare.com)
[![Min SDK](https://img.shields.io/badge/Min_SDK-24-green?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target_SDK-36-blue?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)

<br/>

> **Safwa** is a modern, feature-rich Android e-commerce application built with **Jetpack Compose** and powered by **Shopify's Storefront & Admin GraphQL APIs**. It delivers a premium shopping experience with AI-powered recommendations, real-time currency conversion, and a secure checkout flow — all wrapped in a beautiful Material 3 design with full dark mode support.

<br/>

---

</div>

<br/>

## 📋 Table of Contents

- [✨ Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Tech Stack](#️-tech-stack)
- [📁 Project Structure](#-project-structure)
- [⚙️ Setup & Configuration](#️-setup--configuration)
- [🔒 Security Architecture](#-security-architecture)
- [🧪 Testing](#-testing)
- [📜 License](#-license)

<br/>

---

<br/>

## ✨ Features

<table>
<tr>
<td width="50%">

### 🛍️ Shopping Experience
- **Home Feed** — Personalized greeting, promo banners, categories, best sellers, brands & latest products
- **Product Browsing** — Browse by category, brand, or latest arrivals with rich product detail views
- **Smart Search** — Full-text product search powered by Shopify's search engine
- **Product Details** — Image galleries, variant selection, descriptions & reviews
- **Wishlist** — Save favorite products for later with local persistence

</td>
<td width="50%">

### 🤖 AI-Powered Features
- **AI Recommendations** — Gemini-powered personalized product suggestions on the home feed
- **AI Chat Assistant** — Conversational shopping assistant for product discovery & support
- **Smart Product Matching** — AI analyzes browsing patterns to surface relevant products

</td>
</tr>
<tr>
<td width="50%">

### 🛒 Cart & Checkout
- **Cart Management** — Add, update quantities, remove items with real-time price sync
- **Multiple Payment Methods** — Cash on Delivery (COD) & Shopify Checkout Sheet
- **Draft Order Flow** — Secure server-side order creation via Cloudflare Worker proxy
- **Order Confirmation** — Beautiful success/failure screens with order tracking
- **Discount Codes** — Apply promo codes directly at checkout

</td>
<td width="50%">

### ⚙️ Settings & Personalization
- **User Authentication** — Email/password & Google Sign-In via Firebase Auth
- **Order History** — View past orders with detailed breakdowns
- **Saved Addresses** — Full CRUD for shipping addresses via Shopify Customer API
- **Language & Currency** — Multi-currency support with live exchange rates
- **Location Detection** — Auto-detect user location via LocationIQ reverse geocoding
- **Onboarding** — Elegant first-launch walkthrough experience

</td>
</tr>
</table>

<br/>

### 🎨 Design Highlights

| Feature | Description |
|---|---|
| 🌗 **Dark Mode** | Full Material 3 light/dark theme with custom color palette |
| 📱 **Material 3** | Follows latest Material Design 3 guidelines |
| ✍️ **Custom Typography** | Google Fonts integration for premium feel |
| 🔄 **Pull-to-Refresh** | Native Material 3 pull-to-refresh on the home feed |
| 🎠 **Promo Banner Pager** | Auto-scrolling promotional banner carousel |
| 💫 **Splash Screen** | AndroidX Splash Screen API with smooth transition |

<br/>

---

<br/>

## 🏗️ Architecture

Safwa follows **Clean Architecture** principles with a clear separation of concerns across layers. Each feature module is self-contained with its own `data`, `domain`, `di`, and `presentation` layers.

```
┌──────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                         │
│                                                                  │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────────────────┐ │
│  │   Screens   │  │  ViewModels  │  │  State / Event / Effect │ │
│  │  (Compose)  │◄─┤  (Hilt DI)   │◄─┤      (MVI Pattern)      │ │
│  └─────────────┘  └──────┬───────┘  └─────────────────────────┘ │
├──────────────────────────┼───────────────────────────────────────┤
│                      DOMAIN LAYER                                │
│                          │                                       │
│  ┌──────────────┐  ┌─────▼──────┐  ┌──────────────────────────┐ │
│  │    Models     │  │  Use Cases │  │  Repository Interfaces   │ │
│  └──────────────┘  └──────┬─────┘  └──────────────────────────┘ │
├──────────────────────────┼───────────────────────────────────────┤
│                       DATA LAYER                                 │
│                          │                                       │
│  ┌──────────────┐  ┌─────▼───────┐  ┌─────────────────────────┐ │
│  │   Mappers    │  │ Repositories│  │    Data Sources          │ │
│  │ (DTO ↔ Model)│  │   (Impl)    │◄─┤  (Remote / Local / DB)  │ │
│  └──────────────┘  └─────────────┘  └────────────┬────────────┘ │
└──────────────────────────────────────────────────┼──────────────┘
                                                   │
          ┌────────────────────────────────────────┼──────────┐
          │              NETWORK MODULE                       │
          │                                                   │
          │  ┌────────────────┐  ┌──────────────────────────┐ │
          │  │  Apollo GraphQL│  │  Retrofit REST Clients   │ │
          │  │  (Storefront)  │  │  (Currency, Location,    │ │
          │  │                │  │   Admin Proxy)           │ │
          │  └────────┬───────┘  └────────────┬────────────┘ │
          └───────────┼───────────────────────┼──────────────┘
                      │                       │
        ┌─────────────▼───────┐   ┌───────────▼─────────────┐
        │  Shopify Storefront │   │  Cloudflare Worker      │
        │  GraphQL API        │   │  (Admin API Proxy)      │
        └─────────────────────┘   │                         │
                                  │  Firebase Auth + App    │
                                  │  Check verification     │
                                  └──────────┬──────────────┘
                                             │
                                  ┌──────────▼──────────────┐
                                  │  Shopify Admin          │
                                  │  GraphQL API            │
                                  └─────────────────────────┘
```

### MVI Pattern

Each feature follows the **Model-View-Intent** pattern:
- **State** — Immutable data class representing the current UI state
- **Event** — Sealed class representing user interactions
- **Effect** — One-shot side effects (navigation, toasts, etc.)
- **ViewModel** — Processes events, updates state, and emits effects

<br/>

---

<br/>

## 🛠️ Tech Stack

### Android & UI

| Technology | Purpose |
|---|---|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Declarative UI framework |
| **Material 3** | Design system & component library |
| **Compose Navigation** | Type-safe navigation with Kotlin Serialization |
| **Coil 3** | Async image loading with OkHttp integration |
| **AndroidX Paging 3** | Efficient large dataset pagination |
| **AndroidX SplashScreen** | Modern splash screen API |
| **DataStore Preferences** | Local key-value storage for settings |

### Networking & Data

| Technology | Purpose |
|---|---|
| **Apollo Kotlin** | Shopify Storefront GraphQL client with normalized caching |
| **Retrofit + Gson** | REST API calls (currency exchange, location, admin proxy) |
| **OkHttp** | HTTP client with logging interceptor |
| **Room** | Local SQLite database for offline data |

### Backend & Auth

| Technology | Purpose |
|---|---|
| **Firebase Auth** | User authentication (email/password + Google Sign-In) |
| **Firebase App Check** | Device attestation (Play Integrity / Debug) |
| **Firebase Firestore** | Cloud data storage |
| **Cloudflare Workers** | Serverless proxy for Shopify Admin API |
| **Shopify Checkout Sheet Kit** | Native checkout experience |

### AI

| Technology | Purpose |
|---|---|
| **Google Gemini AI** | Product recommendations & chat assistant |
| **OpenRouter API** | Alternative AI model access |

### Dependency Injection & Architecture

| Technology | Purpose |
|---|---|
| **Dagger Hilt** | Compile-time dependency injection |
| **KSP** | Kotlin Symbol Processing for code generation |
| **Kotlin Coroutines** | Asynchronous programming |
| **Kotlin Serialization** | Type-safe navigation arguments |

### Testing

| Technology | Purpose |
|---|---|
| **JUnit 5 (Jupiter)** | Unit testing framework |
| **MockK** | Kotlin-first mocking library |
| **Turbine** | Flow testing library |
| **Coroutines Test** | Coroutine testing utilities |
| **Espresso** | Android UI testing |
| **Apollo Testing Support** | GraphQL response mocking |

<br/>

---

<br/>

## 📁 Project Structure

The project is a multi-module Gradle build with three modules:

```
Safwa/
├── 📱 app/                              # Main Android application module
│   └── src/main/java/com/tasneem/safwa/
│       ├── SafwaApplication.kt          # Hilt Application + Firebase/Shopify init
│       │
│       ├── 🧱 core/                     # Shared app-level code
│       │   ├── ai/                      # AI services (Gemini integration)
│       │   │   ├── data/                # AI data sources & repos
│       │   │   ├── di/                  # AI Hilt modules
│       │   │   └── domain/              # AI use cases & models
│       │   ├── data/                    # Core data layer
│       │   │   ├── mapper/              # DTO ↔ Domain mappers
│       │   │   ├── model/               # Data transfer objects
│       │   │   ├── repository/          # Repository implementations
│       │   │   ├── source/              # Local/remote data sources
│       │   │   └── sync/                # Data synchronization
│       │   ├── di/                      # Core Hilt modules
│       │   │   ├── AppModule.kt
│       │   │   ├── CoreRepositoryModule.kt
│       │   │   ├── DataStoreModule.kt
│       │   │   └── PreferencesUseCaseModule.kt
│       │   ├── domain/                  # Core domain layer
│       │   │   ├── model/               # Domain models (User, AuthState, etc.)
│       │   │   ├── repository/          # Repository interfaces
│       │   │   └── usecase/             # Shared use cases
│       │   ├── dp/                      # Density-independent pixel utils
│       │   ├── exception/               # Custom exception types
│       │   ├── navigation/              # App navigation
│       │   │   ├── MainScreen.kt        # Bottom navigation host
│       │   │   ├── SafwaNavHost.kt      # Navigation graph
│       │   │   ├── ScreenRoute.kt       # Type-safe route definitions
│       │   │   └── StartDestination.kt  # Start destination logic
│       │   ├── presentation/            # Core UI components
│       │   │   ├── mainactivity/        # MainActivity + MainViewModel
│       │   │   ├── mapper/              # UI mappers
│       │   │   └── model/               # UI models
│       │   ├── shared_component/        # Reusable Compose components
│       │   │   ├── CustomButton.kt
│       │   │   ├── LoadingOverlay.kt
│       │   │   ├── SafwaConfirmDialog.kt
│       │   │   ├── SafwaLogo.kt
│       │   │   └── SafwaTopAppBar.kt
│       │   ├── theme/                   # Material 3 theme
│       │   │   ├── Color.kt
│       │   │   ├── Theme.kt
│       │   │   └── Typography.kt
│       │   └── util/                    # Utility functions
│       │
│       └── 🎯 features/                # Feature modules (Clean Architecture)
│           ├── aichat/                  # 🤖 AI Chat Assistant
│           ├── auth/                    # 🔐 Authentication (Login/Register)
│           ├── brand/                   # 🏷️  Brand browsing & filtering
│           ├── cart/                     # 🛒 Shopping cart management
│           ├── category/                # 📂 Category browsing
│           ├── checkout/                # 💳 Checkout flow
│           ├── core/                    # 🔧 Shared feature components
│           ├── home/                    # 🏠 Home feed
│           ├── latest_products/         # 🆕 Latest arrivals
│           ├── onboarding/              # 👋 First-launch onboarding
│           ├── payment/                 # 💰 Payment processing
│           ├── productdetails/          # 📦 Product detail view
│           ├── reviews/                 # ⭐ Product reviews
│           ├── search/                  # 🔍 Product search
│           ├── settings/                # ⚙️  Settings & profile
│           │   ├── core/                # Settings shared code
│           │   ├── languageandcurrency/ # 🌍 Language & currency selection
│           │   ├── orderhistory/        # 📋 Order history & details
│           │   ├── profile/             # 👤 User profile
│           │   └── savedaddresses/      # 📍 Saved addresses (CRUD)
│           └── wishlist/                # ❤️  Wishlist & favorites
│
├── 🌐 network/                          # Network/API module (Android Library)
│   └── src/
│       ├── main/
│       │   ├── graphql/                 # Shopify Storefront GraphQL
│       │   │   ├── schema.graphqls      # Full Storefront schema
│       │   │   ├── Products.graphql
│       │   │   ├── GetCart.graphql
│       │   │   ├── CreateCart.graphql
│       │   │   ├── AddToCart.graphql
│       │   │   ├── SearchProducts.graphql
│       │   │   ├── GetCategories.graphql
│       │   │   ├── GetOrders.graphql
│       │   │   ├── CustomerAddress.graphql
│       │   │   └── ... (19 operations)
│       │   └── java/com/tasneem/network/
│       │       ├── datasource/          # Remote data sources
│       │       │   ├── address/         # Customer address API
│       │       │   ├── auth/            # Shopify customer auth
│       │       │   ├── brand/           # Brand/vendor API
│       │       │   ├── cart/            # Cart operations
│       │       │   ├── checkout/        # Checkout API
│       │       │   ├── currency/        # Currency exchange API
│       │       │   ├── location/        # LocationIQ geocoding
│       │       │   ├── order/           # Order management
│       │       │   ├── payment/         # Payment processing
│       │       │   └── product/         # Product catalog
│       │       ├── di/                  # Network Hilt modules
│       │       ├── dto/                 # Data transfer objects
│       │       ├── exception/           # Network exception handling
│       │       ├── interceptor/         # OkHttp interceptors
│       │       ├── mapper/              # DTO mappers
│       │       └── model/               # Network models
│       └── test/                        # Network unit tests
│
├── ☁️ worker/                            # Cloudflare Worker (Admin API Proxy)
│   ├── src/
│   │   └── index.ts                     # Worker entry point
│   ├── package.json
│   ├── tsconfig.json
│   └── wrangler.jsonc                   # Wrangler deployment config
│
├── build.gradle.kts                     # Root build script
├── settings.gradle.kts                  # Module declarations
├── gradle.properties                    # Gradle configuration
└── countries.json                       # Country/currency reference data
```

<br/>

---

<br/>

## ⚙️ Setup & Configuration

### Prerequisites

- **Android Studio** Ladybug (2024.2+) or newer
- **JDK 17**
- **Android SDK 36** (API level 36)
- **Node.js 18+** & **npm** (for the Cloudflare Worker)
- A **Shopify Partner** store with Storefront API access
- A **Firebase** project with Auth, Firestore & App Check enabled
- A **Cloudflare** account (free tier works)

### 1. Clone the repository

```bash
git clone https://github.com/Safwa-Org/Safwa.git
cd Safwa
```

### 2. Configure `local.properties`

Create or update `local.properties` in the project root with the following keys:

```properties
# Android SDK (auto-set by Android Studio)
sdk.dir=C\:\\Users\\YourUser\\AppData\\Local\\Android\\Sdk

# Shopify Storefront API
STOREFRONT_TOKEN=your_storefront_access_token

# Shopify Admin Proxy (Cloudflare Worker URL)
ADMIN_PROXY_BASE_URL=https://your-worker.your-subdomain.workers.dev

# Currency Exchange API
Currency_Exchange_BASE_URL=https://your-currency-api-url/

# Location / Reverse Geocoding
Location_BASE_URL=https://us1.locationiq.com/
LOCATIONIQ_API_KEY=your_locationiq_api_key

# AI Services
GEMINI_API_KEY=your_gemini_api_key
OPEN_ROUTER_API_KEY=your_openrouter_api_key
```

### 3. Firebase setup

1. Add your `google-services.json` to the `app/` directory
2. Enable **Email/Password** and **Google Sign-In** providers in Firebase Auth
3. Enable **App Check** with Play Integrity (production) or Debug provider (development)
4. Set up **Firestore** database rules as needed

### 4. Deploy the Cloudflare Worker

```bash
cd worker
npm install
```

Set the Shopify Admin API token as a secret:

```bash
npx wrangler secret put SHOPIFY_ADMIN_API_ACCESS_TOKEN
```

Deploy to Cloudflare:

```bash
npm run deploy
```

> Update `ADMIN_PROXY_BASE_URL` in `local.properties` with the deployed worker URL.

### 5. Build & Run

```bash
./gradlew assembleDebug
```

Or simply open the project in Android Studio and click ▶️ **Run**.

<br/>

---

<br/>

## 🔒 Security Architecture

Safwa implements a **zero-trust security model** for Shopify Admin API access:

```
  📱 Android App                    ☁️ Cloudflare Worker               🛒 Shopify Admin
 ┌──────────────┐               ┌─────────────────────┐            ┌──────────────────┐
 │              │  Bearer Token  │                     │  Admin     │                  │
 │  Firebase    │───────────────►│  Verify Firebase    │  Token     │  Draft Order     │
 │  Auth        │  + App Check   │  ID Token (JWT)     │──────────►│  Create/Complete  │
 │              │  Header        │                     │            │                  │
 │  App Check   │               │  Verify App Check   │            │  Order Cancel    │
 │  (Play       │               │  Token (JWT)        │            │  Mark as Paid    │
 │  Integrity)  │               │                     │            │                  │
 └──────────────┘               └─────────────────────┘            └──────────────────┘
```

**Key security measures:**

| Layer | Protection |
|---|---|
| 🔑 **Firebase Auth** | Every request carries a signed JWT ID token verified via Google's public JWKS |
| 🛡️ **Firebase App Check** | Play Integrity attestation ensures requests come from genuine, unmodified app builds |
| ☁️ **Cloudflare Worker** | Admin API token **never** leaves the server — the app has zero access to it |
| 🔐 **Build Config** | All API keys and tokens are injected via `local.properties` (gitignored) |
| 📜 **JOSE (JWT)** | Server-side JWT verification using cached remote key sets |

<br/>

---

<br/>

## 🧪 Testing

The project uses **JUnit 5** (Jupiter) with **MockK** and **Turbine** for comprehensive unit testing.

### Run all tests

```bash
# App module tests
./gradlew :app:test

# Network module tests
./gradlew :network:test

# All tests
./gradlew test
```

### Testing tools

| Tool | Usage |
|---|---|
| **JUnit 5 Jupiter** | Test lifecycle, parameterized tests, assertions |
| **MockK** | Kotlin-idiomatic mocking (coEvery, verify, slot) |
| **Turbine** | Testing Kotlin Flows (awaitItem, awaitComplete) |
| **Coroutines Test** | `runTest`, `TestDispatcher`, `advanceUntilIdle` |
| **Apollo Testing** | Mock GraphQL responses for Storefront API calls |
| **Espresso** | Android instrumented UI tests |

<br/>

---

<br/>

## 🧩 GraphQL Operations

The network module contains **19 GraphQL operations** against Shopify's Storefront API:

| Operation | Type | Description |
|---|---|---|
| `Products` | Query | Fetch paginated product catalog |
| `GetProductByHandle` | Query | Get single product with variants & images |
| `SearchProducts` | Query | Full-text product search |
| `GetCategories` | Query | Fetch all collections/categories |
| `GetCollectionProducts` | Query | Products in a specific collection |
| `GetProductVendors` | Query | List all product brands/vendors |
| `GetProductsForVendor` | Query | Products filtered by vendor |
| `GetCart` | Query | Retrieve current cart contents |
| `CreateCart` | Mutation | Initialize a new shopping cart |
| `AddToCart` | Mutation | Add line items to cart |
| `UpdateCartLine` | Mutation | Update cart line quantity |
| `RemoveFromCart` | Mutation | Remove line items from cart |
| `ApplyDiscountCode` | Mutation | Apply discount/promo code |
| `CustomerAccessTokenCreate` | Mutation | Shopify customer login |
| `CustomerCreate` | Mutation | Register new customer |
| `CustomerUpdate` | Mutation | Update customer profile |
| `CustomerAddress` | Mutation | CRUD for shipping addresses |
| `GetOrders` | Query | Fetch customer order history |
| `CheckoutCompleteWithTokenizedPaymentV3` | Mutation | Complete tokenized payment |

<br/>

---

<br/>

## 👥 Team

### 🎓 Mentor

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Awatef97">
        <img src="https://github.com/Awatef97.png" width="120px" alt="Awatef"/>
        <br />
        <sub><b>Awatef</b></sub>
      </a>
      <br />
      <sub>Project Mentor</sub>
    </td>
  </tr>
</table>

### 👨‍💻 Developers

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Ashraf0Sherif">
        <img src="https://github.com/Ashraf0Sherif.png" width="120px" alt="Ashraf Sherif"/>
        <br />
        <sub><b>Ashraf Sherif</b></sub>
      </a>
      <br />
      <sub>Mobile App Developer</sub>
    </td>
    <td align="center">
      <a href="https://github.com/OsamaEmam314">
        <img src="https://github.com/OsamaEmam314.png" width="120px" alt="Osama Emam"/>
        <br />
        <sub><b>Osama Emam</b></sub>
      </a>
      <br />
      <sub>Mobile App Developer</sub>
    </td>
    <td align="center">
      <a href="https://github.com/tasneem-hakeem">
        <img src="https://github.com/tasneem-hakeem.png" width="120px" alt="Tasneem Hakeem"/>
        <br />
        <sub><b>Tasneem Hakeem</b></sub>
      </a>
      <br />
      <sub>Mobile App Developer</sub>
    </td>
    <td align="center">
      <a href="https://github.com/sherryahmos473">
        <img src="https://github.com/sherryahmos473.png" width="120px" alt="Sherry Ahmos"/>
        <br />
        <sub><b>Sherry Ahmos</b></sub>
      </a>
      <br />
      <sub>Mobile App Developer</sub>
    </td>
  </tr>
</table>

<br/>

---

<br/>

## 📜 License

This project is proprietary. All rights reserved.

<br/>

---

<div align="center">

**Built with ❤️ using Kotlin & Jetpack Compose**

<br/>

<sub>Powered by Shopify · Firebase · Cloudflare Workers · Gemini AI</sub>

</div>
