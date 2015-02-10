# Android 개발 모범 사례

다음은 [Futurice](http://www.futurice.com)의 Android 개발자들로부터 학습한 내용들이다. 이 가이드를 따라가면서 바퀴를 재개발하는 것(Reinventing the wheel)을 피하도록 하자. iOS나 Windows Phone 개발에도 관심이 있다면, [**iOS Good Practices**](https://github.com/futurice/ios-good-practices) 혹은 [**Windows App Development Best Practices**](https://github.com/futurice/windows-app-development-best-practices) 문서들도 확인해보자.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--best--practices-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1091)

## 요약

#### Gradle을 사용하자. 이는 권장되는 프로젝트 구조이다.
#### gradle.properties에 비밀번호나 민감한 데이터들을 넣어두자.
#### HTTP 클라이언트를 직접 작성하지 말고, Volley나 OkHttp 라이브러리들을 사용하자.
#### JSON 데이터를 파싱하는 데에는 Jackson 라이브러리를 사용하자.
#### 65,000 메소드 수 제한을 방지하기 위해 Guava는 피하고 몇 가지의 라이브러리들만을 사용하자.
#### UI 화면을 표현하는 데에 Fragment들을 사용하자.
#### Fragment들을 관리하는 것은 Activity들이 맡도록 하자.
#### 레이아웃 XML들 또한 코드이다. 그 것들을 잘 관리하자.
#### 레이아웃 XML에서 중복된 속성들을 피하기 위해 style을 사용하자.
#### 하나의 style이 방대해지는 것을 피하기 위해 여러가지 style 파일들을 사용하자.
#### colors.xml을 짧게, 중복 없이 유지하고, 팔렛트처럼 정의해두자.
#### dimens.xml 또한 중복 없이, 일반 상수로 정의하자.
#### ViewGroup에 계층을 깊게 형성하지 않도록 하자.
#### WebView에 클라이언트 측 프로세싱을 피하고, 여러 누수들에 유의하자.
#### 유닛 테스트에는 Robolectric를 사용하고, UI 테스트에는 Robotium을 사용하자.
#### 에뮬레이터로는 Genymotion를 사용하자.
#### 항상 ProGuard 혹은 DexGuard를 사용하자.


----------

### Android SDK

[Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools)를 홈 디렉토리나 다른 애플리케이션에 독립적인 위치에 두자. 몇몇 IDE들은 설치시에 SDK를 해당 IDE와 같은 경로에 포함시키는데, 이는 IDE를 업그레이드(혹은 재설치)하거나 IDE가 변경될 때 불편하다. 또한 IDE가 root 아래에 있지 않고 user 아래에서 동작할 경우, SDK를 sudo 권한을 요구하는 시스템 레벨의 디렉토리에 두지 않도록 하자.

### 빌드 시스템

기본 옵션은 [Gradle](http://tools.android.com/tech-docs/new-build-system)이다. Ant는 상당히 제한적이고 내용이 장황하다. Gradle을 사용하면, 다음 항목들이 간단해진다.

- 앱의 각기 다른 Flavor들과 Varient들을 빌드할 수 있다.
- Task들을 간단한 스크립트처럼 만들 수 있다.
- 여러 Dependency들을 관리하고 다운로드할 수 있다.
- Keystore들을 커스터마이즈할 수 있다.
- 기타 등등

Android의 Gradle 플러그인은 새로운 표준 빌드 시스템으로서 구글에 의해 활발하게 개발되고 있다.

### 프로젝트 구조

두 가지 많이 쓰이는 옵션들이 있다: 낡은 Ant & Eclipse ADT 프로젝트 구조, 새로운 Gradle & Android Studio 프로젝트 구조가 있는데, 새로운 프로젝트 구조를 선택하자. 만약 낡은 구조를 사용하고 있다면, 레거시로 판단하고 새로운 구조로 포팅하는 작업을 시작하자.

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

주된 차이점은 Gradle에서 온 개념인데, 새로운 구조가 'source sets' (`main`, `androidTest`)를 명시적으로 분리시켜둔다는 것이다. 예를 들어 `src`에 paid와 free라는 각기 다른 Flavor에 해당하는 소스코드를 갖는 'paid'라는 소스 셋과 'free'라는 소스 셋을 추가할 수 있다.

최상위 레벨 `app`을 갖는 것은 앱과 앱에서 참조된 다른 라이브러리 프로젝트들(e.g., `library-foobar`)을 구별하는 데에 유용하다. `settings.gradle`은 `app/build.gradle`에서 참조할 수 있는 이러한 라이브러리 프로젝트들을 보관한다.

### Gradle 설정

**일반적인 구조.** [Google's guide on Gradle for Android](http://tools.android.com/tech-docs/new-build-system/user-guide)를 확인하자.

**작은 Task들.** 스크립트들(shell, Python, Perl, etc) 대신, Gradle의 Task들을 만들 수 있다. [Gradle's documentation](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF)에서 더 자세한 내용을 확인하자.

**비밀번호** 앱의 `build.gradle`에 릴리즈 빌드를 위한 `signingConfigs` 정의가 필요할 것이다. 다음은 피하자.

_이렇게 해서는 안된다_. 이는 버전 관리 시스템에서 나타날 것이다.

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

대신, `gradle.properties` 파일을 만들자. 이는 버전 관리 시스템에 추가되어선 _안된다_:

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```

위 파일은 gradle에 자동으로 임포트되어, `build.gradle`에 이렇게 사용할 수 있다:

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

**jar 파일 임포트 대신 Maven을 선호하자.** 프로젝트에 jar 파일을 명시적으로 포함시킬 경우, 이들은 `2.1.1`처럼 특정하게 고정된 버전이 된다. jar를 다운로드하고, 업데이트하는 것은 귀찮은 일이다. 그러나 Maven은 이 문제를 적절하게 해결해줄 것이고, 또한 이는 Android Gradle 빌드에서 장려되는 방식이다. `2.1.+` 버전의 범위를 설정할 수 있고, Maven은 이러한 패턴에 매칭되는 가장 최신 버전으로 업데이트해줄 것이다. 예를 들자면 이렇다:

```groovy
dependencies {
    compile 'com.netflix.rxjava:rxjava-core:0.19.+'
    compile 'com.netflix.rxjava:rxjava-android:0.19.+'
    compile 'com.fasterxml.jackson.core:jackson-databind:2.4.+'
    compile 'com.fasterxml.jackson.core:jackson-core:2.4.+'
    compile 'com.fasterxml.jackson.core:jackson-annotations:2.4.+'
    compile 'com.squareup.okhttp:okhttp:2.0.+'
    compile 'com.squareup.okhttp:okhttp-urlconnection:2.0.+'
}
```

### IDE와 텍스트 에디터

**프로젝트 구조를 다루는 데에 용이한 에디터라면 무엇이든 사용해도 좋다.** 에디터는 개인적인 선택이고, 그 에디터가 프로젝트 구조와 프로젝트 빌드 시스템에 따라 기능하도록 하는 것은 개발자의 몫이다.

현재 가장 추천하는 IDE는 [Android Studio](https://developer.android.com/sdk/installing/studio.html)이다. Google이 개발했고, Gradle에 가장 밀접하며, 기본적으로 새로운 프로젝트 구조를 사용하는데다가 안정화 단계에 들어가 Android 개발에 잘 맞추어져 있기 때문이다.

원한다면 [Eclipse ADT](https://developer.android.com/sdk/installing/index.html?pkg=adt)를 사용해도 좋지만, 빌드하는 데에 낡은 프로젝트 구조와 Ant를 사용하기 때문에 이에 대한 설정이 필요하다. Vim, Sublime Text, Emacs같은 플레인 텍스트 에디터를 사용할 수도 있다. 이 경우에는 Gradle과 `adb`를 커맨드라인에서 사용해야 한다. Eclipse의 Gradle 사용이 제대로 작동하지 않는다면, 커맨드라인으로 빌드하거나 Android Studio로 옮기자. ADT 플러그인이 deprecate되었기 때문에, 이 것이 가장 좋은 옵션일 것이다.

무엇을 사용하든, 애플리케이션 빌드의 공식적인 방법인 Gradle과 새로운 프로젝트 구조를 따르고, 특정 에디터를 따르는 설정 파일을 버전 관리 시스템에 추가하는 것을 피하는 것만 명심하자. 예를 들면, Ant의 `build.xml` 파일들은 추가하지 않도록 한다. 특히 Ant의 빌드 설정을 변경하고 있다면 `build.gradle`을 최신의 상태로 기능하도록 하는 것을 잊지말자. 또한 다른 개발자들에게 친절해지자. 그들의 설정을 바꾸도록 강요하지 않아야한다.

### 라이브러리

**[Jackson](http://wiki.fasterxml.com/JacksonHome)**은 Object를 JSON으로, 혹은 그 반대로 변환해주는 Java 라이브러리이다. [Gson](https://code.google.com/p/google-gson/)이 이 문제를 해결하는 데에 많이 쓰이긴 하지만, 스트리밍, 인메모리 트리 모델, 전통적인 JSON-POJO 데이터 바인딩과 같은 여러 대안들을 지원하는 Jackson이 더 고성능일 것이다. 하지만 명심하자. Jackson이 GSON보다 더 큰 라이브러리이기 때문에, 65,000 메소드 수 제한에 부딪힌 경우라면 GSON을 사용하는 것이 나을 수도 있다. 다른 대안으로는 [Json-smart](https://code.google.com/p/json-smart/)과 [Boon JSON](https://github.com/RichardHightower/boon/wiki/Boon-JSON-in-five-minutes)이 있다.

**네트워킹, 캐싱, 이미지.** 백엔드 서버로의 요청 처리에 대해 클라이언트를 구현하고 처리하는 두 가지 검증된 해결책이 있다. [Volley](https://android.googlesource.com/platform/frameworks/volley) 혹은 [Retrofit](http://square.github.io/retrofit/)을 사용하자. Volley는 이미지를 불러오고 캐싱하는 도우미를 제공한다. Retrofit을 선택한다면, 이미지 로딩과 캐싱에는 [Picasso](http://square.github.io/picasso/)를, 효율적인 HTTP 요청에는 [OkHttp](http://square.github.io/okhttp/)를 고려해보자. 이 모든 세가지의 라이브러리들은 같은 회사에서 개발되어 서로 상호보완이 매우 용이하다. [OkHttp can also be used in connection with Volley](http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835).

**RxJava** 비동기 이벤트를 처리하는 Reactive Programming을 위한 라이브러리이다. 이는 매우 강력하고 유망한 패러다임으로, 너무 다른 점이 많아 혼란스러울 수 있다. 모든 애플리케이션에서 아키텍트들에게 이 라이브러리를 쓰기 전 주의할 것을 추천한다. RxJava를 이용한 몇 가지 프로젝트가 있는데, 필요하다면 이 사람들에게서 도움을 구하자: Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen. 작성된 블로그 포스트도 있다: [[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android), [[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754), [[4]](http://blog.futurice.com/android-development-has-its-own-swift).

Rx에 대한 경험이 없다면, API 응답 처리에만 적용해보자. 다른 방법으로는 클릭 이벤트나 검색 타이핑 이벤트와 같은 간단한 UI 이벤트 처리에 적용해볼 수도 있다. Rx 기술에 자신감이 생겨 모든 설계에 적용하고 싶다면, 모든 까다로운 부분들에 Javadocs를 작성하자. RxJava에 익숙하지 않은 다른 프로그래머가 프로젝트를 유지, 보수하는 데에 어려움이 있을 수 있다는 것을 명심해야 한다. 그들의 Rx와 코드 이해에 대해 최선을 다해 도움을 주자.

**[Retrolambda](https://github.com/evant/gradle-retrolambda)**는 Android 혹은 다른 pre-JDK8 플랫폼에서 Lambda 표현 문법을 사용할 수 있도록 하는 Java 라이브러리이다. 이 라이브러리는 특히 RxJava와 같이 기능 위주 스타일의 코드를 더욱 타이트하고 읽기 좋게 만들어준다. 사용하려면, JDK8을 설치하고 이를 Android Studio 프로젝트 대화상자에서 SDK 경로로 설정한 후, `JAVA8_HOME`과 `JAVA7_HOME` 환경변수를 설정한 뒤 프로젝트 root의 build.gradle을 이렇게 설정한다:

```groovy
dependencies {
    classpath 'me.tatarka:gradle-retrolambda:2.4.+'
}
```

그리고 각각 모듈들의 build.gradle에 아래 내용을 추가하자.

```groovy
apply plugin: 'retrolambda'

android {
    compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}

retrolambda {
    jdk System.getenv("JAVA8_HOME")
    oldJdk System.getenv("JAVA7_HOME")
    javaVersion JavaVersion.VERSION_1_7
}
```

Android Studio는 Java8 lambda의 코드 지원을 제공한다. 만약 lambda가 처음이라면, 다음 항목들을 따라 시작해보자:

- 하나의 메소드를 갖는 모든 인터페이스들은 "lambda와 밀접"하고, 더욱 타이트한 문법으로 묶일 수 있다.
- 만약 파라메터들이 의심스럽다면, 일반 익명 내부 클래스를 작성하고 Android Studio가 lambda로 묶어주도록 해보자.

**Dex 메소드 제한을 유의하고, 많은 라이브러리 사용을 피하자.** Android 앱들이 dex 파일로 패키징될 때, 65,536개의 참조 메소드 수 제한을 갖는다[[1]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [[2]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [[3]](http://jakewharton.com/play-services-is-a-monolith/). 제한된 메소드 수를 넘어서면 컴파일시 Fatal error를 보게될 것이다. 그렇기 때문에, 최소한의 라이브러리들을 사용하고, [dex-method-counts](https://github.com/mihaip/dex-method-counts) 툴을 사용하여 제한된 수보다 적게 유지하기 위해 어떤 라이브러리들을 사용할지 결정하자. 특히 Guava 라이브러리는 피하자. 이 라이브러리는 13,000개가 넘는 메소드를 가지고 있다.

### Activities and Fragments

[Fragments](http://developer.android.com/guide/components/fragments.html) should be your default option for implementing a UI screen in Android. Fragments are reusable user interfaces that can be composed in your application. We recommend using fragments instead of [activities](http://developer.android.com/guide/components/activities.html) to represent a user interface screen, here are some reasons why:

- **Solution for multi-pane layouts.** Fragments were primarily introduced for extending phone applications to tablet screens, so that you can have both panes A and B on a tablet screen, while either A or B occupy an entire phone screen. If your application is implemented in fragments from the beginning, you will make it easier later to adapt your application to different form-factors.

- **Screen-to-screen communication.** Android's API does not provide a proper way of sending complex data (e.g., some Java Object) from one activity to another activity. With fragments, however, you can use the instance of an activity as a channel of communication between its child fragments. Even though this is better than Activity-to-Activity communication, you might want to consider an Event Bus architecture, using e.g. [Otto](https://square.github.io/otto/) or [greenrobot EventBus](https://github.com/greenrobot/EventBus), as a cleaner approach. RxJava can also be used for implementing an Event Bus, in case you want to avoid adding yet another library.

- **Fragments are generic enough to not be UI-only.** You can have a [fragment without a UI](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI) that works as background workers for the activity. You can take that idea further to create a [fragment to contain the logic for changing fragments](http://stackoverflow.com/questions/12363790/how-many-activities-vs-fragments/12528434#12528434), instead of having that logic in the activity.

- **Even the ActionBar can be managed from within fragments.** You can choose to have one Fragment without a UI with the sole purpose of managing the ActionBar, or you can choose to have each currently visible Fragment add its own action items to the parent Activity's ActionBar. [Read more here](http://www.grokkingandroid.com/adding-action-items-from-within-fragments/).

That being said, we advise not to use [nested fragments](https://developer.android.com/about/versions/android-4.2.html#NestedFragments) extensively, because [matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/) can occur. Use nested fragments only when it makes sense (for instance, fragments in a horizontally-sliding ViewPager inside a screen-like fragment) or if it's a well-informed decision.

On an architectural level, your app should have a top-level activity that contains most of the business-related fragments. You can also have some other supporting activities, as long as their communication with the main activity is simple and can be limited to [`Intent.setData()`](http://developer.android.com/reference/android/content/Intent.html#setData(android.net.Uri)) or [`Intent.setAction()`](http://developer.android.com/reference/android/content/Intent.html#setAction(java.lang.String)) or similar.

### Java packages architecture

Java architectures for Android applications can be roughly approximated in [Model-View-Controller](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller). In Android, [Fragment and Activity are actually controller classes](http://www.informit.com/articles/article.aspx?p=2126865). On the other hand, they are explicity part of the user interface, hence are also views.

For this reason, it is hard to classify fragments (or activities) as strictly controllers or views. It's better to let them stay in their own `fragments` package. Activities can stay on the top-level package as long as you follow the advice of the previous section. If you are planning to have more than 2 or 3 activities, then make also an `activities` package.

Otherwise, the architecture can look like a typical MVC, with a `models` package containing POJOs to be populated through the JSON parser with API responses, and a `views` package containing your custom Views, notifications, action bar views, widgets, etc. Adapters are a gray matter, living between data and views. However, they typically need to export some View via `getView()`, so you can include the `adapters` subpackage inside `views`.

Some controller classes are application-wide and close to the Android system. These can live in a `managers` package. Miscellaneous data processing classes, such as "DateUtils", stay in the `utils` package. Classes that are responsible for interacting with the backend stay in the `network` package.

All in all, ordered from the closest-to-backend to the closest-to-the-user:

```
com.futurice.project
├─ network
├─ models
├─ managers
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
    <color name="green">#27D34D</color>
    <color name="blue">#2A91BD</color>
    <color name="orange">#FF9D2F</color>
    <color name="red">#FF432F</color>

</resources>
```

Ask for this palette from the designer of the application. The names do not need to be color names as "green", "blue", etc. Names such as "brand_primary", "brand_secondary", "brand_negative" are totally acceptable as well. Formatting colors as such will make it easy to change or refactor colors, and also will make it explicit how many different colors are being used. Normally for a aesthetic UI, it is important to reduce the variety of colors being used.

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

**Beware of problems related to WebViews.** When you must display a web page, for instance for a news article, avoid doing client-side processing to clean the HTML, rather ask for a "*pure*" HTML from the backend programmers. [WebViews can also leak memory](http://stackoverflow.com/questions/3130654/memory-leak-in-webview) when they keep a reference to their Activity, instead of being bound to the ApplicationContext. Avoid using a WebView for simple texts or buttons, prefer TextViews or Buttons.


### Test frameworks

Android SDK's testing framework is still infant, specially regarding UI tests. Android Gradle currently implements a test task called [`connectedAndroidTest`](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing) which runs JUnit tests that you created, using an [extension of JUnit with helpers for Android](http://developer.android.com/reference/android/test/package-summary.html). This means you will need to run tests connected to a device, or an emulator. Follow the official guide [[1]](http://developer.android.com/tools/testing/testing_android.html) [[2]](http://developer.android.com/tools/testing/activity_test.html) for testing.

**Use [Robolectric](http://robolectric.org/) only for unit tests, not for views.** It is a test framework seeking to provide tests "disconnected from device" for the sake of development speed, suitable specially for unit tests on models and view models. However, testing under Robolectric is inaccurate and incomplete regarding UI tests. You will have problems testing UI elements related to animations, dialogs, etc, and this will be complicated by the fact that you are "walking in the dark" (testing without seeing the screen being controlled).

**[Robotium](https://code.google.com/p/robotium/) makes writing UI tests easy.** You do not need Robotium for running connected tests for UI cases, but it will probably be beneficial to you because of its many helpers to get and analyse views, and control the screen. Test cases will look as simple as:

```java
solo.sendKey(Solo.MENU);
solo.clickOnText("More"); // searches for the first occurence of "More" and clicks on it
solo.clickOnText("Preferences");
solo.clickOnText("Edit File Extensions");
Assert.assertTrue(solo.searchText("rtf"));
```

### Emulators

If you are developing Android apps as a profession, buy a license for the [Genymotion emulator](http://www.genymotion.com/). Genymotion emulators run at a faster frames/sec rate than typical AVD emulators. They have tools for demoing your app, emulating network connection quality, GPS positions, etc. They are also ideal for connected tests. You have access to many (not all) different devices, so the cost of a Genymotion license is actually much cheaper than buying multiple real devices.

Caveats are: Genymotion emulators don't ship all Google services such as Google Play Store and Maps. You might also need to test Samsung-specific APIs, so it's necessary to have a real Samsung device.

### Proguard configuration

[ProGuard](http://proguard.sourceforge.net/) is normally used on Android projects to shrink and obfuscate the packaged code.

Whether you are using ProGuard or not depends on your project configuration. Usually you would configure gradle to use ProGuard when building a release apk.

```groovy
buildTypes {
    debug {
        minifyEnabled false
    }
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        proguardFiles 'proguard-rules.pro'
    }
}
```

In order to determine which code has to be preserved and which code can be discarded or obfuscated, you have to specify one or more entry points to your code. These entry points are typically classes with main methods, applets, midlets, activities, etc.
Android framework uses a default configuration which can be found from `SDK_HOME/tools/proguard/proguard-android.txt`. Custom project-specific proguard rules, as defined in `my-project/app/proguard-rules.pro`, will be appended to the default configuration.

A common problem related to ProGuard is to see the application crashing on startup with `ClassNotFoundException` or `NoSuchFieldException` or similar, even though the build command (i.e. `assembleRelease`) succeeded without warnings.
This means one out of two things:

1. ProGuard has removed the class, enum, method, field or annotation, considering it's not required.
2. ProGuard has obfuscated (renamed) the class, enum or field name, but it's being used indirectly by its original name, i.e. through Java reflection.

Check `app/build/outputs/proguard/release/usage.txt` to see if the object in question has been removed.
Check `app/build/outputs/proguard/release/mapping.txt` to see if the object in question has been obfuscated.

In order to prevent ProGuard from *stripping away* needed classes or class members, add a `keep` options to your proguard config:
```
-keep class com.futurice.project.MyClass { *; }
```

To prevent ProGuard from *obfuscating* classes or class members, add a `keepnames`:
```
-keepnames class com.futurice.project.MyClass { *; }
```

Check [this template's ProGuard config](https://github.com/futurice/android-best-practices/blob/master/templates/rx-architecture/app/proguard-rules.pro) for some examples.
Read more at [Proguard](http://proguard.sourceforge.net/#manual/examples.html) for examples.

**Early on in your project, make a release build** to check whether ProGuard rules are correctly keeping whatever is important. Also whenever you include new libraries, make a release build and test the apk on a device. Don't wait until your app is finally version "1.0" to make a release build, you might get several unpleasant surprises and a short time to fix them.

**Tip.** Save the `mapping.txt` file for every release that you publish to your users. By retaining a copy of the `mapping.txt` file for each release build, you ensure that you can debug a problem if a user encounters a bug and submits an obfuscated stack trace.

**DexGuard**. If you need hard-core tools for optimizing, and specially obfuscating release code, consider [DexGuard](http://www.saikoa.com/dexguard), a commercial software made by the same team that built ProGuard. It can also easily split Dex files to solve the 65k methods limitation.

### Thanks to

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton and other Futurice developers for sharing their knowledge on Android development.

### License

[Futurice Oy](www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)

Translation
===========

Translated to Korean (`ko`) by **[Minsoo Park](http://www.github.com/minsoopark)**.

Original content by [Futurice Oy](http://www.futurice.com).
