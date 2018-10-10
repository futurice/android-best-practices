# Best practices in Android development

Avoid reinventing the wheel by following these guidelines. Lessons learned from Android developers in [Futurice](http://www.futurice.com). If you are interested in iOS or Windows Phone development, be sure to check also our [**iOS Good Practices**](https://github.com/futurice/ios-good-practices) and [**Windows App Development Best Practices**](https://github.com/futurice/windows-app-development-best-practices) documents.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--best--practices-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1091) 
[![Spice Program Sponsored](https://img.shields.io/badge/chilicorn-sponsored-brightgreen.svg?logo=data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAANSUhEUgAAAA4AAAAPCAMAAADjyg5GAAABqlBMVEUAAAAzmTM3pEn%2FSTGhVSY4ZD43STdOXk5lSGAyhz41iz8xkz2HUCWFFhTFFRUzZDvbIB00Zzoyfj9zlHY0ZzmMfY0ydT0zjj92l3qjeR3dNSkoZp4ykEAzjT8ylUBlgj0yiT0ymECkwKjWqAyjuqcghpUykD%2BUQCKoQyAHb%2BgylkAyl0EynkEzmkA0mUA3mj86oUg7oUo8n0k%2FS%2Bw%2Fo0xBnE5BpU9Br0ZKo1ZLmFZOjEhesGljuzllqW50tH14aS14qm17mX9%2Bx4GAgUCEx02JySqOvpSXvI%2BYvp2orqmpzeGrQh%2Bsr6yssa2ttK6v0bKxMBy01bm4zLu5yry7yb29x77BzMPCxsLEzMXFxsXGx8fI3PLJ08vKysrKy8rL2s3MzczOH8LR0dHW19bX19fZ2dna2trc3Nzd3d3d3t3f39%2FgtZTg4ODi4uLj4%2BPlGxLl5eXm5ubnRzPn5%2Bfo6Ojp6enqfmzq6urr6%2Bvt7e3t7u3uDwvugwbu7u7v6Obv8fDz8%2FP09PT2igP29vb4%2BPj6y376%2Bu%2F7%2Bfv9%2Ff39%2Fv3%2BkAH%2FAwf%2FtwD%2F9wCyh1KfAAAAKXRSTlMABQ4VGykqLjVCTVNgdXuHj5Kaq62vt77ExNPX2%2Bju8vX6%2Bvr7%2FP7%2B%2FiiUMfUAAADTSURBVAjXBcFRTsIwHAfgX%2FtvOyjdYDUsRkFjTIwkPvjiOTyX9%2FAIJt7BF570BopEdHOOstHS%2BX0s439RGwnfuB5gSFOZAgDqjQOBivtGkCc7j%2B2e8XNzefWSu%2BsZUD1QfoTq0y6mZsUSvIkRoGYnHu6Yc63pDCjiSNE2kYLdCUAWVmK4zsxzO%2BQQFxNs5b479NHXopkbWX9U3PAwWAVSY%2FpZf1udQ7rfUpQ1CzurDPpwo16Ff2cMWjuFHX9qCV0Y0Ok4Jvh63IABUNnktl%2B6sgP%2BARIxSrT%2FMhLlAAAAAElFTkSuQmCC)](https://spiceprogram.org/)

## Summary

#### [Use Gradle and its default project structure](#build-system)
#### [Put passwords and sensitive data in gradle.properties](#gradle-configuration)
#### [Use the Jackson library to parse JSON data](#libraries)
#### [Don't write your own HTTP client, use OkHttp libraries](#networklibs)
#### [Avoid Guava and use only a few libraries due to the *65k method limit*](#methodlimitation)
#### [Sail carefully when choosing between Activities and Fragments](#activities-and-fragments)
#### [Layout XMLs are code, organize them well](#resources)
#### [Use styles to avoid duplicate attributes in layout XMLs](#styles)
#### [Use multiple style files to avoid a single huge one](#splitstyles)
#### [Keep your colors.xml short and DRY, just define the palette](#colorsxml)
#### [Also keep dimens.xml DRY, define generic constants](#dimensxml)
#### [Do not make a deep hierarchy of ViewGroups](#deephierarchy)
#### [Avoid client-side processing for WebViews, and beware of leaks](#webviews)
#### [Use JUnit for unit tests, Espresso for connected (UI) tests, and AssertJ-Android for easier assertions in your Android tests](#test-frameworks)
#### [Always use ProGuard or DexGuard](#proguard-configuration)
#### [Use SharedPreferences for simple persistence, otherwise ContentProviders](#data-storage)
#### [Use Stetho to debug your application](#use-stetho)
#### [Use Leak Canary to find memory leaks](#use-leakcanary)
#### [Use continuous integration](#use-continuous-integration-1)

----------

### Android SDK

Place your [Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools) somewhere in your home directory or some other application-independent location. Some distributions of IDEs include the SDK when installed, and may place it under the same directory as the IDE. This can be bad when you need to upgrade (or reinstall) the IDE, as you may lose your SDK installation, forcing a long and tedious redownload.

Also avoid putting the SDK in a system-level directory that might need root permissions, to avoid permissions issues.

### Build system

Your default option should be [Gradle](https://gradle.org) using the [Android Gradle plugin](https://developer.android.com/studio/build/index.html). 

It is important that your application's build process is defined by your Gradle files, rather than being reliant on IDE specific configurations. This allows for consistent builds between tools and better support for continuous integration systems.

### Project structure

Although Gradle offers a large degree of flexibility in your project structure, unless you have a compelling reason to do otherwise, you should accept its [default structure](https://developer.android.com/studio/build/index.html#sourcesets) as this simplify your build scripts. 

### Gradle configuration

**General structure.** Follow [Google's guide on Gradle for Android](https://developer.android.com/studio/build/index.html).

**minSdkVersion: 21** We recommend to have a look at the [Android version usage chart](https://developer.android.com/about/dashboards/index.html#Platform) before defining the minimum API required. Remember that the statistics given are global statistics and may differ when targeting a specific regional/demographic market. It is worth mentioning that some material design features are only available on Android 5.0 (API level 21) and above. And also, from API 21, the multidex support library is not needed anymore.

**Small tasks.** Instead of (shell, Python, Perl, etc) scripts, you can make tasks in Gradle. Just follow [Gradle's documentation](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF) for more details. Google also provides some helpful [Gradle recipes](https://developer.android.com/studio/build/gradle-tips.html), specific to Android.

**Passwords.** In your app's `build.gradle` you will need to define the `signingConfigs` for the release build. Here is what you should avoid:

_Don't do this_. This would appear in the version control system.

```groovy
signingConfigs {
    release {
        // DON'T DO THIS!!
        storeFile file("myapp.keystore")
        storePassword "password123"
        keyAlias "thekey"
        keyPassword "password789"
    }
}
```

Instead, make a `gradle.properties` file which should _not_ be added to the version control system:

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```

That file is automatically imported by Gradle, so you can use it in `build.gradle` as such:

```groovy
signingConfigs {
    release {
        try {
            storeFile file("myapp.keystore")
            storePassword KEYSTORE_PASSWORD
            keyAlias "thekey"
            keyPassword KEY_PASSWORD
        }
        catch (ex) {
            throw new InvalidUserDataException("You should define KEYSTORE_PASSWORD and KEY_PASSWORD in gradle.properties.")
        }
    }
}
```

**Prefer Maven dependency resolution to importing jar files.** If you explicitly include jar files in your project, they will be a specific frozen version, such as `2.1.1`. Downloading jars and handling updates is cumbersome and is a problem that Maven already solves properly. Where possible, you should attempt to use Maven to resolve your dependencies, for example:

```groovy
dependencies {
    implementation 'com.squareup.okhttp:okhttp3:3.8.0'
}
```    

**Avoid Maven dynamic dependency resolution**
Avoid the use of dynamic dependency versions, such as `2.1.+` as this may result in different and unstable builds or subtle, untracked differences in behavior between builds. The use of static versions such as `2.1.1` helps create a more stable, predictable and repeatable development environment.

**Use different package name for non-release builds**
Use `applicationIdSuffix` for *debug* [build type](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Build-Types) to be able to install both *debug* and *release* apk on the same device (do this also for custom build types, if you need any). This will be especially valuable after your app has been published.

```groovy
android {
    buildTypes {
        debug {
            applicationIdSuffix '.debug'
            versionNameSuffix '-DEBUG'
        }

        release {
            // ...
        }
    }
}
```

Use different icons to distinguish the builds installed on a device—for example with different colors or an overlaid  "debug" label. Gradle makes this very easy: with default project structure, simply put *debug* icon in `app/src/debug/res` and *release* icon in `app/src/release/res`. You could also [change app name](http://stackoverflow.com/questions/24785270/how-to-change-app-name-per-gradle-build-type) per build type, as well as  `versionName` (as in the above example).

**Share debug app keystore file**
Sharing the debug APK keystore file via the app repository saves time when testing on shared devices and avoids the uninstalling/reinstalling of the app. It also simplifies the processing of working with some Android SDKs, such as Facebook, which require the registration of a single key store hash. Unlike the release key file, the debug key file can safely be added to your repository.

**Share code style formatting defintions**
Sharing the code style and formatting definitions via the app repository helps ensure a visually consistent code base and makes code comprehension and reviews easier.

### Android Studio as your main IDE

The recommended IDE for Android development is [Android Studio](https://developer.android.com/sdk/installing/studio.html) because it is developed and constantly updated by Google, has good support for Gradle, contains a range of useful monitoring and analysis tools and is fully tailored for Android development.

Avoid adding Android Studio's specific configuration files, such as `.iml` files to the version control system as these often contain configurations specific of your local machine, which won't work for your colleagues.

### Libraries

**[Jackson](http://wiki.fasterxml.com/JacksonHome)** is a Java library for JSON serialization and deserialization, it has a wide-scoped and versatile API, supporting various ways of processing JSON: streaming, in-memory tree model, and traditional JSON-POJO data binding. 

[Gson](https://code.google.com/p/google-gson/) is another popular choice and being a smaller library than Jackson, you might prefer it to avoid 65k methods limitation. Also, if you are using  

[Moshi](https://github.com/square/moshi), another of [Square's](https://github.com/square) open source libraries, builds upon learnings from the development of Gson and also integrates well with Kotlin.

<a name="networklibs"></a>
**Networking, caching, and images.** There are a couple of battle-proven solutions for performing requests to backend servers, which you should use rather than implementing your own client. We recommend basing your stack around [OkHttp](http://square.github.io/okhttp/) for efficient HTTP requests and using [Retrofit](http://square.github.io/retrofit/) to provide a typesafe layer. If you choose Retrofit, consider [Picasso](http://square.github.io/picasso/) for loading and caching images.

Retrofit, Picasso and OkHttp are created by the same company, so they complement each other nicely and compatability issues are uncommon.

[Glide](https://github.com/bumptech/glide) is another option for loading and caching images. It has support for animated GIFs, circular images and claims of better performance than Picasso, but also a bigger method count.

**RxJava** is a library for Reactive Programming, in other words, handling asynchronous events. It is a powerful paradigm, but it also has a steep learning curve. We recommend taking some caution before using this library to architect the entire application. We have written some blog posts on it: [[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android), [[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754), [[4]](http://blog.futurice.com/android-development-has-its-own-swift). For a reference app, our open source app [Freesound Android](https://github.com/futurice/freesound-android) makes extensive use of RxJava 2.

If you have no previous experience with Rx, start by applying it only for responses from app's backend APIs. Alternatively, start by applying it for simple UI event handling, like click events or typing events on a search field. If you are confident in your Rx skills and want to apply it to the whole architecture, then write documentation on all the tricky parts. Keep in mind that another programmer unfamiliar to RxJava might have a very hard time maintaining the project. Do your best to help them understand your code and also Rx.

Use [RxAndroid](https://github.com/ReactiveX/RxAndroid) for Android threading support and [RxBinding](https://github.com/JakeWharton/RxBinding) to easily create Observables from existing Android components.

**[Retrolambda](https://github.com/evant/gradle-retrolambda)** is a Java library for using Lambda expression syntax in Android and other pre-JDK8 platforms. It helps keep your code tight and readable especially if you use a functional style, such as in RxJava.

Android Studio offers code assist support for Java 8 lambdas. If you are new to lambdas, just use the following to get started:

- Any interface with just one method is "lambda friendly" and can be folded into the more tight syntax
- If in doubt about parameters and such, write a normal anonymous inner class and then let Android Studio fold it into a lambda for you.

Note that from Android Studio 3.0, [Retrolambda is no longer required](https://developer.android.com/studio/preview/features/java8-support.html).

<a name="methodlimitation"></a>
**Beware of the dex method limitation, and avoid using many libraries.** Android apps, when packaged as a dex file, have a hard limitation of 65536 referenced methods [[1]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [[2]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [[3]](http://jakewharton.com/play-services-is-a-monolith/). You will see a fatal error on compilation if you pass the limit. For that reason, use a minimal amount of libraries, and use the [dex-method-counts](https://github.com/mihaip/dex-method-counts) tool to determine which set of libraries can be used in order to stay under the limit. Especially avoid using the Guava library, since it contains over 13k methods.

### Activities and Fragments

There is no consensus among the community nor Futurice developers how to best organize Android architectures with Fragments and Activities. Square even has [a library for building architectures mostly with Views](https://github.com/square/mortar), bypassing the need for Fragments, but this still is not considered a widely recommendable practice in the community.

Because of Android API's history, you can loosely consider Fragments as UI pieces of a screen. In other words, Fragments are normally related to UI. Activities can be loosely considered to be controllers, they are especially important for their lifecycle and for managing state. However, you are likely to see variation in these roles: activities might take UI roles ([delivering transitions between screens](https://developer.android.com/about/versions/lollipop.html)), and [fragments might be used solely as controllers](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI). We suggest you sail carefully, making informed decisions since there are drawbacks for choosing a fragments-only architecture, or activities-only, or views-only. Here is some advice on what to be careful with, but take them with a grain of salt:

- Avoid using [nested fragments](https://developer.android.com/about/versions/android-4.2.html#NestedFragments) extensively, because [matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/) can occur. Use nested fragments only when it makes sense (for instance, fragments in a horizontally-sliding ViewPager inside a screen-like fragment) or if it's a well-informed decision.
- Avoid putting too much code in Activities. Whenever possible, keep them as lightweight containers, existing in your application primarily for the lifecycle and other important Android-interfacing APIs. Prefer single-fragment activities instead of plain activities - put UI code into the activity's fragment. This makes it reusable in case you need to change it to reside in a tabbed layout, or in a multi-fragment tablet screen. Avoid having an activity without a corresponding fragment, unless you are making an informed decision.

### Java packages structure

We recommend using a *feature based* package structure for your code. This has the following benefits:

- Clearer feature dependency and interface boundaries.
- Promotes encapsulation.
- Easier to understand the components that define the feature.  
- Reduces risk of unknowingly modifying unrelated or shared code.
- Simpler navigation: most related classes will be in the one package.
- Easier to remove a feature.
- Simplifies the transition to module based build structure (better build times and Instant Apps support)

The alternative approach of defining your packages by *how* a feature is built (by placing related Activities, Fragments, Adapters etc in separate packages) can lead to a fragmented code base with less implementation flexibility. Most importantly, it hinders your ability to comprehend your code base in terms of its primary role: to provide features for your app.   

### Resources

**Naming.** Follow the convention of prefixing the type, as in `type_foo_bar.xml`. Examples: `fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`.

**Organizing layout XMLs.** If you're unsure how to format a layout XML, the following convention may help.

- One attribute per line, indented by 4 spaces
- `android:id` as the first attribute always
- `android:layout_****` attributes at the top
- `style` attribute at the bottom
- Tag closer `/>` on its own line, to facilitate ordering and adding attributes.
- Rather than hard coding `android:text`, consider using [Designtime attributes](http://tools.android.com/tips/layout-designtime-attributes) available for Android Studio.

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    >

    <TextView
        android:id="@+id/name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentRight="true"
        android:text="@string/name"
        style="@style/FancyText"
        />

    <include layout="@layout/reusable_part" />

</LinearLayout>
```

As a rule of thumb, attributes `android:layout_****` should be defined in the layout XML, while other attributes `android:****` should stay in a style XML. This rule has exceptions, but in general works fine. The idea is to keep only layout (positioning, margin, sizing) and content attributes in the layout files, while keeping all appearance details (colors, padding, font) in styles files.

The exceptions are:

- `android:id` should obviously be in the layout files
- `android:orientation` for a `LinearLayout` normally makes more sense in layout files
- `android:text` should be in layout files because it defines content
- Sometimes it will make sense to make a generic style defining `android:layout_width` and `android:layout_height` but by default these should appear in the layout files

<a name="styles"></a>
**Use styles.** Almost every project needs to properly use styles, because it is very common to have a repeated appearance for a view. At least you should have a common style for most text content in the application, for example:

```xml
<style name="ContentText">
    <item name="android:textSize">@dimen/font_normal</item>
    <item name="android:textColor">@color/basic_black</item>
</style>
```

Applied to TextViews:

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/price"
    style="@style/ContentText"
    />
```

You probably will need to do the same for buttons, but don't stop there yet. Go beyond and move a group of related and repeated `android:****` attributes to a common style.

<a name="splitstyles"></a>
**Split a large style file into other files.** You don't need to have a single `styles.xml` file. Android SDK supports other files out of the box, there is nothing magical about the name `styles`, what matters are the XML tags `<style>` inside the file. Hence you can have files `styles.xml`, `styles_home.xml`, `styles_item_details.xml`, `styles_forms.xml`. Unlike resource directory names which carry some meaning for the build system, filenames in `res/values` can be arbitrary.

<a name="colorsxml"></a>
**`colors.xml` is a color palette.** There should be nothing in your `colors.xml` other than a mapping from a color name to an RGBA value. This helps avoid repeating RGBA values and as such will make it easy to change or refactor colors, and also will make it explicit how many different colors are being used. Normally for a aesthetic UI, it is important to reduce the variety of colors being used.
 
*So, don't define your colors.xml like this:*

```xml
<resources>
    <color name="button_foreground">#FFFFFF</color>
    <color name="button_background">#2A91BD</color>
</resources>    
```

Instead, do this:

```xml
<resources>
    <!-- grayscale -->
    <color name="white">#FFFFFF</color>
   
    <!-- basic colors -->
    <color name="blue">#2A91BD</color>
</resources>
```

Ask the designer of the application for this palette. The names do not need to be plain color names as "green", "blue", etc. Names such as "brand_primary", "brand_secondary", "brand_negative" are totally acceptable as well.

By referencing the color palette from your styles allows you to abstract the underlying colors from their usage in the app, as per:

- `colors.xml` - defines only the color palette.
- `styles.xml` - defines styles which reference the color palette and reflects the color usage. (e.g. the button foreground is white).
- `activity_main.xml` - references the appropriate style in `styles.xml` to color the button.

If needed, even further separation between underlying colors and style usage can be achieved by defined an additional color resource file which references the color palette. As per:

```xml
<color name="button_foreground">@color/white</color> 
<color name="button_background">@color/blue</color> 
```

Then in styles.xml:

```xml
<style name="AcceptButton">
    <item name="android:foreground">@color/button_foreground</item>
    <item name="android:background">@color/button_background</item>
</style>
```

This approach offers improved color refactoring and more stable style definitions when multiple related styles share similar color and usage properties. However, it comes at the cost of maintaining another set of color mappings. 

<a name="dimensxml"></a>
**Treat dimens.xml like colors.xml.** You should also define a "palette" of typical spacing and font sizes, for basically the same purposes as for colors. A good example of a dimens file:

```xml
<resources>

    <!-- font sizes -->
    <dimen name="font_larger">22sp</dimen>
    <dimen name="font_large">18sp</dimen>
    <dimen name="font_normal">15sp</dimen>
    <dimen name="font_small">12sp</dimen>

    <!-- typical spacing between two views -->
    <dimen name="spacing_huge">40dp</dimen>
    <dimen name="spacing_large">24dp</dimen>
    <dimen name="spacing_normal">14dp</dimen>
    <dimen name="spacing_small">10dp</dimen>
    <dimen name="spacing_tiny">4dp</dimen>

    <!-- typical sizes of views -->
    <dimen name="button_height_tall">60dp</dimen>
    <dimen name="button_height_normal">40dp</dimen>
    <dimen name="button_height_short">32dp</dimen>

</resources>
```

You should use the `spacing_****` dimensions for layouting, in margins and paddings, instead of hard-coded values, much like strings are normally treated. This will give a consistent look-and-feel, while making it easier to organize and change styles and layouts.

**strings.xml**

Name your strings with keys that resemble namespaces, and don't be afraid of repeating a value for two or more keys. Languages are complex, so namespaces are necessary to bring context and break ambiguity.

**Bad**
```xml
<string name="network_error">Network error</string>
<string name="call_failed">Call failed</string>
<string name="map_failed">Map loading failed</string>
```

**Good**
```xml
<string name="error_message_network">Network error</string>
<string name="error_message_call">Call failed</string>
<string name="error_message_map">Map loading failed</string>
```

Don't write string values in all uppercase. Stick to normal text conventions (e.g., capitalize first character). If you need to display the string in all caps, then do that using for instance the attribute [`textAllCaps`](http://developer.android.com/reference/android/widget/TextView.html#attr_android:textAllCaps) on a TextView.

**Bad**
```xml
<string name="error_message_call">CALL FAILED</string>
```

**Good**
```xml
<string name="error_message_call">Call failed</string>
```

<a name="deephierarchy"></a>
**Avoid a deep hierarchy of views.** Sometimes you might be tempted to just add yet another LinearLayout, to be able to accomplish an arrangement of views. This kind of situation may occur:

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    >

    <RelativeLayout
        ...
        >

        <LinearLayout
            ...
            >

            <LinearLayout
                ...
                >

                <LinearLayout
                    ...
                    >
                </LinearLayout>

            </LinearLayout>

        </LinearLayout>

    </RelativeLayout>

</LinearLayout>
```

Even if you don't witness this explicitly in a layout file, it might end up happening if you are inflating (in Java) views into other views.

A couple of problems may occur. You might experience performance problems, because there is a complex UI tree that the processor needs to handle. Another more serious issue is a possibility of [StackOverflowError](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views).

Therefore, try to keep your views hierarchy as flat as possible: learn how to use [ConstraintLayout](https://developer.android.com/training/constraint-layout/index.html), how to [optimize your layouts](http://developer.android.com/training/improving-layouts/optimizing-layout.html) and to use the [`<merge>` tag](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts).

<a name="webviews"></a>
**Beware of problems related to WebViews.** When you must display a web page, for instance for a news article, avoid doing client-side processing to clean the HTML, rather ask for a "*pure*" HTML from the backend programmers. [WebViews can also leak memory](http://stackoverflow.com/questions/3130654/memory-leak-in-webview) when they keep a reference to their Activity, instead of being bound to the ApplicationContext. Avoid using a WebView for simple texts or buttons, prefer the platform's widgets.


### Test Frameworks

**Use [JUnit](https://developer.android.com/training/testing/unit-testing/local-unit-tests.html) for unit testing** Plain, Android dependency-free unit testing on the JVM is best done using [Junit](https://junit.org). 

**Avoid [Robolectric](http://robolectric.org/)** Prior to the improved support for JUnit in the Android build system, Robolectric was promoted as a test framework seeking to provide tests "disconnected from device" for the sake of development speed. However, testing under Robolectric is inaccurate and incomplete as it works by providing mock implementations of the Android platform, which provides no guarantees of correctness. Instead, use a combination of JVM based unit tests and dedicated on-device integration tests.

**[Espresso](https://developer.android.com/training/testing/ui-testing/espresso-testing.html) makes writing UI tests easy.**

**[AssertJ-Android](http://square.github.io/assertj-android/) an AssertJ extension library making assertions easy in Android tests**  Assert-J comes modules easier for you to test Android specific components, such as the Android Support, Google Play Services and Appcompat libraries.

A test assertion will look like:

```java
// Example assertion using AssertJ-Android
assertThat(layout).isVisible()
    .isVertical()
    .hasChildCount(5);
```

### Emulators

The performance of the Android SDK emulator, particularly the x86 variant, has improvement markedly in recent years and is now adequate for most day-to-day development scenarios. However, you should not discount the value of ensuring your application behaves correctly on real devices. Of course, testing on all possible devices is not practical, so rather focus your efforts on devices with a large market share and those most relevant to your app.

### Proguard configuration

[ProGuard](http://proguard.sourceforge.net/) is normally used on Android projects to shrink and obfuscate the packaged code.

Whether you are using ProGuard or not depends on your project configuration. Usually you would configure Gradle to use ProGuard when building a release APK.

```groovy
buildTypes {
    debug {
        minifyEnabled false
    }
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
    }
}
```

In order to determine which code has to be preserved and which code can be discarded or obfuscated, you have to specify one or more entry points to your code. These entry points are typically classes with main methods, applets, midlets, activities, etc.
Android framework uses a default configuration which can be found from `SDK_HOME/tools/proguard/proguard-android.txt`. Using the above configuration, custom project-specific ProGuard rules, as defined in `my-project/app/proguard-rules.pro`, will be appended to the default configuration.

A common problem related to ProGuard is to see the application crashing on startup with `ClassNotFoundException` or `NoSuchFieldException` or similar, even though the build command (i.e. `assembleRelease`) succeeded without warnings.
This means one out of two things:

1. ProGuard has removed the class, enum, method, field or annotation, considering it's not required.
2. ProGuard has obfuscated (renamed) the class, enum or field name, but it's being used indirectly by its original name, i.e. through Java reflection.

Check `app/build/outputs/proguard/release/usage.txt` to see if the object in question has been removed.
Check `app/build/outputs/proguard/release/mapping.txt` to see if the object in question has been obfuscated.

In order to prevent ProGuard from *stripping away* needed classes or class members, add a `keep` options to your ProGuard config:
```
-keep class com.futurice.project.MyClass { *; }
```

To prevent ProGuard from *obfuscating* classes or class members, add a `keepnames`:
```
-keepnames class com.futurice.project.MyClass { *; }
```

Read more at [Proguard](https://www.guardsquare.com/en/proguard/manual/examples) for examples.

**Early in your project, make and test release build** to check whether ProGuard rules are correctly retaining your dependencies. Also whenever you include new libraries or update their dependencies, make a release build and test the APK on a device. Don't wait until your app is finally version "1.0" to make a release build, you might get several unpleasant surprises and a short time to fix them.

**Tip.** Save the `mapping.txt` file for every release that you publish to your users. By retaining a copy of the `mapping.txt` file for each release build, you ensure that you can debug a problem if a user encounters a bug and submits an obfuscated stack trace.

**DexGuard**. If you need hard-core tools for optimizing, and specially obfuscating release code, consider [DexGuard](http://www.saikoa.com/dexguard), a commercial software made by the same team that built ProGuard. It can also easily split Dex files to solve the 65k methods limitation.

### Data storage


#### SharedPreferences

If you only need to persist simple values and your application runs in a single process SharedPreferences is probably enough for you. It is a good default option. 

There are some situations where SharedPreferences are not suitable:

* *Performance*: Your data is complex or there is a lot of it
* *Multiple processes accessing the data*: You have widgets or remote services that run in their own processes and require synchronized data
* *Relational data* Distinct parts of your data are relational and you want to enforce that those relationships are maintained.

You can also store more complex objects by serializing them to JSON to store them and deserializing them when retrieving. You should consider the tradeoffs when doing this as it may not be particularly performant, nor maintainable.

#### ContentProviders

In case SharedPreferences are not enough, you should use the platform standard ContentProviders, which are fast and process safe.

The single problem with ContentProviders is the amount of boilerplate code that is needed to set them up, as well as low quality tutorials. It is possible, however, to generate the ContentProvider by using a library such as [Schematic](https://github.com/SimonVT/schematic), which significantly reduces the effort.

You still need to write some parsing code yourself to read the data objects from the Sqlite columns and vice versa. It is possible to serialize the data objects, for instance with Gson, and only persist the resulting string. In this way you lose in performance but on the other hand you do not need to declare a column for all the fields of the data class.

#### Using an ORM

We generally do not recommend using an Object-Relation Mapping library unless you have unusually complex data and you have a dire need. They tend to be complex and require time to learn. If you decide to go with an ORM you should pay attention to whether or not it is _process safe_ if your application requires it, as many of the existing ORM solutions surprisingly are not.

### Use Stetho 

[Stetho](http://facebook.github.io/stetho/) is a debug bridge for Android applications from Facebook that integrates with the Chrome desktop browser's Developer Tools. With Stetho you can easily inspect your application, most notably the network traffic. It also allows you to easily inspect and edit SQLite databases and the shared preferences in your app. You should, however, make sure that Stetho is only enabled in the debug build and not in the release build variant. 

Another alternative is [Chuck](https://github.com/jgilfelt/chuck) which, although offering slightly more simplified functionality, is still useful for testers as the logs are displayed on the device, rather than in the more complicated connected Chrome browser setup that Stetho requires.

### Use LeakCanary

[LeakCanary](https://github.com/square/leakcanary) is a library that makes runtime detection and identification of memory leaks a more routine part of your application development process. See the library [wiki](https://github.com/square/leakcanary/wiki) for details on configuration and usage. Just remember to configure only the "no-op" dependency in your release build!

### Use continuous integration

Continuous integration systems let you automatically build and test your project every time you push updates to version control. Continuous integration also runs static code analysis tools, generates the APK files and distributes them.
[Lint](https://developer.android.com/studio/write/lint.html) and [Checkstyle](http://checkstyle.sourceforge.net/) are tools that ensure the code quality while [Findbugs](http://findbugs.sourceforge.net/) looks for bugs in the code.
   
There is a wide variety of continuous integration software which provide different features. Pricing plans might be for free if your project is open-sourced.
[Jenkins](https://jenkins.io/) is a good option if you have a local server at your disposal, on the other hand [Travis CI](https://travis-ci.org/) is also a recommended choice if you plan to use a cloud-based continuous integration service.

### Thanks to

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton and other Futurice developers for sharing their knowledge on Android development.

### Acknowledgements

<p align="center">
  <img alt="logo" src="https://raw.githubusercontent.com/futurice/spiceprogram/gh-pages/assets/img/logo/chilicorn_no_text-256.png" width="220"/>
</p>

This project is sponsored by [**Spice Program**](http://spiceprogram.org/chilicorn-history/), our open source and social impact program made with love by [**Futurice**](http://www.futurice.com).

### License

[Futurice Oy](http://www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)
