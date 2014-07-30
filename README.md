# Best practices in Android develoment

Lessons learned from Android developers in Futurice. Avoid reinventing the wheel by following these guidelines.

Feedback and criticism are welcomed, feel free to open an issue or send a pull request.

## Summary

#### Use Gradle and its recommended project structure
#### Put passwords and sensitive data in gradle.properties
#### Don't write your own HTTP client, use a library
#### Use the Jackson library to parse JSON data
#### Use Volley or Retrofit+OkHttp+Picasso for networking and images
#### Use Fragments to represent a UI screen
#### Use Activities just to manage Fragments
#### Layout XMLs are code, organize them well
#### Use styles to avoid duplicate attributes in layout XMLs
#### Use multiple style files to avoid a single huge one
#### Keep your colors.xml short and DRY, just define the palette
#### Also keep dimens.xml DRY, define generic constants
#### Do not make a deep hierarchy of ViewGroups
#### Avoid using WebViews as much as you can


----------

### Android SDK

Place your [Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools) in the `/opt` directory or some other application-independent location. Some IDEs include the SDK when installed, and may place it under the same directory as the IDE. This can be bad when you need to upgrade (or reinstall) the IDE, or when changing IDEs. Putting it in `/opt` makes it independent of IDE.

### Build system

Your default option should be [Gradle](http://tools.android.com/tech-docs/new-build-system). Ant is much more limited and also more verbose. With Gradle, it's simple to:

- Build different flavours or variants of your app
- Make simple script-like tasks
- Manage and download dependencies
- Customize keystores
- And more

Android's Gradle plugin is also being actively developed by Google while as the new standard build system.

### Project structure

There are two popular options: the old Ant & Eclipse ADT project structure, and the new Gradle & Android Studio project structure. You can make the decision whether to use one or the other, although we recommend the new project structure. If using the old, try to provide also a Gradle configuration `build.gradle`.

Old structure:

```
old-structure
├─ assets
├─ libs
├─ res
├─ src
│  └─ com/futurice/project
├─ AndroidManifest.xml
├─ build.gradle
├─ project.properties
└─ proguard-rules.pro
```

New structure:

```
new-structure
├─ library-foobar
├─ app
│  ├─ libs
│  ├─ src
│  │  ├─ androidTest
│  │  │  └─ java
│  │  │     └─ com/futurice/project
│  │  └─ main
│  │     ├─ java
│  │     │  └─ com/futurice/project
│  │     ├─ res
│  │     └─ AndroidManifest.xml
│  ├─ build.gradle
│  └─ proguard-rules.pro
├─ build.gradle
└─ settings.gradle
```

The main difference is that the new structure explicitly separates 'source sets' (`main`, `androidTest`), a concept from Gradle. You could, for instance, add source sets 'paid' and 'free' into `src` which will have source code for the paid and free flavours of your app.

Having a top-level `app` is useful to distinguish your app from other library projects (e.g., `library-foobar`) that will be referenced in your app. The `settings.gradle` then keeps references to these library projects, which `app/build.gradle` can reference to.

### Gradle configuration

**General structure.** Follow [Google's guide on Gradle for Android](http://tools.android.com/tech-docs/new-build-system/user-guide)

**Small tasks.** Instead of (shell, Python, Perl, etc) scripts, you can make tasks in Gradle. Just follow [Gradle's documentation](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF) for more details.

**Passwords.** In your app's `build.gradle` you will need to define the `signingConfigs` for the release build. Here is what you should avoid:

_Don't do this_. This would appear in the version control system.

```groovy
signingConfigs {
    release {
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

That file is automatically imported by gradle, so you can use it in `build.gradle` as such:

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

### IDEs and text editors

**Use whatever editor, but it must play nicely with the project structure.** Editors are a personal choice, and it's your responsibility to get your editor functioning according to the project structure and build system.

The most recommended IDE at the moment is [Android Studio](https://developer.android.com/sdk/installing/studio.html), because it is developed by Google, is closest to Gradle, uses the new project structure by default, is finally in beta stage, and is tailored for Android development.

You can use [Eclipse ADT](https://developer.android.com/sdk/installing/index.html?pkg=adt) if you wish, but you need to configure it, since it expects the old project structure and Ant for building. You can even use a plain text editor like Vim, Sublime Text, or Emacs. In that case, you will need to use Gradle and `adb` on the command line. If Eclipse's integration with Gradle is not working for you, your options are using the command line just to build, or migrating to Android Studio.

Whatever you use, just make sure Gradle and the new project structure remain as the official way of building the application, and avoid adding your editor-specific configuration files to the version control system. For instance, avoid adding an Ant `build.xml` file. Especially don't forget to keep `build.gradle` up-to-date and functioning if you are changing build configurations in Ant. Also, be kind to other developers, don't force them to change their tool of preference.

### Libraries

**[Jackson](http://wiki.fasterxml.com/JacksonHome)** is a Java library for converting Objects into JSON and vice-versa. [Gson](https://code.google.com/p/google-gson/) is a popular choice for solving this problem, however we find Jackson to be more performant since it supports alternative ways of processing JSON: streaming, in-memory tree model, and traditional JSON-POJO data binding. Other alternatives: [Json-smart](https://code.google.com/p/json-smart/) and [Boon JSON](https://github.com/RichardHightower/boon/wiki/Boon-JSON-in-five-minutes)

**Networking, caching, and images.** There are a couple of battle-proven solutions for performing requests to backend servers, which you should use perform considering implementing your own client. Use [Volley](https://android.googlesource.com/platform/frameworks/volley) or [Retrofit](http://square.github.io/retrofit/). Volley also provides helpers to load and cache images. If you choose Retrofit, consider [Picasso](http://square.github.io/picasso/) for loading and caching images, and [OkHttp](http://square.github.io/okhttp/) for efficient HTTP requests. All three Retrofit, Picasso and OkHttp are created by the same company, so they complement each other nicely.

**RxJava** is a library for Reactive Programming, in other words, handling asynchronous events. It is a powerful and promising paradigm, which can also be confusing since it's so different. We recommend to take some caution before using this library to architect the entire application. There are some projects done by us using RxJava, if you need help talk to one of these people: Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, (any one else?). We have written some blog posts on it: [[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android), [[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754), [[4]](http://blog.futurice.com/android-development-has-its-own-swift).

If you have no previous experience with Rx, start by applying it only for responses from the API. Alternatively, start by applying it for simple UI event handling, like click events or typing events on a search field. If you are confident in your Rx skills and want to apply it to the whole architecture, then write Javadocs on all the tricky parts. Keep in mind that another programmer unfamiliar to RxJava might have a very hard time maintaining the project. Do your best to help him understand your code and also Rx.

### Activities and Fragments

[Fragments](http://developer.android.com/guide/components/fragments.html) should be your default option for implementing a UI screen in Android. Fragments are reusable user interfaces that can be composed in your application. We recommend using fragments instead of [activities](http://developer.android.com/guide/components/activities.html) to represent a user interface screen, here are some reasons why:

- **Solution for multi-pane layouts.** Fragments were primarily introduced for extending phone applications to tablet screens, so that you can have both panes A and B on a tablet screen, while either A or B occupy an entire phone screen. If your application is implemented in fragments from the beginning, you will make it easier later to adapt your application to different form-factors.

- **Proper screen-to-screen communication.** Android's API does not provide a proper way of sending complex data (e.g., some Java Object) from one activity to another activity. With fragments, however, you can use the instance of an activity as a channel of communication between its child fragments.

- **Many Android classes expect fragments.** The [API for tabs on the ActionBar](http://developer.android.com/reference/android/app/ActionBar.html#newTab()) expects the tab contents to be fragments. Also, if using Google Maps API v2, the [recommended solution](https://developers.google.com/maps/documentation/android/) is [MapFragment](http://developer.android.com/reference/com/google/android/gms/maps/MapFragment.html), even though [MapView](http://developer.android.com/reference/com/google/android/gms/maps/MapView.html) exists.

- **Easy to implement swiping transitions between screens.** If a UI screen is a fragment in your application, you can use `FragmentPagerAdapter` to contain a collection of fragments and implement smooth and interactive transitioning between them. For instance, horizontal swiping of screens.

- **Intelligent behavior for "back".** The [FragmentManager](http://developer.android.com/reference/android/app/FragmentManager.html) allows you to perform transactions to change the fragments inside an activity. Hence, the FragmentManager manages the "state" of fragments. The back button/action will be handled by the FragmentManager to go back to the previous "fragments state". This is more advanced than the [activity stack](http://developer.android.com/guide/components/tasks-and-back-stack.html).

- **Fragments are generic enough to not be UI-only.** You can have a [fragment without a UI](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI) that works as background workers for the activity. You can take that idea further to create a [fragment to contain the logic for changing fragments](http://stackoverflow.com/questions/12363790/how-many-activities-vs-fragments/12528434#12528434), instead of having that logic in the activity.

- **Even the ActionBar can be managed from within fragments.** You can choose to have one Fragment without a UI with the sole purpose of managing the ActionBar, or you can choose to have each currently visible Fragment add its own action items to the parent Activity's ActionBar. [Read more here](http://www.grokkingandroid.com/adding-action-items-from-within-fragments/).

That being said, we advise not to use [nested fragments](https://developer.android.com/about/versions/android-4.2.html#NestedFragments) extensively, because [matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/) can occur. Use nested fragments only when it makes sense (for instance, fragments in a horizontally-sliding ViewPager inside a screen-like fragment) or if it's a well-informed decision.

On an architectural level, your app should have a top-level activity that contains most of the business-related fragments. You can also have some other supporting activities, as long as their communication with the main activity is simple and can be limited to [`Intent.setData()`](http://developer.android.com/reference/android/content/Intent.html#setData(android.net.Uri)) or [`Intent.setAction()`](http://developer.android.com/reference/android/content/Intent.html#setAction(java.lang.String)) or similar.

### Java packages architecture

Java architectures for Android applications can be roughly approximated in [Model-View-Controller](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller). In Android, [Fragment and Activity are actually controller classes](http://www.informit.com/articles/article.aspx?p=2126865). On the other hand, they are explicity part of the user interface, hence are also views.

For this reason, it is hard to classify fragments (or activities) as strictly controllers or views. It's better to let them stay in their own `fragments` package. Activities can stay on the top-level package as long as you follow the advice of the previous section. If you are planning to have more than 2 or 3 activities, then make also an `activities` package.

Otherwise, the architecture can look like a typical MVC, with a `models` package containing POJOs to be populated through the JSON parser with API responses, and a `views` package containing your custom Views, notifications, action bar views, widgets, etc. Adapters are a gray matter, living between data and views. However, they typically need to export some View via `getView()`, so you can include the `adapters` subpackage inside `views`.

Some controller classes are application-wide and close to the Android system. These can live in the `services` package. Miscellaneous data processing classes, such as "DateUtils", stay in the `utils` package. Classes that are responsible for interacting with the backend stay in the `network` package.

All in all, ordered from the closest-to-backend to the closest-to-the-user:

```
com.futurice.project
├─ network
├─ models
├─ services
├─ utils
├─ fragments
└─ views
   ├─ adapters
   ├─ actionbar
   ├─ widgets
   └─ notifications
```

### Resources

**Naming.** Follow the convention of prefixing the type, as in `type_foo_bar.xml`. Examples: `fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`.

**Organizing layout XMLs.** If you're unsure how to format a layout XML, the following convention may help.

- One attribute per line, indented by 4 spaces
- `android:id` as the first attribute always
- `android:layout_****` attributes at the top
- `style` attribute at the bottom
- Tag closer `/>` on its own line, to facilitate ordering and adding attributes.

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

**Split a large style file into other files.** You don't need to have a single `styles.xml` file. Android SDK supports other files out of the box, there is nothing magical about the name `styles`, what matters are the XML tags `<style>` inside the file. Hence you can have files `styles.xml`, `styles_home.xml`, `styles_item_details.xml`, `styles_forms.xml`. Unlike resource directory names which carry some meaning for the build system, filenames in `res/values` can be arbitrary.

**`colors.xml` is a color palette.** There should be nothing else in your `colors.xml` than just a mapping from a color name to an RGBA value. Do not use it to define RGBA values for different types of buttons.

*Don't do this:*

```xml
<resources>
    <color name="button_foreground">#FFFFFF</color>
    <color name="button_background">#2A91BD</color>
    <color name="comment_background_inactive">#5F5F5F</color>
    <color name="comment_background_active">#939393</color>
    <color name="comment_foreground">#FFFFFF</color>
    <color name="comment_foreground_important">#FF9D2F</color>
    ...
    <color name="comment_shadow">#323232</color>
```

You can easily start repeating RGBA values in this format, and that makes it complicated to change a basic color if needed. Also, those definitions are related to some context, like "button" or "comment", and should live in a button style, not in `colors.xml`.

Instead, do this:

```xml
<resources>

    <!-- grayscale -->
    <color name="white"     >#FFFFFF</color>
    <color name="gray_light">#DBDBDB</color>
    <color name="gray"      >#939393</color>
    <color name="gray_dark" >#5F5F5F</color>
    <color name="black"     >#323232</color>

    <!-- basic colors -->
    <color name="green" >#27D34D</color>
    <color name="blue"  >#2A91BD</color>
    <color name="orange">#FF9D2F</color>
    <color name="red"   >#FF432F</color>

</resources>
```

Ask for this palette from the designer of the application. Formatting colors as such will make it easy to change or refactor colors, and also will make it explicit how many different colors are being used. Normally for a aesthetic UI, it is important to reduce the variety of colors being used.

**Treat dimens.xml like colors.xml.** You should also define a "palette" of typical spacing and font sizes, for basically the same purposes as for colors. A good example of a dimens file:

```xml
<resources>

    <!-- font sizes -->
    <dimen name="font_larger">22dp</dimen>
    <dimen name="font_large">18dp</dimen>
    <dimen name="font_normal">15dp</dimen>
    <dimen name="font_small">12dp</dimen>

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

A couple of problems may occur. You might experience performance problems, because there are is a complex UI tree that the processor needs to handle. Another more serious issue is a possibility of [StackOverflowError](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views).

Therefore, try to keep your views hierarchy as flat as possible: learn how to use [RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html), how to [optimize your layouts](http://developer.android.com/training/improving-layouts/optimizing-layout.html) and to use the [`<merge>` tag](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts).

**WebViews are hard to layout and style.** Avoid using a WebView whenever you can, because it is hard or impossible (due to difference among devices) to style content inside it consistently with the native views in the application. You can use CSS in the WebView, but it is not a robust solution.

### Test frameworks

TODO Robotium, Mockito, Roboeletric, etc.

### Proguard configuration

TODO *Can someone please contribute hints and tips on Proguard configs?*

- Always save proguard's mapping.txt with the builds
- etc

### Thanks to

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, and other Futurice developers for sharing their knowledge on Android development.
