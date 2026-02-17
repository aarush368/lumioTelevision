# Lumio Television – News Headlines App

Android TV app that displays news headlines with **long-press DPAD-Down to refresh**. Built with Kotlin, Jetpack Compose for TV, and Retrofit.

## Features

- **News API integration** – Fetches top headlines from [NewsAPI.org](https://newsapi.org/) (US, 30 articles).
- **Retrofit** – All network calls use Retrofit with OkHttp; errors and timeouts are handled.
- **Headlines + summaries** – Each item shows headline and a short summary; **press OK (DPAD center)** on an item to expand and show the full summary.
- **TV-optimized** – Layout and typography tuned for 1080p TV viewport; focusable list for D-pad navigation.
- **Long-press DPAD-Down to refresh** – Hold **DPAD Down** for about 0.8 seconds to refresh headlines. A “Refreshing…” overlay is shown while loading.
- **Article images** – Thumbnail images loaded with [Coil](https://coil-kt.github.io/coil/) (memory cache, crossfade).
- **Offline caching** – Last successful result is kept in memory; if a refresh fails (e.g. no network), the previous list is still shown with an error message.

## Requirements

- Android SDK 24+ (API 24+)
- Android TV or emulator with TV support
- NewsAPI.org API key (free at [newsapi.org/register](https://newsapi.org/register))

## Setup and run

### 1. Get a News API key

1. Go to [https://newsapi.org/register](https://newsapi.org/register).
2. Register and copy your API key.

### 2. Configure the API key

Use **one** of these options:

**Option A – `local.properties` (recommended, not committed to git)**

In the project root, open or create `local.properties` and add:

```properties
NEWS_API_KEY=your_api_key_here
```

**Option B – `gradle.properties`**

In the project root, open `gradle.properties` and set:

```properties
NEWS_API_KEY=your_api_key_here
```

### 3. Build and run

- Open the project in Android Studio.
- Build: **Build → Make Project** or run `./gradlew assembleDebug`.
- Run on a TV device or **TV emulator** (AVD with a TV profile).  
  The app appears in the launcher as **Lumio Televison** (or the name set in `strings.xml`).

## Usage

- **Navigate** – DPAD Up/Down to move between headlines.
- **Expand summary** – Focus an item and press **DPAD Center (OK)** to expand/collapse the full summary.
- **Refresh** – **Long-press DPAD Down** (~0.8 s) to fetch new headlines. A “Refreshing…” indicator is shown; the list updates when the request completes.

## Project structure

```
app/src/main/java/com/lumio/lumiotelevison/
├── MainActivity.kt              # Long-press DPAD-Down handled here
├── data/
│   ├── model/NewsArticle.kt     # Domain model
│   ├── remote/                  # Retrofit API, DTOs, OkHttp client
│   └── repository/NewsRepository.kt  # Fetch + in-memory cache
├── ui/
│   ├── NewsViewModel.kt         # UI state, load/refresh
│   ├── components/NewsItem.kt   # Single row: image, headline, expandable summary
│   └── screens/NewsScreen.kt    # List, loading/error, refresh hint
└── ui/theme/                    # TV Material3 theme
```

## Key implementation details

- **Refresh trigger** – `MainActivity.dispatchKeyEvent()` starts a 800 ms timer on `KEYCODE_DPAD_DOWN` down; if the key is still down when the timer fires, `NewsViewModel.loadHeadlines()` is called. On key up, the timer is cancelled so a short press only moves focus.
- **Errors** – Missing API key, HTTP errors, and network failures are surfaced in the UI; the last successful list is retained when a refresh fails.
- **Images** – Coil’s `AsyncImage` with a fixed size and `ContentScale.Crop`; Coil’s default memory cache is used.

## Screenshots and video

For submission, include:

1. Screenshot of the app showing the headline list.
2. Screenshot or frame showing the “Refreshing…” state (during long-press DPAD-Down).
3. Short video: navigate list → long-press DPAD-Down → refreshed headlines.

## License

This project is for assignment/portfolio use. NewsAPI.org terms apply to the data.
# lumioTelevision
