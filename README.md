# AppKiller 🔥

A powerful Android application for managing and terminating running apps with comprehensive logging capabilities.

> Vibe coded. Obviously.

## 🚀 Features

- **Dual Kill Modes**: Auto (programmatic) and Manual (Android settings)
- **Smart App Detection**: Lists all installed apps with running status indicators
- **High-Definition Icons**: Crystal clear app icons with automatic density optimization
- **Comprehensive Logging**: Track every app kill with date, time, and mode
- **Custom Splash Screen**: Branded startup experience with designer credit
- **Persistent Settings**: Configuration survives app restarts
- **Modern UI**: Material 3 design with dark theme and smooth animations

## 📱 Screenshots

### Main Interface
- Clean app list with swipe-to-kill functionality
- Toggle between Auto and Manual kill modes
- Real-time running status indicators

### Kill Logs
- Detailed history of all app terminations
- Three-column layout: Date/Time | App Name | Kill Mode
- Color-coded modes (Auto: Green, Manual: Orange)

## 🛠️ Technical Details

### Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose
- **Language**: Kotlin
- **Storage**: SharedPreferences with JSON serialization
- **Theme**: Material 3 Dark Theme

### Key Components
- `MainActivity.kt` - Main UI and navigation
- `AppManager.kt` - Core app management logic
- `AppSettings.kt` - Configuration and logging
- `LogScreen.kt` - Kill history viewer
- `SplashActivity.kt` - Custom splash screen

### Dependencies
```kotlin
// Core Android & Compose
androidx.core:core-ktx
androidx.activity:activity-compose
androidx.compose.material3:material3

// Additional Features
androidx.core:core-splashscreen
kotlinx-serialization-json
accompanist-swiperefresh
```

## 🔧 Installation & Setup

### Requirements
- Android 7.0+ (API level 24+)
- ~25MB storage space

### Installation Steps
1. Download `AppKiller-v2.5-refactored-final.apk`
2. Enable "Install from Unknown Sources" if needed
3. Install the APK
4. Grant requested permissions for full functionality

### Permissions
- **Usage Access** - For detecting running apps (Required)
- **Device Admin** - For enhanced termination (Optional)
- **Kill Background Processes** - Standard Android permission

## 🎯 Usage

### Quick Start
1. **Launch** the app
2. **Grant permissions** when prompted
3. **Swipe left** on any app to reveal kill button
4. **Tap kill button** to terminate the app
5. **View logs** by tapping the list icon (📋)

### Kill Modes
- **Auto Mode**: Programmatic termination using multiple strategies
- **Manual Mode**: Opens Android settings for guaranteed force stop

### Logging
- Every app kill is automatically logged
- View detailed history with date, time, app name, and kill mode
- Automatic cleanup keeps newest 128 entries

## 🏗️ Development

### Project Structure
```
app/src/main/
├── java/com/jazz13/appkiller/
│   ├── MainActivity.kt
│   ├── SplashActivity.kt
│   ├── data/
│   ├── settings/
│   ├── system/
│   └── ui/
└── res/
    ├── layout/
    ├── values/
    └── font/
```

### Building from Source
```bash
git clone <repository-url>
cd AppKiller
./gradlew assembleDebug
```

### Configuration Constants
All configurable values are centralized in `AppSettings.kt`:
```kotlin
const val MAX_LOG_SIZE = 128
const val DAILY_LOG_LIMIT = 100
const val ICON_BITMAP_SIZE = 144
const val SPLASH_DURATION_MS = 2000L
```

## 📊 Version History

- **v2.5** - Final release with best practices refactoring
- **v2.4** - Automatic log truncation
- **v2.3** - Complete logging system
- **v2.2** - High-definition app icons
- **v2.1** - Splash screen with designer text
- **v2.0** - Custom splash screen
- **v1.8** - Settings-based configuration
- **v1.7** - Toggle between kill modes
- **v1.0** - Initial release

## 🤝 Contributing

### Code Style
- Follow Android development best practices
- Use meaningful constant names instead of magic numbers
- Implement proper error handling and fallbacks
- Write self-documenting code with clear variable names

### Areas for Enhancement
- Migration to newer Compose APIs (replace deprecated Accompanist)
- Additional kill strategies for specific device manufacturers
- Export/import functionality for kill logs
- Scheduled app killing capabilities

## 📄 License

This project is developed for educational and utility purposes. Please ensure compliance with your local laws and device manufacturer policies when using app termination features.

---

## 📞 Support

For issues, questions, or feature requests, please refer to the included documentation:
- `USER_GUIDE.md` - Complete user manual
- `ARCHITECTURE.md` - Technical documentation
- `DEVELOPMENT_LOG.md` - Development history and decisions

**Current Build**: `AppKiller-v2.5-refactored-final.apk` (25MB)
