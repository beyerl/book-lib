# L-Space

A local-only Android book library and reading-tracker app, themed after a sepia
library with an orangutan librarian mascot.

## Features
- **Home** — yearly reading-goal progress and a "currently reading" carousel.
- **Search** — search the open OpenLibrary catalogue (the same open source book
  data family BookWyrm uses) and add books to a shelf, or create a book from scratch.
- **Library** — shelves (Reading List, Now Reading, Finished Reading, Stopped Reading)
  used as filters over a paged list with a configurable page size.
- **Book** — detailed view with a 1–5 star rating, an edit mode, and read date ranges.
- **Goals / Achievements** — set a per-year target and browse "The year &lt;yyyy&gt; in Books".
- **Import / Export** — Goodreads-compatible CSV and Markdown export; Goodreads CSV import.

## Tech
- Kotlin, Jetpack Compose, Material 3 (sepia theme)
- Room (local persistence), Retrofit + kotlinx.serialization (OpenLibrary), Coil
- Manual DI container; MVVM with ViewModels and a single-Activity Navigation-Compose graph

## Building
Requires JDK 17 and the Android SDK.

```
./gradlew test            # unit tests
./gradlew assembleDebug   # debug APK -> app/build/outputs/apk/debug/
```

CI (GitHub Actions) builds and tests on every push. Pushing a `v*` tag builds an
unsigned release APK and attaches it to a GitHub Release.
