# Best practices in Android develoment

Lessons learned from Android developers in Futurice. Avoid reinventing the wheel by following these guidelines.

Feedback and criticism are welcomed, feel free to open an issue or send a pull request.

## Summary

#### Use Gradle and its recommended project structure
#### Put passwords and sensitive data in gradle.properties
#### Don't write your own HTTP client, use a library
#### Use Gson unless you have a reason not to
#### Use Fragments to represent a UI "screen"
#### Use Activities just to manage Fragments and Action Bar
#### Use Volley or Picasso to load images
#### Keep your colors.xml short and DRY, just define the palette
#### Also keep dimens.xml DRY, define generic constants
#### Use styles to avoid duplicate attributes in layout XMLs
#### Use multiple style files to avoid a single huge one
#### Do not make a deep hierarchy of ViewGroups
#### Avoid using WebViews as much as you can

### Android SDK

Place your SDK in the /opt directory. Some IDEs include the Android SDK when installed, and might place the SDK under the same directory as the IDE. This can be bad when you need to upgrade (and reinstall) the IDE, or when changing IDEs. Putting it in /opt makes it independent of IDE.

### Project structure

TODO: WRITE ABOUT OLD VS NEW PROJECT STRUCTURE.

Old structure.

    assets
    libs
    res
    src
        com/example/app
    AndroidManifest.xml
    build.gradle
    project.properties
    proguard-rules.pro

New structure

    app
        libs
        src
            androidTest
                java
                    com/example/app
            main
                java
                    com/example/app
                res
                AndroidManifest.xml
        build.gradle
        proguard-rules.pro
    build.gradle
    settings.gradle

### Gradle configuration

### Resources