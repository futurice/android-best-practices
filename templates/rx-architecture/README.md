
# Project name here

Mobile client for XYZ.

## Deliverables

Software releases of the app will be downloadable at `https://hockeyapp.net`.
In order to download a test release, you must be a invited/registered user with
a registered test phone.

## Supported devices

This project will initially target the following devices:

1.  Samsung SM-G900F Galaxy S5
2.  Samsung GT-I9300 Galaxy S III (upgradeable to Android 4.3)
3.  Samsung GT-I9100 Galaxy S II (upgradeable Android 4.1)
4.  Samsung GT-I9505 Galaxy S4 (Android 4.2.2)
5.  Samsung GT-I9305 Galaxy S III LTE
6.  Samsung GT-I8190 Galaxy S Mini (Android 4.1)
7.  HTC ONE
8.  Nexus 4/5

Android versions targeted:

1. Android 4.1 Jelly Bean (API level 16)
2. Android 4.2 Jelly Bean (API level 17)
3. Android 4.3 Jelly Bean (API level 18)
4. Android 4.4 KitKat (API level 19)

## Codebase

Repository URL: https://code.futurice.com/customerx_projecty

- Master: Main branch containing only versions supposed to be in live production.
Master branch contains only merges from rc branch, after rc branch has gone
through user tests, and is approved as a stable version.

- Release candidate (rc): branch containing all versions deliverable to the
customer, for internal testing purposes. Also can be used for beta testing
with users in the public. Gets merges from develop branch, and any commit
made on rc should only be bug fixes. Any commit on rc must be merged back to
develop too.

- Develop: The development branch. This is where the actual work happens.
Developers are supposed to checkout this branch in order to develop new code.
Commits will happen often and in small chunks.
Feel free to create your own feature branches out of this branch.

## Used libraries

1. http://square.github.io/retrofit/

2. http://square.github.io/picasso/

3. https://github.com/FasterXML/jackson

4. https://github.com/Netflix/RxJava

## Environment Setup

1.  Add yourself to the git project 'PCustomerXProjectY' at `https://code.futurice.com`.
Under your profile, add your ssh public key.
If you don't have a public key yet, create it on command line using command `ssh-keygen`.

2.  Checkout the code from code.futurice.com

    git clone git@code.futurice.com:customerx_projecty.git
    git checkout -b develop origin/develop
    git branch -a

3.  Define necessary configurations such as passwords.
    Make a `gradle.properties` file following this format:

```
KEYSTORE_PASSWORD=********
KEY_PASSWORD=********
```

Get these passwords from https://password.futurice.com.

## Important Gradle tasks

1. `gradle assembleDebug`
2. `gradle assembleRelease`
3. `gradle runDebug`
4. `gradle runRelease`
5. `gradle checkstyle`

## Release builds

Release or release candidate builds should be done via Jenkins jobs.
