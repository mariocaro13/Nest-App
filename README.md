# Nest App
Mobile application built with Jetpack Compose and Firebase to help users catalog, view, and manage a personal “nest” of birds. 
The core functionality is complete, offering authentication, bird creation, listing, and detail views. The visual design is actively under development and will be refined over future releases.

## Overview
Nest App provides a simple, intuitive interface for bird enthusiasts to:

Sign up or log in with Firebase Authentication.

Create new bird entries with name, description, and photos.

Browse a scrolling list of existing birds.

View detailed information and images for each bird.

Edit or delete entries from the detail screen.

Maintain a user profile with a custom avatar.

## Features
Firebase Authentication (email/password)

Cloud Firestore for storing bird data

Photo upload via the device picker

Home screen with “Add Bird” dialog

Bird detail screen with “Edit Bird” dialog

Bottom navigation bar for Home and Profile

Easy-to-extend architecture with ViewModels and state flows

## Getting Started
Clone the repository:

bash
```
  git clone https://github.com/mariocaro13/Nest-App.git
```
Open the project in Android Studio (Arctic Fox or newer). 
Sync Gradle and install required SDK components.

## Configuration
Create a Firebase project at console.firebase.google.com.

Enable Authentication (Email/Password) and Firestore.

Download the google-services.json file and place it in the module’s app/ directory.

Ensure your package name matches the one registered in Firebase.

## Usage
Launch the app on an emulator or real device.

Create a new account or log in.

On the Home screen, tap the “+” FAB to add a bird.

Fill in the form, select up to 5 images, and save.

Scroll the list to view saved birds.

Tap any bird card to see details and edit or delete.

Switch to Profile via the bottom bar to update your avatar and logout.

## Visual Design
The core functionality works end-to-end, but the UI is a work in progress. You may notice:

Placeholder artwork and basic layouts

Early Material3 theming

Future plans for polished animations, responsive styling, and improved accessibility

## Future Implementations

- Upload additional bird images in the detail tab beyond the initial five.
- Edit existing bird entries directly from the detail screen.
- Delete bird entries from both list and detail views. 
