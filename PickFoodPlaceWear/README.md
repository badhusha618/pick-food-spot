# PickFoodPlace Wear OS

A Wear OS 3+ app built with Jetpack Compose for Wear, Hilt, Retrofit, Room, and Coroutines/Flow.

Note: Jetpack Compose and Kotlin Coroutines require Kotlin. The app is implemented in Kotlin with MVVM and Compose.

## Requirements
- Android Studio Arctic Fox or newer
- Wear OS emulator or device (API 30+)

## Features
- JWT login & registration
- View/search nearby places
- Place details & quick booking
- Booking confirmation with QR code
- Upcoming bookings & cancellation
- Push notifications for reminders (FCM) + local reminders fallback (WorkManager)
- Offline cache (Room)

## Getting Started
1. Open the project in Android Studio.
2. If prompted, let Android Studio generate/download the Gradle wrapper and sync the project.
3. Create a Wear OS emulator (API 30+).
4. Build and run the `app` module.

### Mock API
By default, `BuildConfig.USE_MOCK` is true. The app uses in-memory mock data and delayed responses. To use a real backend, set `USE_MOCK=false` and set `BASE_URL` in `app/build.gradle`.

### JWT Storage
Tokens are stored using `EncryptedSharedPreferences`.

### Token Refresh
401 responses are handled by an authenticator that calls `auth/refresh` (mocked when `USE_MOCK=true`).

### Notifications
- The app includes an `FirebaseMessagingService` implementation. If you want to use FCM, add your `google-services.json` and apply the `com.google.gms.google-services` plugin in `app/build.gradle`.
- Local reminders are scheduled with WorkManager when bookings are created.

## Modules/Packages
- `data.remote` - Retrofit API and interceptors
- `data.local` - Room database and encrypted prefs
- `repository` - Business repositories
- `ui.screens` - Compose screens
- `ui.viewmodel` - ViewModels
- `notifications` - FCM service and notification helpers
- `di` - Hilt modules
- `work` - WorkManager workers

## Tests
Run unit tests from Android Studio or with Gradle wrapper: `./gradlew test`

## License
MIT