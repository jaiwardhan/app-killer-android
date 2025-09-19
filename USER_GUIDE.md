# AppKiller User Guide

## Getting Started

### Installation
1. Install `AppKiller-v2.5-refactored-final.apk` on your Android device
2. Grant necessary permissions when prompted
3. Enable Device Admin for enhanced functionality (optional)

### First Launch
- Custom splash screen displays for 2 seconds
- Shows "designed by @jaiwardhan" credit
- Automatically transitions to main app interface

## Main Interface

### App List
- Displays all installed applications
- Shows app icons in high definition
- Indicates running status for each app
- Pull-to-refresh to update the list

### Header Controls
```
AppKiller    [📋] [Auto/Manual] [Toggle Switch]
```
- **📋 Log Icon**: View kill history
- **Auto/Manual Label**: Current kill mode
- **Toggle Switch**: Switch between kill modes

## Kill Modes

### Auto Mode (Switch OFF/Left)
- **How it works**: Programmatic app termination
- **Methods used**: Background process killing, shell commands, task removal
- **Best for**: Quick automatic termination
- **Effectiveness**: Good for most apps, may not work on all foreground apps

### Manual Mode (Switch ON/Right)
- **How it works**: Opens Android's app settings
- **User action**: Tap "Force Stop" button in settings
- **Best for**: Guaranteed app termination
- **Effectiveness**: 100% success rate, works on all apps

## Using the App

### Killing Apps
1. **Swipe left** on any app in the list
2. Red "Kill" button appears
3. Tap the kill button
4. App is terminated based on current mode
5. Action is automatically logged

### Viewing Kill Logs
1. Tap the **📋 list icon** in the header
2. View chronological list of killed apps
3. Each entry shows:
   - **Date**: When the app was killed
   - **Time**: Exact time (12-hour format)
   - **App Name**: Name of the killed application
   - **Mode**: Auto (green) or Manual (orange)
4. Tap **← Back** to return to main screen

### Switching Kill Modes
1. Use the toggle switch in the header
2. **Left/OFF**: Auto mode (programmatic killing)
3. **Right/ON**: Manual mode (opens app settings)
4. Setting is automatically saved and persists

## Permissions

### Required Permissions

#### Usage Access Permission
- **Purpose**: Detect which apps are currently running
- **How to grant**: 
  1. Tap "Grant Usage Access" when prompted
  2. Find AppKiller in the list
  3. Toggle the permission ON
  4. Return to the app

#### Device Admin Permission (Optional)
- **Purpose**: Enhanced app termination capabilities
- **How to grant**:
  1. Tap "Enable Device Admin" when prompted
  2. Review permissions and tap "Activate"
  3. Return to the app
- **Note**: Provides better success rate for auto mode

### Permission Benefits
- **With permissions**: More accurate running app detection, better kill success rate
- **Without permissions**: Basic functionality still works, limited effectiveness

## Features

### Automatic Logging
- Every app kill is automatically logged
- No user action required
- Logs include date, time, app name, and kill mode
- Maximum 128 log entries (oldest automatically deleted)

### High-Quality Icons
- App icons displayed in high definition
- Automatically loads best available icon quality
- Crisp display on all screen densities

### Persistent Settings
- Kill mode preference saved automatically
- Settings survive app restarts
- Logs preserved between sessions

### Smart Log Management
- Automatic cleanup on app startup
- Keeps newest 128 entries
- Prevents storage bloat
- JSON-based efficient storage

## Troubleshooting

### App Won't Kill
1. **Try Manual Mode**: Switch to manual mode for guaranteed results
2. **Check Permissions**: Ensure Usage Access and Device Admin are granted
3. **System Apps**: Some system apps cannot be killed for security reasons
4. **Foreground Apps**: Apps actively in use may resist termination

### No Apps Showing
1. **Grant Usage Access**: Required for accurate app detection
2. **Pull to Refresh**: Swipe down to refresh the app list
3. **Wait**: Initial load may take a few seconds

### Logs Not Saving
1. **Storage Space**: Ensure device has available storage
2. **App Permissions**: Check if app has storage permissions
3. **Restart App**: Close and reopen AppKiller

### Performance Issues
1. **Too Many Apps**: Large app lists may load slowly
2. **Low Memory**: Close other apps to free memory
3. **Restart Device**: Reboot if system is sluggish

## Tips & Best Practices

### For Best Results
- Grant all requested permissions
- Use Auto mode for quick kills
- Use Manual mode for stubborn apps
- Regularly check kill logs to monitor usage

### Battery Optimization
- AppKiller itself uses minimal battery
- Killing battery-draining apps can improve device performance
- Monitor which apps you kill most frequently

### Privacy & Security
- AppKiller only accesses app information, not app data
- No personal information is collected or transmitted
- All data stored locally on your device
- Logs can be cleared by uninstalling the app

## Support

### Common Questions
- **Q**: Why can't I kill system apps?
- **A**: Android protects critical system apps from termination

- **Q**: Does this work without root?
- **A**: Yes, AppKiller works on non-rooted devices

- **Q**: Will this improve battery life?
- **A**: Killing battery-draining apps can help improve battery performance

### Version Information
- **Current Version**: v2.5 (Final)
- **Last Updated**: January 2025
- **Compatibility**: Android 7.0+ recommended
