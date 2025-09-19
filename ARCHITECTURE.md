# AppKiller Technical Architecture

## Project Structure

```
app/src/main/java/com/jazz13/appkiller/
├── MainActivity.kt                 # Main UI entry point
├── SplashActivity.kt              # Custom splash screen
├── data/
│   ├── AppInfo.kt                 # App data model
│   ├── AppRepository.kt           # Data access layer
│   └── KillLog.kt                 # Log entry model
├── settings/
│   └── AppSettings.kt             # Configuration management
├── system/
│   ├── AppManager.kt              # Core app management logic
│   └── AppKillerDeviceAdmin.kt    # Device admin receiver
└── ui/
    ├── MainViewModel.kt           # Main screen view model
    ├── LogScreen.kt               # Log viewer UI
    └── theme/
        └── Theme.kt               # App theming

app/src/main/res/
├── layout/
│   └── splash_screen.xml          # Splash screen layout
├── values/
│   ├── colors.xml                 # Color definitions
│   ├── dimens.xml                 # Dimension resources
│   ├── splash_colors.xml          # Splash-specific colors
│   └── splash_theme.xml           # Splash screen theme
├── xml/
│   └── device_admin_policy.xml    # Device admin configuration
└── font/                          # Custom font files
```

## Core Components

### 1. MainActivity.kt
- **Purpose**: Main UI controller and navigation
- **Key Features**:
  - Jetpack Compose UI implementation
  - Navigation between main screen and log screen
  - ViewModel integration and state management
  - Custom font loading and dark theme

### 2. AppManager.kt
- **Purpose**: Core app management and system interaction
- **Key Features**:
  - Multi-layered app killing strategies
  - High-density icon loading
  - Running app detection via Usage Stats
  - Device admin permission management
  - App settings navigation

### 3. AppSettings.kt
- **Purpose**: Centralized configuration management
- **Key Features**:
  - SharedPreferences-based persistence
  - Kill behavior toggle (Auto/Manual)
  - Logging system with JSON serialization
  - Automatic log truncation
  - All application constants

### 4. MainViewModel.kt
- **Purpose**: UI state management and business logic
- **Key Features**:
  - MVVM pattern implementation
  - StateFlow for reactive UI updates
  - Permission state tracking
  - Navigation state management
  - Log truncation on startup

### 5. LogScreen.kt
- **Purpose**: Kill log viewer interface
- **Key Features**:
  - Three-column log display layout
  - Date/time formatting
  - Color-coded kill modes
  - Empty state handling
  - Back navigation

## Data Flow

```
User Action → ViewModel → Repository → AppManager → System APIs
     ↓
UI Updates ← StateFlow ← ViewModel ← Repository ← AppManager
```

## Configuration Constants

### AppSettings Constants
```kotlin
// Kill behavior
const val KILL_BEHAVIOR_AUTO = 1
const val KILL_BEHAVIOR_MANUAL = 2

// Log management
const val MAX_LOG_SIZE = 128
const val DAILY_LOG_LIMIT = 100

// UI constants
const val ICON_BITMAP_SIZE = 144
const val ICON_DISPLAY_SIZE = 48

// Timing
const val SPLASH_DURATION_MS = 2000L

// Colors
const val DEFAULT_SPLASH_BACKGROUND = "#3B3B3B"
```

## Permission Requirements

### Required Permissions
- `android.permission.PACKAGE_USAGE_STATS` - For detecting running apps
- `android.permission.KILL_BACKGROUND_PROCESSES` - For killing background processes
- `android.permission.GET_TASKS` - For task management
- `android.permission.REORDER_TASKS` - For task reordering
- `android.permission.FORCE_STOP_PACKAGES` - For enhanced force stop (system-level)

### Device Admin Policy
- Enables enhanced app termination capabilities
- Provides system-level access for force stopping apps
- User must manually enable in device settings

## App Killing Strategies

### Auto Mode (Programmatic)
1. **killBackgroundProcesses()** - Standard Android API
2. **Shell am force-stop** - Most effective method
3. **Direct process kill** - Kill by PID
4. **Task removal** - Remove from recent tasks
5. **Shell pkill** - Unix-style killing (requires root)

### Manual Mode
- Opens Android's native app settings
- User manually taps "Force Stop"
- Guaranteed termination method
- Works on all devices without special permissions

## Storage Architecture

### SharedPreferences Structure
```
app_killer_settings.xml
├── TOGGLE_APP_KILL_BEHAVIOUR_ROUTE (Int) - Kill behavior mode
├── SPLASH_BACKGROUND_COLOR (String) - Splash background color
└── KILL_LOGS (String) - JSON array of kill log entries
```

### Log Entry Format
```json
{
  "id": 1642636800000,
  "date": "Jan 20, 2025",
  "time": "01:46 AM",
  "appName": "Chrome",
  "killMode": "Auto"
}
```

## UI Architecture

### Compose Structure
- **AppKillerApp**: Root composable with navigation logic
- **AppListScreen**: Main app list with swipe-to-kill
- **LogScreen**: Dedicated log viewer
- **AppListItem**: Individual app row with swipe actions

### Theme Configuration
- **Dark Theme**: Primary color scheme
- **Custom Colors**: Background (#1C1C1E), Surface (#2C2C2E)
- **Typography**: Custom FK Grotesk Neue font family
- **Material 3**: Latest design system implementation

## Build Configuration

### Dependencies
```kotlin
// Core Android
implementation("androidx.core:core-ktx")
implementation("androidx.activity:activity-compose")

// Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// Additional
implementation("androidx.core:core-splashscreen:1.0.1")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")
```

### Plugins
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.10"
}
```

This architecture provides a robust, maintainable, and scalable foundation for the AppKiller application.
