# Build & Sign Context

## Build Environment
- **Java Version**: OpenJDK 17.0.16 (Homebrew)
- **Gradle Version**: 8.13
- **Kotlin Version**: 2.0.21
- **Platform**: macOS 14.5 (aarch64)

## Android Configuration
- **Compile SDK**: 36
- **Target SDK**: 36
- **Min SDK**: 33
- **Application ID**: com.jazz13.appkiller
- **Version Code**: 1
- **Version Name**: 1.0

## Build Commands

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Clean Build
```bash
./gradlew clean assembleDebug
```

### Build and Verify
Builds the app, verifies no errors, and automatically resolves any build issues:
```bash
./gradlew clean assembleDebug --continue
```

### Build and Publish
Builds and verifies the app, autofixes errors, then copies versioned APK to ~/Downloads (debug signed for any Android device):
```bash
./gradlew clean assembleDebug && cp app/build/outputs/apk/debug/app-debug.apk ~/Downloads/AppKiller-v$(grep 'versionName' app/build.gradle.kts | cut -d'"' -f2)-debug.apk
```

## Version Management Rules
- **Always increment patch version before export**
- **Last delivered version**: 1.0.11
- **Next version should be**: 1.0.12
- **Format**: MAJOR.MINOR.PATCH (semantic versioning)

## Build Output
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`

## Requirements
- Java 17+ (OpenJDK recommended)
- Android SDK with API level 36
- Gradle 8.13+

## Current Status
- ✅ Debug build successful
- ⚠️ Deprecation warnings (non-blocking)
- 🔄 Release signing not configured

## Notes
- Build tested on macOS ARM64
- Uses Kotlin serialization plugin
- Jetpack Compose enabled
