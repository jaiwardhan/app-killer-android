# Project Context

> This file is linked to [ai.context.md](./ai.context.md) - the root context file for AI conversations.

## Project Overview
AppKiller - Android application for managing and force-stopping running applications

## Core Requirements
- List all installed applications (preferably user-installed) with running state
- Single page layout with app list
- Each list item displays: app name, icon, status label, "Kill" button
- Kill button triggers Android's "Force stop" mechanism
- Auto-refresh list every 10 seconds
- Target Android 13+ (API level 33+)

## Key Features
- **App Discovery**: Enumerate installed applications
- **Process Monitoring**: Track running state of applications
- **Force Stop**: Terminate applications using system mechanism
- **Real-time Updates**: Periodic refresh of app states
- **Permission Management**: Handle required system permissions

## Technical Approach
- **UI**: Single Activity with RecyclerView for app list
- **Permissions**: QUERY_ALL_PACKAGES, KILL_BACKGROUND_PROCESSES
- **Architecture**: MVVM pattern with LiveData for real-time updates
- **Background Tasks**: Periodic refresh using coroutines/handlers
- **System Integration**: PackageManager and ActivityManager APIs

## Module Structure
1. **UI Module**: MainActivity, AppListAdapter, ViewModels
2. **Data Module**: AppRepository, AppInfo data class
3. **System Module**: AppManager (discovery, monitoring, killing)
4. **Permission Module**: Permission handling and requests

## Development Notes
- Code will evolve incrementally
- Focus on user-installed apps for better UX
- Handle edge cases for system apps and protected processes
- Consider battery optimization implications

## Status
- Requirements defined
- Ready for implementation planning
