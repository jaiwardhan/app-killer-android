# AppKiller - Android App Management Tool

## Project Overview
AppKiller is a comprehensive Android application that allows users to manage and terminate running applications on their device. The app provides both automatic and manual app killing capabilities with detailed logging functionality.

## Key Features
- **Dual Kill Modes**: Auto (programmatic) and Manual (Android settings)
- **App Detection**: Lists all installed apps with running status
- **High-Quality Icons**: HD app icons with proper density loading
- **Kill Logging**: Comprehensive logging system with date/time tracking
- **Custom Splash Screen**: Branded splash screen with designer credit
- **Settings Management**: Persistent configuration with SharedPreferences
- **Permission Handling**: Usage Stats and Device Admin permissions

## Architecture
- **MVVM Pattern**: Clean separation with ViewModel and Repository
- **Jetpack Compose**: Modern UI framework
- **Material 3 Design**: Dark theme with consistent styling
- **Settings-Based Config**: Centralized configuration management

## Version History
- v1.0: Basic app listing and killing
- v1.5: Enhanced permissions and device admin
- v1.6: Improved killing mechanisms
- v1.7: Toggle between auto/manual modes
- v1.8: Settings-based configuration
- v2.0: Custom splash screen implementation
- v2.1: Splash screen with designer text
- v2.2: High-definition app icons
- v2.3: Complete logging system
- v2.4: Automatic log truncation
- v2.5: Refactored with best practices (FINAL)

## Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Storage**: SharedPreferences with JSON serialization
- **Permissions**: Usage Stats, Device Admin, Kill Background Processes
- **Dependencies**: Material 3, Accompanist, Kotlinx Serialization
