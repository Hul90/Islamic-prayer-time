# Sultan Islamic Prayer - Requested Modifications

Implemented:
- Dark-theme foreground/text contrast improved; primary foreground is now warm yellow/gold while the existing dark background remains.
- Fixed BroadcastReceiver background coroutine lifetime with `goAsync()`.
- Fixed prayer alarm scheduling timezone comparison.
- Added exact-alarm permission check with graceful inexact fallback.
- Fixed Azan stop behavior to stop the foreground service directly.
- Removed synthesized/fake Azan tone and notification ringtone fallback.
- Prayer notification channels now have no notification sound, so they do not compete with Azan audio.
- Added **All Duas & Zikr — Contents** inside Sultan Tools. It opens the full Dua & Zikr index from `dua.gtaf.org`, with Bangla/English site selection.
- Added **AL QURAN** inside Sultan Tools. It opens the Quran index from `hadithbd.com/quran/`, including Arabic and Bangla translation/reading options available on the source.
- Existing prayer, calendar, Qibla, Tasbih, tracker, converter, Zakat, Ramadan and settings features remain in place.

## Azan MP3

Place the user's MP3 file at:

`app/src/main/res/raw/azan.mp3`

The service is already wired to play this file at enabled prayer times. Until the MP3 is supplied, the app intentionally does **not** synthesize a tone or play a system ringtone as a fake Azan.

## Final Azan MP3 integration
- Added `azan_001.mp3` for Dhuhr, Asr, Maghrib and Isha.
- Added `azan_002.mp3` for Fajr.
- `AzanAudioService` selects the correct bundled MP3 from the prayer type.
- Removed any synthesized/fallback Azan tone behavior.
- Existing prayer-specific/global Azan settings remain active.

## Build fix after CI compile error
- Fixed `IslamicWebViewScreen.kt`: removed invalid nullable assignments to `webViewClient` and `webChromeClient` during disposal. Android WebView's Kotlin API exposes these properties as non-null, so assigning `null` caused `compileDebugKotlin` to fail.
- WebView cleanup now stops loading and calls `destroy()` without assigning null to non-null WebViewClient/WebChromeClient properties.
