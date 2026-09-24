# V100 Football AI — Android Cloud Build Project

This is a starter Android project for the V100 Football AI platform. It is deliberately built with simple Android Views/Java so it can be compiled in the cloud without needing Android Studio on the phone.

## What is included
- V100 dashboard
- Fixture scanner demo
- Bet Builder demo
- Safe / Balanced / High Odds sections
- V100 module roadmap
- GitHub Actions workflow that builds an APK automatically

## Important
The current app uses demo fixtures and demo selections. It does **not** contain live football fixtures, bookmaker odds, or guaranteed predictions. For the production version, connect a licensed football-data and odds provider and implement the V100 statistical/prediction modules.

## Build from a phone using GitHub
1. Create/sign in to a GitHub account.
2. Create a new repository, e.g. `V100FootballAI`.
3. Upload the contents of this folder (not the ZIP file inside another folder).
4. Open the repository's **Actions** tab.
5. Select **Build V100 Android APK**.
6. Tap **Run workflow** if it has not started automatically.
7. When the workflow finishes, open the run and download the artifact named **V100FootballAI-debug-apk**.
8. Extract the artifact and install `app-debug.apk` on your Android phone.

## Next production steps
1. Add a football fixtures/results API.
2. Add a bookmaker-odds API.
3. Build the historical database and back-testing engine.
4. Add player/line-up data.
5. Add live match feeds.
6. Add authentication and user profiles.
7. Add crash/error monitoring.
8. Generate a signed release APK/AAB for Play Store publishing.

## Project structure
- `app/` — Android application
- `.github/workflows/build-apk.yml` — cloud APK build
- `README.md` — setup instructions
