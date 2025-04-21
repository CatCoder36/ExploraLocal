# ExploraLocal - Android Location-Based App

## Student: Fernando Mauricio Mamani Navarro

ExploraLocal is a location-based Android application that allows users to discover, save, and share interesting places around them. The app uses Google Maps integration and provides features to manage place information including photos, ratings, and descriptions.

## Project Overview

This project is an Android application built with Kotlin that demonstrates modern Android development practices including:

- Google Maps integration for location-based services
- Room database for local data persistence
- MVVM architecture with ViewModels and LiveData
- Material Design components for modern UI
- Fragment-based navigation with a bottom navigation bar
- Camera and image handling capabilities
- Location services for finding nearby places

## Key Features

- Interactive map to explore and find places
- Save custom places with details (name, description, rating, photos)
- View places in a list or on a map
- Nearby places discovery
- Photo capture and upload functionality
- Place sharing capabilities

## Project Structure

- **app/**
  - **src/main/**
    - **java/com/example/firstexampleandroid/**
      - **ui/**: UI components that handle views and user interactions
        - **fragments/**: All screen fragments (PlacesListFragment, MapFragment, NearbyPlacesFragment)
        - **activities/**: Main activity and other activities (MapActivity, SplashActivity)
        - **adapters/**: RecyclerView adapters for displaying lists of places
        - **viewholders/**: ViewHolders for RecyclerView items
      - **viewmodel/**: Contains ViewModels following MVVM architecture
        - Handles business logic and UI state management
        - Communicates between UI and repositories
      - **model/**: Data models and entity classes
        - Place data class and other model objects
      - **database/**: Room database implementation
        - DAOs (Data Access Objects)
        - Database class
        - Entity definitions
      - **repository/**: Data repositories
        - PlaceRepository: Manages place data operations
        - LocationRepository: Handles location services
      - **utils/**: Utility classes and helper functions
        - Permission handlers
        - Image utility functions
        - Location utilities
    - **res/**
      - **layout/**: XML layout files
        - Activities (main_activity, maps_activity, splash_screen)
        - Fragments (fragment_places_list, fragment_map, fragment_nearby_places)
        - Items (item_place, item_nearby_place)
        - Forms (form_place)
      - **menu/**: Menu resources
        - bottom_navigation_menu.xml
        - menu_sort_places.xml
      - **drawable/**: Icons and graphic resources
      - **values/**: Resource values
        - strings.xml: Text resources
        - colors.xml: Color definitions
        - styles.xml: UI styles
        - themes.xml: App themes
      - **navigation/**: Navigation graph for Fragment navigation




## Technical Specifications

- **Minimum SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **Language**: Kotlin

## Key Dependencies

- **AndroidX Libraries**:
  - AppCompat, ConstraintLayout, Activity, Fragment
  - Core-KTX for Kotlin extensions
  - Lifecycle components for ViewModels

- **Google Services**:
  - Maps SDK for Android
  - Play Services Location
  
- **Database**:
  - Room for local database storage

- **Dependency Injection**:
  - Hilt for dependency injection

- **Image Loading**:
  - Glide for efficient image loading and caching

## Setup Requirements

1. **Google Maps API Key**:
   - The app requires a Google Maps API key
   - Add your API key in the AndroidManifest.xml file

2. **Location Permissions**:
   - The app requires location permissions (ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION)
   - Camera and storage permissions for photo functionality

## Building the Project

1. Clone the repository
2. Open the project in Android Studio (recommended version: Hedgehog or later)
3. Sync Gradle files
4. Build and run the application on a device or emulator

## Usage

- The app starts with a splash screen that transitions to the main map view
- Use the bottom navigation to switch between Map, Nearby Places, and My Places
- Add new places through the floating action button or from the map
- View place details and manage saved places in the My Places section

## License

[Include your license information here]

## Contributors

[Include contributors information here]