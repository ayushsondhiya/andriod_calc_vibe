# Android Calculator (Java)

This is a minimal native Android app port of your Java Swing calculator. It uses the same expression evaluator logic (shunting-yard + BigDecimal) and provides a basic calculator UI.

## How to build an APK

1. Install Android Studio (recommended) or Android SDK + Gradle on Windows.
2. Open this folder (`android-calculator`) in Android Studio and let it sync/install SDK components.
3. Build a debug APK from Android Studio: "Build > Build Bundle(s) / APK(s) > Build APK(s)".

Or, to build from command line (requires Android SDK and Gradle):

- From project root:
  - `gradle wrapper` (if you don't have a gradle wrapper)
  - `./gradlew assembleDebug` (on Windows use `gradlew.bat assembleDebug`)

## Note
I detected no Android SDK or Gradle on your PATH, so I didn't attempt an automated build. If you want, I can try to run a build now — install Android SDK/Android Studio first or tell me to proceed and I will try and report errors.