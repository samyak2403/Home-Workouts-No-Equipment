# Home Workouts No Equipment App

This project is an Android application designed to provide home workout routines without the need for any equipment. The app uses various libraries and tools to enhance functionality and user experience.

## Features

- Home workout routines without equipment
- User-friendly interface
- Firebase integration for analytics, messaging, ads, and crash reporting
- Data binding for efficient UI updates
- Expandable lists for workout categories
- Custom rating dialogs and rating bars
- Integration with Google Play services and Firebase services

## Dependencies

### Firebase
- `implementation 'com.google.firebase:firebase-analytics:21.2.0'`
  - Firebase Analytics for tracking user behavior and app usage.
- `implementation 'com.google.firebase:firebase-messaging:23.1.2'`
  - Firebase Cloud Messaging for push notifications.
- `implementation 'com.google.firebase:firebase-ads:21.5.0'`
  - AdMob for displaying ads within the app.
- `implementation 'com.google.firebase:firebase-core:21.1.1'`
  - Core utilities for Firebase.
- `implementation 'com.google.firebase:firebase-crashlytics-ktx:18.3.6'`
  - Crashlytics for tracking and reporting app crashes.
- `implementation 'com.google.firebase:firebase-analytics-ktx:21.2.0'`
  - Kotlin extensions for Firebase Analytics.

### UI Components
- `implementation 'com.github.florent37:expansionpanel:1.2.4'`
  - Expandable lists for categorizing workout routines.
- `implementation 'com.codemybrainsout.rating:ratingdialog:1.0.8'`
  - Custom rating dialog for user feedback.
- `implementation 'com.github.CaiJingLong:Android-CustomRatingBar:-SNAPSHOT'`
  - Custom rating bar for user feedback.

### Google Play Services
- `implementation 'com.google.android.gms:play-services-ads:21.5.0'`
  - Google Play services for displaying ads.

### AndroidX Libraries
- `implementation 'androidx.appcompat:appcompat:1.3.1'`
  - AppCompat library for backward compatibility.
- `implementation 'com.google.android.material:material:1.4.0'`
  - Material Design components.
- `implementation 'androidx.browser:browser:1.0.0'`
  - Browser support for in-app web browsing.
- `implementation 'androidx.media:media:1.0.1'`
  - Media support for playing audio/video.
- `implementation 'androidx.legacy:legacy-support-v4:1.0.0'`
  - Legacy support for older Android versions.
- `implementation 'androidx.constraintlayout:constraintlayout:2.1.0'`
  - ConstraintLayout for flexible UI design.
- `implementation 'androidx.recyclerview:recyclerview:1.2.1'`
  - RecyclerView for displaying workout routines in a list.

### Image Loading and Animation
- `implementation 'com.github.bumptech.glide:glide:4.9.0'`
  - Glide for loading and caching images.
- `implementation 'com.github.antonKozyriatskyi:CircularProgressIndicator:1.2.2'`
  - Circular progress indicator for displaying loading states.
- `implementation "com.airbnb.android:lottie:3.0.7"`
  - Lottie for loading and rendering animations.

### Network and JSON Parsing
- `implementation 'com.squareup.retrofit2:retrofit:2.6.1'`
  - Retrofit for network requests.
- `implementation 'com.squareup.retrofit2:converter-gson:2.6.1'`
  - Converter for parsing JSON responses using Gson.
- `implementation 'com.google.code.gson:gson:2.8.9'`
  - Gson for JSON serialization and deserialization.

### Others
- `implementation 'com.facebook.android:audience-network-sdk:6.2.1'`
  - Facebook Audience Network for displaying ads.
- `implementation 'uk.co.chrisjenx:calligraphy:2.3.0'`
  - Calligraphy for custom fonts.
- `implementation 'com.ankushgrover:Hourglass:2.0.0'`
  - Hourglass for countdown timers.
- `implementation 'com.kovachcode:timePickerWithSeconds:1.0.1'`
  - Time picker with seconds for workout timing.
- `implementation 'com.shawnlin:number-picker:2.4.8'`
  - Number picker for selecting workout parameters.

### Testing
- `testImplementation 'junit:junit:4.12'`
  - JUnit for unit testing.
- `androidTestImplementation 'androidx.test:runner:1.2.0'`
  - Test runner for instrumentation tests.
- `androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'`
  - Espresso for UI testing.

### Repositories
- `mavenCentral()`
  - Maven Central repository for dependency management.

## Installation

To build and run the project, clone the repository and open it in Android Studio. Ensure you have the necessary SDKs and tools installed.

```bash
git clone https://github.com/samyak2403/Home-Workouts-No-Equipment.git
cd Home-Workouts-No-Equipment
