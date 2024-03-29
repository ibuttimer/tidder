## Overview
This application is an API client, allowing the retrieval and display of content from [Reddit](https://www.reddit.com/). 

    The application and its developers are not affiliated in any way with Reddit.

## Log in

In order to use the application a Reddit account is required.
If you do not have an account, you can sign up at [reddit.com](https://www.reddit.com/login).

* When prompted login using your Reddit credentials.
Check the 'Remember Me' option to avoid having to enter your credentials every time you log in.
* Review and accept the authorisation request to allow access to your account

The application only uses the login credentials to **read** information regarding
subreddits, posts and the currently logged in user (specifically the user's Reddit id).
In the case of the free version, the user's Reddit id may be stored in a local database.
In the case of the full version, the user's Reddit id is used to identify data stored in a Firebase Realtime database.

## Follow Subreddits

The application allows the user to maintain a list of Subreddits from which posts are retrieved.

**Note** this list is separate to any follow list which may be stored in the
user's settings on Reddit.com.

The Follow functionality may be accessed via the *Follow* option in the menu.

### List tab
The list tab displays the list of Subbreddits which are currently being followed.

* Tap the *UnLike* button at the side of a Subbreddit entry to unfollow the Subreddit.
* Tap the *Discard* button at the bottom of the screen to unfollow all Subreddits

### Search tab
The search tab allows the user to search for Subreddits by interests or name.

* Enter the search criteria in the appropriate field and tap the *Search* button
* Tap the *Like* button at the side of a Subbreddit entry to follow the Subreddit
* Tap the *Clear* button to clear the search criteria and Subreddit results list

### All tab
The all tab allows the user to scroll through the list of all Subreddits.

* Tap the *Like* button at the side of a Subbreddit entry to follow the Subreddit
* Tap the *UnLike* button at the side of a Subbreddit entry to unfollow the Subreddit.

## Subreddits Posts

A list of posts from the Subreddit Follow list are displayed on the New Posts tab.
The Post Source Listing may be changed in Settings.

The FreeMax and Full application versions allow the user to pin posts,
and these may be viewed on the Pinned tab.

### New tab
* Tap on a post to view the post content.
* Swipe left or right to discard a post from the list.
* Tap on the *Refresh* button to retrieve new posts.

### Pinned tab
**Note** only available in the FreeMax and Full application version.

* Tap on a post to view the post content.


## Post Details
Post details along with a selection of comment are displayed.

* For a post containing an image/link, tap on the image/link to view the linked material.
* Tap on the *Expand* icon to display replies to comments.
* Where a 'More Comments' is displayed, tap on the entry to retrieve additional replies to the comment.
* Tap on the *Collapse* icon to hide replies to a comment.
* Double tap on a comment to view the comment thread.

When navigating using a keyboard or D-pad:
* Select the list entry using the arrow keys.
* Press the enter/centre key to expand/collapse replies to comments,
or in the case of 'More Comments' to retrieve additional replies.
* Double press the enter/centre key to view a comment thread.

For the Full application version
* Tap on the *Pin* button to add the post to the pinned list.
* Tap on the *Unpin* button to remove the post from the pinned list.

## Settings
The Settings functionality may be accessed via the *Settings* option in the menu.

* *Safe for Work* - Enable/disable display of post marked Safe for Work.
* *Refresh on Discard* - Enable/disable the automatic retrieval of another post from the same Subreddit when a post is dicarded.
* *Comment Thread Auto Expand* - Enable/disable the automatic expansion of replies to comments.
* *Thread Auto Expand Level* - Sets the level to which replies to comments are expanded when *Comment Thread Auto Expand* is enabled.
* *Post Source* - Sets the listing from which posts are retrieved.

## Logout
Select the *Logout* option from the menu to logout.

## Application Widget
Add the application widget to the home screen to view post summaries. Tap on a post to view details.