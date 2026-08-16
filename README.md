# ReceiptAI — Expense & Budget Tracker

ReceiptAI is a modern, offline-first Android app for personal finance management. It combines classic expense tracking with a polished, animated Material 3 experience — all data stays on the device.

> AI receipt scanning (ML Kit Document Scanner + Gemini API) is planned as the next milestone. The current release covers the full manual tracking experience.

## Features

**Dashboard & Analytics**
- Total balance and current-month spending overview with animated donut chart
- Spending breakdown by category with percentages
- Recent transactions timeline
- Dedicated analytics screen with monthly insights and transaction counts

**Transaction Management (CRUD)**
- Add, edit, and delete expenses and income
- Search and filter transaction history by type and time range
- Automatic grouping by day (Today / Yesterday / This Week / Older)
- Multi-currency input with offline reference conversion rates

**Security**
- App Lock with a 4-digit PIN (stored as a salted PBKDF2 hash, never in plaintext)
- Biometric unlock (fingerprint / face) via AndroidX BiometricPrompt, with PIN fallback
- Configurable re-lock timeout (5 minutes in background, always on cold start)
- CSV export neutralizes spreadsheet formula injection

**Customization**
- Light / dark / system theme with dedicated night launch theme
- Display currency selection (USD, EUR, GBP, PLN, CAD, AUD, JPY)
- Five languages: English, Ukrainian, Polish, German, Spanish — with Android 13+ per-app language support

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3, custom theming with design tokens |
| Architecture | Clean Architecture, MVI (Unidirectional Data Flow) |
| Persistence | Room (SQLite), Preferences DataStore |
| DI | Hilt |
| Async | Coroutines & Flow |
| Security | AndroidX Biometric, PBKDF2WithHmacSHA256 |
| Build | Gradle (Kotlin DSL), R8 minification + resource shrinking |

## Architecture

The app follows Clean Architecture with a strict dependency rule: `presentation → domain ← data`. Presentation never touches data implementations directly; everything flows through domain interfaces bound by Hilt.

```
app/src/main/java/com/receiptai/tracker/
├── MainActivity.kt              # Composition root: theming, locale, lock gate
├── ReceiptAIApplication.kt      # Hilt application entry point
├── di/                          # Hilt modules (database, repositories)
├── domain/
│   ├── model/                   # Expense, AppSettings — pure business models
│   ├── repository/              # ExpenseRepository, SettingsRepository interfaces
│   ├── money/                   # CurrencyConverter (minor units, BigDecimal)
│   └── security/                # PinHasher (salted PBKDF2)
├── data/
│   ├── local/                   # Room database, DAO, entity, migrations
│   ├── repository/              # ExpenseRepositoryImpl
│   └── settings/                # SettingsRepositoryImpl (DataStore + legacy migration)
├── presentation/
│   ├── MainViewModel.kt         # App-level state: settings, lock lifecycle
│   ├── dashboard/               # Home screen, MVI state/intents, ViewModel
│   ├── expense/                 # Add/edit transaction flow
│   ├── history/                 # Transaction history, details, filters
│   ├── analytics/               # Monthly analytics screen
│   ├── settings/                # Settings, PIN management, CSV export
│   ├── lock/                    # Lock screen, PIN pad, biometric prompt
│   ├── localization/            # Typed string-resources facade (5 locales)
│   ├── navigation/              # Bottom bar, section headers
│   └── components/              # Shared UI: dialogs, formatters, parsers
└── ui/theme/                    # Colors, gradients, typography, day/night tokens
```

**Key design decisions**

- **MVI state flow** — each screen exposes a single immutable `UiState` plus a sealed `Intent` hierarchy; state changes are pure functions of the previous state.
- **Money as minor units** — amounts are persisted as `Long` minor units (cents) and converted with `BigDecimal`, so no floating-point drift is possible.
- **Offline-first currency conversion** — deterministic reference rates power display conversion without network access; a remote rate provider can replace the converter without touching persistence or UI.
- **Hashed PIN at rest** — the PIN never leaves the device and is stored only as `PBKDF2WithHmacSHA256(password, random salt, 60k iterations)`; the DataStore file is excluded from cloud backups and device transfers.
- **Non-destructive Room migrations** — schema changes ship with explicit migrations; schemas are exported under `app/schemas/` for diff validation.

## Getting Started

**Requirements:** Android Studio (Ladybug or newer), JDK 11+, Android SDK with compileSdk 36.

```bash
# Clone
git clone <repository-url>
cd ReceiptAIExpenseBudgetTracker

# Debug build
./gradlew :app:assembleDebug

# Release build (R8-minified; debug-signed for local testing —
# replace signingConfig with your own keystore for store upload)
./gradlew :app:assembleRelease

# Unit tests
./gradlew :app:testDebugUnitTest

# Lint
./gradlew :app:lintDebug
```

Min SDK 24 (Android 7.0) · Target SDK 36 (Android 16).

## Data & Privacy

- All data lives locally: Room database for transactions, Preferences DataStore for settings.
- CSV export writes only to a user-chosen location via the system file picker.
- "Delete All Data" removes every stored transaction permanently.
- The PIN-bearing settings file is excluded from Android cloud backup and device-to-device transfer.

## Roadmap

- [ ] AI receipt scanner: ML Kit Document Scanner + Gemini API for amount, date, and merchant extraction
- [ ] Budgets per category with over-spend alerts
- [ ] Recurring transactions
- [ ] Widgets and quick-add shortcuts

## License

All rights reserved. This project is a portfolio piece and is not distributed under an open-source license.
