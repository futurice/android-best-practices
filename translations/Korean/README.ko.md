# Android 개발 모범 사례

다음은 [Futurice](http://www.futurice.com)의 Android 개발자들로부터 학습한 내용들이다. 이 가이드를 따라가면서 이미 했던 작업을 되풀이(Reinventing the wheel)하지 않도록 하자. iOS나 Windows Phone 개발에도 관심이 있다면, [**iOS Good Practices**](https://github.com/futurice/ios-good-practices) 혹은 [**Windows App Development Best Practices**](https://github.com/futurice/windows-app-development-best-practices) 문서들도 확인해보자.

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

_아래 방법처럼 작업하지 않도록 한다_. 이는 버전 관리 시스템에서 나타날 것이다.

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

**jar 파일 임포트 대신 Maven을 선호하자.** 프로젝트에 jar 파일을 명시적으로 포함시킬 경우, 이들은 `2.1.1`처럼 특정하게 고정된 버전이 된다. jar를 다운로드하고, 업데이트하는 것은 귀찮은 일이다. 그러나 Maven은 이 문제를 적절하게 해결해줄 것이고, 또한 이는 Android Gradle 빌드에서 장려되는 방식이다. 예를 들자면 이렇다:

```groovy
dependencies {
    implementation 'com.squareup.okhttp:okhttp:2.2.0'
    implementation 'com.squareup.okhttp:okhttp-urlconnection:2.2.0'
}
```

**Maven의 동적 의존성 해결을 피하라**
`2.1.+`과 같이 동적으로 버전을 정하는 방식은 불안정하고, 빌드 사이에 미묘하고 이해하기 어려운 차이를 초래할 수 있어 피하도록 하자. `2.1.1`처럼 정적으로 고정된 버전을 사용하는 것이 보다 안정적이고, 예측 가능하고, 반복적인 개발 환경을 구성하는 데에 도움이 될 것이다.

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
    classpath 'me.tatarka:gradle-retrolambda:2.4.1'
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

### Activity와 Fragment

Fragment와 Activity를 이용하여 Android의 구조를 가장 좋은 방법으로 설계하는 방법은 커뮤니티나 Futurice 개발자들 사이에서도 합의된 바가 없다. Square는 Fragment의 우회 대안으로 [a library for building architectures mostly with Views](https://github.com/square/mortar)를 제공하는데, 이 또한 커뮤니티 사이에서 널리 추천할만한 방식은 아니라고 생각된다.

Android API의 히스토리로 인해, 막연히 Fragment가 화면상의 UI 조각이라고 떠올릴 수 있을 것이다. 즉, Fragment는 일반적으로 UI와 연관되어 있다는 것이다. Activity 또한 막연하게 그들의 라이프사이클과 상태를 관리하는 데에 중요한 컨트롤러라고 생각할 수 있다. 그러나, 다음 역할들에서 차이를 쉽게 찾아볼 수 있다: Activity는 UI 역할을 수행하고([delivering transitions between screens](https://developer.android.com/about/versions/lollipop.html)), [Fragment는 독립적으로 컨트롤러의 역할을 수행한다](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI). 그래서 우리는 Fragment 혹은 Activity, 또는 View 셋 중 하나만을 이용한 구조를 선택함에 있어서 결함이 있다는 점을 파악하고 정확한 근거를 갖는 결정을 하여 조심스럽게 시작하기를 권한다. 다음은 주의해야 할 것들에 대한 조언인데, 적당히 걸러서 수용하자:

- [Nested fragments](https://developer.android.com/about/versions/android-4.2.html#NestedFragments)를 널리 사용하는 것은 피해야 하는데, [matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/)가 발생할 수 있기 때문이다. 중첩된 Fragment는 꼭 타당한 경우(예를 들면, 수평으로 슬라이딩하는 ViewPager 내부의 Fragment들)나 잘 설명할 수 있을 만한 경우에만 사용하자.
- Activity에 너무 많은 코드를 넣는 것을 피해야 한다. 가능하면 언제든지 가벼운 컨테이너로서 유지하고, 주로 라이프사이클과 다른 중요한 Android와의 인터페이싱 API를 위해서만 존재하도록 하자. 순수 Activity 보다는 단일 Fragment로 구성된 Activity가 좋다 - UI 코드를 Activity의 Fragment에 넣자. 이는 다른 잘 구성된 레이아웃, 혹은 여러 Fragment로 구성된 타블렛 화면으로 옮길 필요가 있을 때에 재사용이 가능하도록 만든다. 정확한 근거가 없는 결정이라면 Fragment와 상호 작용하지 않는 Activity는 피하자.
- 앱의 내부적 동작이 Intent에 강하게 의존적인 Android 레벨의 API를 남용해서는 안된다. 이는 버그와 렉을 유발하여 Android OS나 다른 애플리케이션들에 영향을 줄 수 있다. 예를 들어, 만약 앱이 당신의 패키지 사이에서 내부적인 커뮤니케이션을 위해 Intent를 사용한다면, 앱이 OS 부팅 바로 후에 실행되었을 때 사용자 경험 상에서 몇 초간의 렉을 발생시킬 수 있다고 알려져 있다.

### Java 패키지 설계

Android 애플리케이션을 위한 Java 설계 간단히 [Model-View-Controller](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) 간략화할 수 있다. Android에서는, [Fragment and Activity are actually controller classes](http://www.informit.com/articles/article.aspx?p=2126865) 라고 설명되는데, 다른 면에서 Fragment와 Activity들은 명시적으로 사용자 인터페이스, 즉 View 이기도 하다.

그렇기 때문에, Fragment(혹은 Activity)를 정확히 Conteroller나 View 구분할 수 없다. 그래서 그에 해당하는 `fragments` 패키지에 두는 것이 낫다. Activity는 이전 섹션에서의 조언에 따라 최상위 패키지에 둘 수 있다. 2, 3개 이상의 Activity들을 계획하고 있다면, `activities` 패키지를 만들어 두자.

다른 경우에는, API 응답에 대한 JSON 파서에 의해 채워진 POJO들을 담는 `models` 패키지, 커스텀 View, Notification, Action bar view, Widget 등을 담는 `views` 패키지를 두어 설계가 일반적인 MVC로 표현될 수 있다. Adapter들은 데이터와 View들 사이에 존재하는 gray matter라고 할 수 있지만, 일반적으로 `getView()`를 통해 View들을 추출하는 데에 필요하기 때문에 `views` 패키지 안에 `adapters`라는 서브패키지로 둘 수 있다.

Controller 클래스들은 애플리케이션 전체에, Android 시스템에 가깝게 존재한다. 이들은 `managers` 패키지에 둘 수 있다. "DateUtils"와 같은 다양한 데이터 처리 클래스들은 `utils` 패키지에, 백엔드와 인터랙션하는 역할을 맡는 클래스들은 `network` 패키지에 두자.

종합적으로, 백엔드와 가까운 것부터 유저와 가까운 순서대로 정렬해보면 이렇다:

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

### 리소스

**이름 정하기.** `type_foo_bar.xml`과 같이 타입을 접두어로 두는 컨벤션을 따르자. 예시: `fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`.

**레이아웃 XML을 체계화하기.** 레이아웃 XML을 어떤 형태로 만들지 확실치 않다면, 다음 컨벤션이 도움이 될 것이다.

- 한 속성당 한 줄, 들여쓰기는 4칸의 스페이스
- `android:id`를 항상 첫 속성으로
- `android:layout_****` 속성들을 윗쪽에
- `style` 속성은 맨 아래에
- 태그를 닫는 `/>`는 정렬과 새 속성 추가를 위해 독립적인 줄에
- Rather than hard coding `android:text`에 하드코딩하는 것보다, Android Studio에서 사용 가능한 [Designtime attributes](http://tools.android.com/tips/layout-designtime-attributes)를 고려하자.

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

가장 중요한 것은, `android:layout_****`를 레이아웃 XML에 두고 `android:****`를 스타일 XML에 정의하는 것이다. 이 규칙은 예외가 있지만, 일반적으로 잘 동작한다. 이는 레이아웃(위치, 여백, 크기)과 내용에 관한 속성을 레이아웃 파일에 두고, 상세한 모양(색, 안쪽 여백, 폰트)에 대한 내용은 스타일 파일에 두기 위함이다.

예외는 이런 경우가 있다:

- `android:id`는 정확히 레이아웃 파일에 두어야 한다. should obviously be in the layout files
- `LinearLayout`의 `android:orientation` 속성은 일반적으로 레이아웃 파일에 있는 것이 타당하다.
- `android:text`는 내용을 정의하는 속성이기 때문에 레이아웃 파일에 두어야 한다.
- 가끔 일반적인 스타일로 `android:layout_width`와 `android:layout_height` 속성들을 두어야 말이 될 것 같지만, 기본적으로 이들은 레이아웃 파일에 보여진다.

**스타일을 사용하자.** View에 중복되는 모양이 사용되는 것은 매우 일반적인 일이기 때문에, 거의 모든 프로젝트들이 스타일을 적절히 사용해야한다. 적어도 애플리케이션에서 대부분의 텍스트 내용들은 일반 스타일을 가져야한다. 예를 들면 이렇다:

```xml
<style name="ContentText">
    <item name="android:textSize">@dimen/font_normal</item>
    <item name="android:textColor">@color/basic_black</item>
</style>
```

TextView에 적용해보면:

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/price"
    style="@style/ContentText"
    />
```

아마 버튼들에도 같은 일을 해주어야 하겠지만, 멈추지 말자. 계속 진행하면서 연관되어있고 중복된 `android:****` 속성들을 일반 스타일로 묶자.

**큰 스타일 파일은 다른 파일들로 나누자.** 단 하나의 `styles.xml` 파일을 가질 필요는 없다. Android SDK는 박스 외부의 파일들도 지원하는데, `styles`라는 이름엔 전혀 마법같은 무언가가 없이 파일 안에 `<style>` XML 태그가 있다면 상관없다. 그렇기 때문에 파일 이름은 `styles.xml`, `styles_home.xml`, `styles_item_details.xml`, `styles_forms.xml` 등과 같이 정할 수 있다. 빌드 시스템에서 의미를 갖는 리소스 디렉토리와는 다르게 `res/values` 안의 파일명은 임의로 설정 가능하다.

**`colors.xml`는 색 팔렛트이다.** `colors.xml`에는 색 이름과 RGBA 값을 매핑해놓는 일 외에 더 해야할 일은 없다. 각각 다른 버튼 타입들에 RGBA 값들을 정의하지 않도록 하자.

*아래의 방식은 피하자:*

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

이러한 형식으로 RGBA 값들을 반복하기 쉬운데, 이는 기본 색깔을 필요에 따라 변경하기 복잡하게 만든다. 또한 이러한 방식의 정의는 "button" 혹은 "comment" 처럼 특정 문맥에 관계되어있어 `colors.xml`이 아닌 스타일에 더 적합하다.

대신, 이렇게 하자:

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

애플리케이션의 디자이너에게 이 팔렛트를 요청해보자. 이름이 꼭 "green", "blue" 처럼 색의 이름일 필요는 없다. "brand_primary", "brand_secondary", "brand_negative" 같은 이름들이 더욱 받아들이기 쉽다. 이렇게 색의 형식을 지정하게 되면 색 값들을 리팩토링하기 쉬워지고, 얼마나 많은 색들이 사용되고 있는지 명시적으로 알 수 있다. 보통 심미감을 중요시하는 앱에서는, 사용되는 색의 종류를 줄이는 것이 중요하다.

**dimens.xml을 colors.xml처럼 다루자.** 기본적으로 색과 같은 목적을 위해 일반적인 여백과 폰트 크기 등의 "팔렛트"를 정의하자. 다음은 dimens.xml 파일의 좋은 예시이다:

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

문자열들이 일반적으로 다루어지듯이, 레이아웃, 바깥쪽/안쪽 여백에 하드코딩된 값들 대신 `spacing_****`을 사용하자. 이는 스타일과 레이아웃을 체계화하고 변경하는 것을 쉽게 해줌과 동시에 일관된 룩앤필(Look-and-feel)을 제공한다.

**strings.xml**

strings.xml의 문자열들은 네임스페이스의 형태와 비슷하게 이름을 짓고, 2개 이상의 Key에 값을 중복해서 사용하는 것을 두려워하지 않도록 하자. 언어는 복잡하기 때문에, 네임스페이스가 문맥을 갖고 애매함을 없애는 것이 필수이다.

**잘못된 예**
```xml
<string name="network_error">Network error</string>
<string name="call_failed">Call failed</string>
<string name="map_failed">Map loading failed</string>
```

**좋은 예**
```xml
<string name="error.message.network">Network error</string>
<string name="error.message.call">Call failed</string>
<string name="error.message.map">Map loading failed</string>
```

문자열 값을 모두 대문자로 쓰지 않도록 한다. 일반적인 텍스트 컨벤션을 따르되(e.g., 첫 글자만 대문자로), 만약 문자열을 모두 대문자로 표시해야 한다면, TextView의 [`textAllCaps`](http://developer.android.com/reference/android/widget/TextView.html#attr_android:textAllCaps) 속성을 이용하자.

**잘못된 예**
```xml
<string name="error.message.call">CALL FAILED</string>
```

**좋은 예**
```xml
<string name="error.message.call">Call failed</string>
```

**깊은 View 계층을 피하자.** 가끔 View를 편성하기 위해 LinearLayout을 하나 더 추가하려할 것이다. 이 상황은 이러한 문제를 일으킨다:

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

레이아웃 파일에서 명시적으로 이런 형태를 보지 못했다 하더라도, 결국 다른 View에 또 다른 View들을 채울 때(Java에서) 이렇게 끝나게 될 것이다.

두 가지 문제가 발생한다. 프로세서가 다루어야 할 UI 트리가 복잡해지면서 퍼포먼스 문제를 경험하게 될 것이다. 또 하나의 심각한 문제는 [StackOverflowError](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views)의 가능성이다.

그러므로, 최대한 View 계층을 수평하게 유지하자: [RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html)를 사용하는 방법, [optimize your layouts](http://developer.android.com/training/improving-layouts/optimizing-layout.html) 방법과 [`<merge>` tag](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts)를 사용하는 방법을 확인하자.

**WebView와 관련된 문제들에 유의하자.** 뉴스 기사와 같은 웹페이지를 보여주어야 할 때, 백엔드 프로그래머들에게 "*순수한*" HTML을 요청하지 않고 HTML을 정리하기 위해 클라이언트단에서 프로세싱하는 것을 피하자. ApplicationContext가 아닌 Activity로의 참조를 유지할 때, [WebViews can also leak memory](http://stackoverflow.com/questions/3130654/memory-leak-in-webview). 간단한 텍스트와 버튼을 사용할 때에는 WebView를 피하고 TextView와 Button을 사용하도록 한다.


### 테스트 프레임워크

Android SDK의 테스팅 프레임워크는 여전히 미흡한데, 특히 UI 테스트에 관해서는 더더욱 그렇다. Android Gradle은 현재 [extension of JUnit with helpers for Android](http://developer.android.com/reference/android/test/package-summary.html)을 사용하여 만들어진 JUnit 테스트들을 실행하는 [`connectedAndroidTest`](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing)라는 테스트 Task를 구현하고 있다. 이는 기기 혹은 에뮬레이터에 연결된 테스트를 실행해야 할 것을 의미한다. 공식 가이드인 [[1]](http://developer.android.com/tools/testing/testing_android.html) [[2]](http://developer.android.com/tools/testing/activity_test.html)를 따라 테스트하자.

**View가 아닌 유닛 테스트에는 [Robolectric](http://robolectric.org/)를 사용하자.** 이는 개발 속도의 만족을 위해 "기기에 연결되지 않은" 테스트의 제공을 추구하는 테스트 프레임워크이다. 특히 모델과 View 모델들의 유닛 테스트에 적합하다. 하지만, UI 테스트에서의 Robolectric를 사용한 테스트는 부정확하고 불완전하다. 애니메이션, 대화 상자 등에 관한 UI 요소들의 테스트에서는 문제를 보일 것이고, 이는 "어둠 속에서 걷는 것"(조작할 만한 화면을 보지 않고 테스트)처럼 매우 복잡할 것이다.

**[Robotium](https://code.google.com/p/robotium/)은 UI 테스트 작성을 쉽게 해준다.** UI의 연결된 테스트를 실행하는 데에 Robotium이 필요하지 않지만, View를 가져와 분석하고, 화면을 조작할 수 있게 해주는 Robotium의 많은 도우미들이 유용하게 작용할 것이다. 테스트 케이스는 이처럼 쉽게 표현된다:

```java
solo.sendKey(Solo.MENU);
solo.clickOnText("More"); // searches for the first occurence of "More" and clicks on it
solo.clickOnText("Preferences");
solo.clickOnText("Edit File Extensions");
Assert.assertTrue(solo.searchText("rtf"));
```

### 에뮬레이터

전문적으로 Android 앱을 개발하고 있다면, [Genymotion emulator](http://www.genymotion.com/)의 라이센스를 구매하자. Genymotion 에뮬레이터는 일반적인 AVD 에뮬레이터에 비해 빠른 FPS로 실행된다. 앱을 데모하고, 네트워크 연결 품질, GPS 포지션을 시험해보는 툴들을 제공한다. 또한 연결된 테스트에 이상적으로 작동한다. 많은(전부는 아님) 다른 기기들을 접할 때, Genymotion의 라이센스는 실제 여러가지 기기를 구매하는 것 보다 훨씬 저렴하다.

주의할 점: Genymotion 에뮬레이터는 Google Play Store와 Maps과 같은 모든 Google 서비스들을 포함하고 있지는 않다. 삼성의 특정 API를 테스트해야 한다면, 실제 삼성 기기가 필요하다.

### Proguard 설정

[ProGuard](http://proguard.sourceforge.net/)는 일반적으로 Android 프로젝트의 패키징된 코드를 축소하고, 난독화하기 위해 사용된다.

ProGuard를 사용하고 있는지 아닌지는 해당 프로젝트 설정에 달려있다. 보통 릴리즈 apk를 빌드할 때 gradle을 설정하고 ProGuard를 사용할 것이다.

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

어떤 코드가 보존되어야 하고 어떤 코드가 버려지거나 난독화되어야할 지 결정하기 위해, 코드에 한 개 이상의 엔트리 포인트를 설정해야한다. 이 엔트리 포인트는 일반적으로 main 메소드, applets, midlets, Activity와 같은 것들이다.
Android 프레임워크는 `SDK_HOME/tools/proguard/proguard-android.txt`에서 찾아볼 수 있는 기본 설정을 사용한다. 위의 설정을 사용하여, `my-project/app/proguard-rules.pro`의 사용자화된 특정 프로젝트 ProGuard 규칙은 기본 설정에 추가로 설정될 것이다.

ProGuard와 관련된 일반적인 문제는 어떠한 Warning도 없이 빌드 커맨드가 성공했는데도 애플리케이션이 시작시에  `ClassNotFoundException`나 `NoSuchFieldException`와 비슷한 예외로 크래시가 발생하는 것이다.
이는 두 가지 중 하나를 의미한다:

1. ProGuard가 클래스, Enum, 메소드, 필드 혹은 어노테이션들을 필요하지 않다고 여겨 제거한 것이다.
2. ProGuard가 클래스, Enum, 필드 이름들이 간접적으로 고유의 이름대로 사용되고 있음에도 불구하고(예를 들면 Java reflection을 통해) 이들을 난독화(이름 변경)한 것이다.

물음의 객체가 제거되었는지 보려면 `app/build/outputs/proguard/release/usage.txt`을 확인하자.
물음의 객체가 난독화되었는지 보려면 `app/build/outputs/proguard/release/mapping.txt`을 확인하자.

ProGuard가 필요한 클래스 혹은 클래스 멤버들을 *벗겨내는 것*을 막기 위해서는, ProGuard 설정에 `keep` 옵션을 추가하자:
```
-keep class com.futurice.project.MyClass { *; }
```

ProGuard가 클래스 혹은 클래스 멤버들의 *난독화*을 막고싶다면, `keepnames`을 추가하자:
```
-keepnames class com.futurice.project.MyClass { *; }
```

또 다른 예시로 [Proguard](http://proguard.sourceforge.net/#manual/examples.html)를 읽어보자.

**프로젝트의 초기에, 릴리즈 빌드를 만들자.** 이는 ProGuard 규칙들이 중요한 것들을 정확하게 보관하고 있는지 확인하기 위함이다. 또한 언제든지 새로운 라이브러리를 포함시켰을 때, 릴리즈 빌드를 만들고 기기에서 apk를 테스트해보자. 릴리즈 빌드를 만들기 위해 앱이 "1.0" 버전이 되기까지 기다리지 말고, 수 차례 의외의 문제들을 발견하고 수정하는 짧은 시간을 갖자.

**팁.** 배포시에 `mapping.txt` 파일들은 매 릴리즈마다 저장하자. 각 릴리즈 빌드마다 `mapping.txt` 파일을 보관해두면, 사용자가 버그를 만나고 알아보기 힘든 스택 트레이스를 보내왔을 때 문제를 확실하게 디버깅할 수 있다.

**DexGuard**. 릴리즈 코드를 최적화하고, 특히 알기 어렵게 만들기 위해 하드코어한 툴이 필요하다면, ProGuard를 빌드한 같은 팀에서 만든 상업 소프트웨어인 [DexGuard](http://www.saikoa.com/dexguard)를 고려해보자. 이는 65,000 메소드 수 제한을 해결하기 위해 Dex 파일들을 쉽게 나눈다.

### Thanks to

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton and other Futurice developers for sharing their knowledge on Android development.

### License

[Futurice Oy](http://www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)

Translation
===========

Translated to Korean (`ko`) by **[Minsoo Park](http://www.github.com/minsoopark)**.

Original content by [Futurice Oy](http://www.futurice.com).
