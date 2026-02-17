# GitHub Actions Android App

A minimal Android app for committing and pushing files to GitHub using Personal Access Token.

## Features
- Commit and push files to GitHub repositories
- Uses GitHub Personal Access Token for authentication
- Simple UI with all necessary fields

## Building the APK

### Prerequisites
- Android Studio or Android SDK installed
- Java JDK 11 or higher

### Build Instructions

#### Option 1: Using Android Studio
1. Open Android Studio
2. Click "Open an Existing Project"
3. Navigate to the `github-app` folder
4. Wait for Gradle sync to complete
5. Click Build > Build Bundle(s) / APK(s) > Build APK(s)
6. APK will be in `app/build/outputs/apk/release/`

#### Option 2: Using Command Line
```bash
cd github-app
./gradlew assembleRelease
```

The APK will be generated at: `app/build/outputs/apk/release/app-release.apk`

## Usage

1. Install the APK on your Android device
2. Open the app
3. Enter your GitHub username
4. Enter your Personal Access Token (create one at https://github.com/settings/tokens)
5. Enter the repository name
6. Enter the file path (e.g., README.md)
7. Enter the file content
8. Enter a commit message (optional)
9. Tap "Commit & Push"

## Creating a GitHub Personal Access Token

1. Go to https://github.com/settings/tokens
2. Click "Generate new token" > "Generate new token (classic)"
3. Give it a name
4. Select scopes: `repo` (Full control of private repositories)
5. Click "Generate token"
6. Copy the token and use it in the app

## Files Structure

```
github-app/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/github/actions/
│           │   ├── MainActivity.java
│           │   └── GitHubAPI.java
│           └── res/
│               └── layout/
│                   └── activity_main.xml
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Note

This app requires internet permission to communicate with GitHub API.
