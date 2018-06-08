# Tidder #

## Overview ##
This application was developed as part of the Udacity, [Android Developer Nanodegree Program](https://eu.udacity.com/course/android-developer-nanodegree-by-google--nd801).

A [Reddit](https://www.reddit.com/) viewing application which allows the user to follow posts to community
forums (subreddits) separate to their subreddit subscription settings on the website.
Posts are retrieved in an equal manner ensuring that all configured subreddits receive equal
attention.

The application consists of two variants; full and free.

| Features                  | Full variant  | Free variant |
| ------------------------- | ------------- | ------------ |
| View posts                | ✓             | ✓           |
| Follow subreddits         | ✓             | ✓           |
| Widget                    | ✓             | ✓           |
| Ads                       |              | ✓           |
| Share data across devices | ✓             |            |
| Pin posts                 | ✓             |            |

## Technical Overview ##
| Features                  | Full variant  | Free variant |
| ------------------------- | ------------- | ------------ |
| Storage                   | Firebase Realtime Database | Local SQLite Database |
| Advertising               | n/a                        | AdMob                 |

## Development Setup ##
The development environment may be configured as follows:
* Clone the repository (https://github.com/ibuttimer/tidder) from GitHib in Android Studio
* When prompted to create a Studio project, choose yes and Import the project using the default Gradle wrapper.

## Usage ##
Please see the [Tidder help](app/src/main/assets/help.md) for details of how to use the application.




