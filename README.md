# Best practices in Android develoment

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
    proguard-project.txt

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

### Gradle configuration

### Resources