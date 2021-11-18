# Tidder #

- [Tidder](#tidder)
  - [Overview](#overview)
    - [Features](#features)
  - [Technical Overview](#technical-overview)
  - [Development Setup](#development-setup)
  - [Usage](#usage)

## Overview ##
This application was developed as part of the Udacity, [Android Developer Nanodegree Program](https://eu.udacity.com/course/android-developer-nanodegree-by-google--nd801).

A [Reddit](https://www.reddit.com/) viewing application which allows the user to follow posts to community
forums (subreddits) separate to their subreddit subscription settings on the website.
Posts are retrieved in an equal manner ensuring that all configured subreddits receive equal
attention.

### Features
The application consists of three variants; full, free and freeMax.

| Features                  | Full variant  | Free variant | FreeMax variant |
| ------------------------- | ------------- | ------------ | --------------- |
| View posts                | &check;       | &check;      | &check;         |
| Follow subreddits         | &check;       | &check;      | &check;         |
| Widget                    | &check;       | &check;      | &check;         |
| Ads                       | &cross;       | &check;      | &check;         |
| Share data across devices | &check;       | &cross;      | &cross;         |
| Pin posts                 | &check;       | &cross;      | &check;         |

## Technical Overview ##
| Features                  | Full variant  | Free variant | FreeMax variant |
| ------------------------- | ------------- | ------------ | --------------- |
| Storage                   | Firebase Realtime Database | Local SQLite Database | Local SQLite Database |
| Advertising               | n/a                        | AdMob                 | n/a                   |

## Development Setup ##
The development environment may be configured as follows:
* Clone the repository (https://github.com/ibuttimer/tidder) from GitHib in Android Studio
* When prompted to create a Studio project, choose yes and Import the project using the default Gradle wrapper.
* Create a copy of the sample Values Resource File [secrets.xml.sample](sample/secrets.xml.sample), update appropriately,
  and save as `secrets.xml` in [app/src/main/res/values](app/src/main/res/values).
  ```xml
  <resources>
    <!-- Reddit Client ID, see https://www.reddit.com/wiki/api -->
    <string name="reddit_client_id">replace_with_client_id</string>
  </resources>
  ```
    E.g. using a Client ID of `abcde12345ABCDE`:
  ```xml
  <resources>
    <string name="reddit_client_id">abcde12345ABCDE</string>
  </resources>
  ```
* The **Full variant** requires adding a `google-services.json` file to the [app](app) folder
  
  - Create a project in the [Firebase Console](https://console.firebase.google.com/)
  - Follow the *Add Firebase to your Android app* instructions to add an app for each variant:
    - com.ianbuttimer.tidderish.free 
    - com.ianbuttimer.tidderish.freeMax 
    - com.ianbuttimer.tidderish.full
  - Download the `google-services.json` file to the [app](app) folder 

## Usage ##
Please see the [Tidder help](app/src/main/assets/help.md) for details of how to use the application.




