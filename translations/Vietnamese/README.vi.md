# Những phương pháp tốt nhất trong việc phát triển Android (Best practices in Android development)

Đây là những bài học và kinh nghiệm được rút ra từ những lập trình viên Android tại [Futurice](http://www.futurice.com). Những chỉ dẫn trong bài viết này giúp bạn tối ưu hoá được thời gian cũng như chất lượng dự án Android.Bên cạnh đó, nếu bạn quan tâm tới iOS và Windows Phone, bạn có thể tham khảo thêm tại hai bài hướng dẫn tương tự [**iOS Good Practices**](https://github.com/futurice/ios-good-practices) và [**Windows App Development Best Practices**](https://github.com/futurice/windows-app-development-best-practices).

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--best--practices-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1091)

## Tổng quát (Summary)

#### [Sự dụng Gradle và cấu trúc Android project](#build-system)
#### [Đặt password và dữ liệu quan trọng trong gradle.properties](#gradle-configuration)
#### [Sử dụng Jackson library để bóc tách dữ liệu dạng JSON](#libraries)
#### [Đừng tự viết HTTP Client, hãy sử dụng thư viện Volley hoặc OkHttp](#networklibs)
#### [Không nên sử dụng Guava và hãy sử dụng thư viện ít nhất có thể để tránh vượt quá *65k method limit*](#methodlimitation)
#### [Cẩn thận trong việc lựa chọn giữa Activities và Fragments](#activities-and-fragments)
#### [Layout XMLs cũng là code, nên hãy học cách tổ chức và sắp xếp chúng](#resources)
#### [Sử dụng styles để tránh lặp lại việc khai báo attributes trong layout XMLs](#styles)
#### [Sử dụng nhiều file styles thay vì nhét tất cả vào một file](#splitstyles)
#### [Chỉ dùng colors.xml cho việc định nghĩa bàng màu và thật ngắn gọn](#colorsxml)
#### [Tương tự cũng chỉ dùng dimens.xml cho định nghĩa các hằng số tổng quát](#dimensxml)
#### [Không nên sử dụng hệ phân cấp sâu trong ViewGroups](#deephierarchy)
#### [Tránh việc xử lý phía client tại WebViews, và chú ý việc rò rỉ bộ nhớ](#webviews)
#### [Sử dụng Robolectric cho Unit Tests và Robotium cho UI tests](#test-frameworks)
#### [Nên sử dụng Genymotion như máy ảo chính](#emulators)
#### [Luôn sử dụng ProGuard hoặc DexGuard](#proguard-configuration)
#### [Sử dụng SharedPreferences cho việc lưu trữ đơn giản, còn lại dùng ContentProviders](#data-storage)
#### [Sử dụng Stetho để debug ứng dụng](#use-stetho)

----------

### Android SDK

Nên đặt [Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools) ở đâu đó trong thư mục home (home directory) hoặc vị trí mà không liên quan đến ứng dụng/dự án của bạn. Một số bản phân phối IDEs đã bao gồm cả SDK, và có thể đặt nó trong cùng thư mục với IDEs. Việc này sẽ làm bạn điên đầu khi cần update hay cài lại IDE, và như vậy sẽ mất bản cài đặt SDK, một lần nữa phải mất công download lại.

Bên cạnh đó, nếu IDE của bạn chạy dưới quyền user, thì không nên để SDK trong thư mục hệ thống, nơi cần quyền truy cập root (sudo).

### Build system

Sự lựa chọn mặc định là [Gradle](http://tools.android.com/tech-docs/new-build-system). Với Gradle, mọi thứ thực sự đơn giản để:

- Build các bản flavours hay variants khác nhau cho ứng dụng của bạn
- Tạo các tasks dạng script-like đơn giản
- Quản lý và download thư viện hay dependencies
- Thay đổi keystores
- Và nhiều thứ hay ho nữa

Trước đó Android có sử dụng một hệ thống build khác là Ant nhưng đã chấm dứt từ cuối năm 2015, giờ chỉ có Android's Gradle plugin là được phát triển và hỗ trợ từ Google.

Việc ứng dụng của bạn được định nghĩa bởi các file Gradle quan trọng hơn việc phụ thuộc vào các cấu hình trong IDEs. Việc này cho phép việc chuyển tiếp builds giữa các công cụ tốt hơn và hỗ trợ tốt hơn cho hệ thống tích hợp liên tục (continuous integration).

### Cấu trúc dự án (Project structure)

Mặc dù Gradle có độ linh hoạt cao và cho phép can thiệp sâu vào cấu trúc một project Android, trừ khi bạn bắt buộc phải thay đổi, còn không thì bạn nên chấp nhậntru cấu trúc mặc định[default structure](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Project-Structure). Việc này sẽ giúp bạn tối giản code trong build scripts.

### Cấu hình Gradle (Gradle configuration) 

**Cấu trúc tổng quát (General structure).** Áp dụng hướng dẫn của Google[Google's guide on Gradle for Android](http://tools.android.com/tech-docs/new-build-system/user-guide)

**Các tác vụ nhỏ (Small tasks).** Thay vì sử dụng (shell, Python, Perl, etc) scripts, bạn có thể tạo các tác vụ bằng Gradle, chỉ cần dựa theo hướng dẫn của Gradle ở đây [Gradle's documentation](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF).

**Passwords.** Nếu bạn cần định nghĩa `signingConfigs` trong file `build.gradle` cho bản release build. Thì bên dưới là việc bạn nên tránh:

_Không nên_. Bởi vì khi bạn định nghĩa như vậy thì password sẽ hiển thị trên hệ thống quản lý mã nguồn (Version Control System).

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

Bạn nên tạo file `gradle.properties` và nó _không nên_ được thêm vào hệ thống quản lý mã nguồn (ví dụ Git) (nên liệt kê `gradle.properties` trong file .gitignore):

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```

File đó sẽ tự động được import vào Gradle, vậy nên bạn có thể khái báo trong file `build.gradle` như sau:

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

**Nên sử dụng Maven dependency thay vì import thư viện file jar.** Nếu bạn sử dụng file jar trong project, thì việc download và update các phiên bản mới của nó sẽ mất thời gian và phức tạp. Vấn đề này đã được Maven giải quyết và được khuyến khích sử dụng trong Android Gradle builds. Ví dụ:

```groovy
dependencies {
    implementation 'com.squareup.okhttp:okhttp:2.2.0'
    implementation 'com.squareup.okhttp:okhttp-urlconnection:2.2.0'
}
```    

**Tránh việc khai báo thư viện có version động trong Maven**
Tránh việc khai báo thư viện có version động ví dụ như `2.1.+`, vì việc này có thể dẫn đến các bản build khác nhau và không ổn định và khó kiểm soát các thư viện. Nên khai báo version tĩnh ví dụ như `2.1.1` để giúp project có môi trường phát triển ổn đinh, dễ kiểm soát.

**Sử dụng tên package khác cho các bản build không phải là bản release**
Ví dụ sử dụng (`applicationIdSuffix`) cho bản *debug* [build type](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Build-Types) để có thể cài đặt cả hai bản *debug* và *release* trên cùng một thiết bị (cũng nên thay đổi tên package cho mỗi bản build của mỗi khách hàng). Việc này rất có lợi cho phần sau trong vòng đời của một ứng dụng, sau khi nó đã được publish trên store.

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

Sử dụng icons khác nhau để phân biệt bản debug và release. Dùng Gradle, làm việc này rất đơn giản: với cấu trúc app mặc định, chỉ cần đặt *debug* icon trong `app/src/debug/res` và *release* icon trong `app/src/release/res`. Bạn cũng có thể [thay đổi app name](http://stackoverflow.com/questions/24785270/how-to-change-app-name-per-gradle-build-type) cho các loại bản build, cũng như  `versionName` (như ví dụ bên trên).

### IDE và trình soạn thảo (IDEs and text editors)

**Sử dụng bất cứ trình soạn thảo nào, nhưng nó phải phù hợp với cấu trúc dự án của bạn** Trình soạn thảo(Editors) là sự lựa chọn của cá nhân bạn, và nhiệm vụ của bạn là cấu hình và thay đổi trình soạn thảo của bạn để phù hợp với cấu trúc dự án và hệ thống build(build system).

IDE được khuyên dùng là [Android Studio](https://developer.android.com/sdk/installing/studio.html) bởi nó được phát triển và update thường xuyên bởi Google, hỗ trợ tốt Gradle, có rất nhiều các công cụ giám sát và phân tích hữu dụng, và nó được phát triển nhắm đến các dự án Android.

Nếu bạn muốn, bạn có thể sử dụng các trình soạn thảo như Vim, Sublime Text, or Emacs. Nhưng trong trường hợp này, bạn sẽ phải sử dụng Gradle and `adb` qua command line. 

Sử dụng [Eclipse ADT](http://developer.android.com/tools/help/adt.html) cho việc phát triển Android không còn được khuyến khích.
[Google ngừng hỗ trợ ADT vào cuối năm 2015](http://android-developers.blogspot.fi/2015/06/an-update-on-eclipse-android-developer.html) và thông báo cho người dùng [Chuyển qua Android Studio](http://developer.android.com/sdk/installing/migrate.html) sớm nhất có thể.

Bất cứ lựa chon của bạn là gì, bạn cũng nên tránh việc thêm files cấu hình trình soạn thảo hay IDE vào hệ thống quản trị mã nguồn (CVS), ví dụ file `.iml` của Android Studio, vì các file này thường chứa thông tin cấu hình cho riêng máy của bạn, và nó sẽ không phù hợp với máy của người khác, ví dụ thành viên trong nhóm làm cùng project với bạn.

Cuối cùng, nên đối xử hoà nhã với những lập trình viên khác; đừng bắt họ phải thay đổi công cụ ưa thích nếu họ làm việc hiệu quả với những công cụ đó.

### Thư viện (Libraries)

**[Jackson](http://wiki.fasterxml.com/JacksonHome)** là thư viện Java giúp chuyển đổi dữ liệu dạng Objects thành JSON và ngược lại. [Gson](https://code.google.com/p/google-gson/) thường được mọi người sử dụng, tuy nhiên chúng tôi thấy rằng Jackson có hiệu năng tốt hơn bởi vì nó hỗ trợ nhiều cách khác nhau trong việc xử lý JSON: streaming, in-memory tree model, and traditional JSON-POJO data binding. Mặc dù vậy hãy nên nhớ rằng Jackson là thư viện lớn hơn Gson, vì vậy phụ thuộc vào trường hợp của bạn, bạn có thể sử dụng Gson để tránh 65k methods limitation. Hai thư viện khác bạn có thể sử dụng là: [Json-smart](https://code.google.com/p/json-smart/) và [Boon JSON](https://github.com/RichardHightower/boon/wiki/Boon-JSON-in-five-minutes)

<a name="networklibs"></a>
**Networking, caching, and images.** Có rất nhiều giải pháp đang tranh cãi được đưa ra cho việc thực hiện request tới backend mà bạn sử dụng để implement phía client. Vậy nên sử dụng [Volley](https://android.googlesource.com/platform/frameworks/volley) hay [Retrofit](http://square.github.io/retrofit/). Volley cung cấp cả helpers để load and cache images. Còn nếu bạn dùng Retrofit, nên sử dụng [Picasso](http://square.github.io/picasso/) cho việc loading and caching images, và [OkHttp](http://square.github.io/okhttp/) cho việc thực hiện HTTP requests hiệu quả. Bộ ba thư viện Retrofit, Picasso và OkHttp được tạo ra bởi cùng một công ty, nên chúng hỗ trợ cho nhau rất tốt. [OkHttp cũng có thể được sử dụng để kết nối với Volley](http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835).
[Glide](https://github.com/bumptech/glide) là một lựa chọn nữa cho việc loading and caching images. Nó có performance tốt hơn Picasso, hỗ trợ GIF and circular image, nhưng có số lượng method lớn hơn Picasso.

**RxJava** là một thư viện cho Reactive Programming, hay nói cách khác, xử lý các sự kiện không đồng bộ (asynchronous events). Đây là một mô hình mạnh mẽ và đầy hứa hẹn nhưng cũng có thể làm bạn hơi bối rối vì sự khác biệt của nó. Chúng tôi khuyến khích các bạn nên thận trọng trong việc sử dụng thư viện này để cấu trúc toàn bộ application. Chúng tôi đã sử dụng RxJava trong một số project, nếu bạn cần sự giúp đỡ, hãy liên hệ với một trong những thành viên sau: Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen. We have written some blog posts on it: [[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android), [[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754), [[4]](http://blog.futurice.com/android-development-has-its-own-swift).

Nếu bạn không có kinh nghiệm với Rx, khi bắt đầu bạn chỉ nên áp dụng nó cho responses từ API. Bên cạnh đó, bắt đầu bằng việc áp dụng nó cho các tác vụ xử lý sự kiện UI đơn giản, ví dụ sự kiện click hay typing trong search field. Nếu bạn đủ tự tin vào kỹ năng Rx programming và muốn áp dụng nó cho toàn bộ cấu trúc, thì hãy nên viết Javadocs cho tất cả các phần khó. Vì nếu không sẽ khó cho một lập trình viên khác không quen với RxJava khi maintain project của bạn. Hãy cố gắng giúp họ hiểu code của bạn cũng như Rx một cách tốt nhất.

**[Retrolambda](https://github.com/evant/gradle-retrolambda)** là một thư viện Java cho việc sử dụng cú pháp Lamda expression trong Android và các nền tảng trước JDK-8. Nó giúp làm cho code bạn ngắn gọn và dễ hiểu, đặc biệt nếu bạn sử dụng functional style, ví dụ với RxJava.

Android Studio hỗ trợ code cho Java8 lambdas. Nếu bạn mới bắt đầu với lambdas, bạn nên theo hướng dẫn sau đây: 
- interface chỉ có một phương thức là "lambda friendly" và nó có thể gộp thành một cú pháp gọn hơn.
- Nếu bạn nghi ngờ về parameters, hãy lập tức viết một inner class và để Android Studio tự động gộp nó vào một lamda.

<a name="methodlimitation"></a>
**Nên chú ý về dex method limitation, và tránh sử dụng quá nhiều thư viện.** Android apps,khi được đóng gói như một file dex, chỉ cho phép giới hạn trong 65536 phương thức tham chiếu (referenced methods) [[1]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [[2]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [[3]](http://jakewharton.com/play-services-is-a-monolith/). Bạn sẽ bắt gặp lỗi fatal error trong quá trình biên dịch nếu bạn vượt qua ngưỡng này. Với lý do đó, hãy nên sử dụng thử viện ít nhất có thể và sử dụng công cụ [dex-method-counts](https://github.com/mihaip/dex-method-counts) để xác định xem tập thư viện nào có thể được sử dụng để tránh việc vượt quá giới hạn này. Đặc biệt, tránh sử dụng thư viện Guava vì nó chứa hơn 13k methods.

### Activities and Fragments

Không có sự đồng thuận duy nhất nào trong cộng đồng cũng như trong developers tại Futurice trong câu trả lời cho câu hỏi: "Đâu là cách tổ chức cấu trúc tốt nhất cho một dự án Android với Fragments hay Activity?". Thậm chí Square có một thư viện cho việc xây dựng cấu trúc gần như hoàn toàn với Views [a library for building architectures mostly with Views](https://github.com/square/mortar), bỏ qua cho sự cần thiết của Fragments, nhưng việc này vẫn không được khuyến khích như một phương pháp hay trong cộng đồng.

Qua lịch sử xây dựng Android API, bạn có thể coi Fragments như là một phần UI trên một màn hình. Activities có thể được xem như controllers và chúng đặc biệt quan trọng cho việc quản lý vòng đời và trạng thái của chúng. Tuy nhiên, bạn có thể thấy chúng trong điều ngược lại: activities có thể đóng vai trò như thành phần UI([delivering transitions between screens](https://developer.android.com/about/versions/lollipop.html)), và fragments có thể được độc lập sử dụng như controllers[fragments might be used solely as controllers](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI). 
Chúng tôi khuyên bạn nên cẩn thận khi đưa ra quyết định bởi vì sẽ có nhiều hạn chế nếu chọn cấu trúc project chỉ có fragments, chỉ có activities hay chỉ có views.Sau đây là một số lời khuyên nhưng bạn nên tự đưa ra quyết định của riêng mình:

- Tránh sử dụng [nested fragments](https://developer.android.com/about/versions/android-4.2.html#NestedFragments) quá nhiều, bởi vì lỗi [matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/) có thể xảy ra. Chỉ sử dụng nested fragments khi nó có ích với bạn (ví dụ, fragments trong ViewPager trong một fragment hoạt động như một màn hình), hoặc nếu nó là một quyết định chắc chắn và được hiểu rõ.
- Tránh đặt quá nhiều code trong activities. Bất cứ khi nào có thể, hãy giữ cho chúng như một containers gọn nhẹ, tồn tại trong application mục đích chính cho quản lý lifecycle và các APIs giao diện quan trọng. Nên sử dụng các activity đơn fragment (single-fragment activities) thay vì activities đơn thuần - đăt code liên quan tới UI trong fragment. Việc này giúp nó có thể được sử dụng lại trong trường hợp bạn cần thay đổi để đặt trong một tabbed layout, hoặc trong một màn hình tablet đa fragment(multi-fragment tablet screen). Một activity nên kèm theo một fragment tương ứng.
- Đừng lạm dụng các Android-level APIs ví dụ như phụ thuộc vào Intent cho các app's internal workings. Việc đó có thể sẽ ảnh hưởng tới Android OS và ứng dụng khác, tạo ra các lỗi(bug) và độ trễ(lag). 
- Ví dụ, app của bạn sử dụng Intents cho việc giao tiếp giữa các packages, bạn có thể vô tình tạo ra độ trễ vài giây, gây ảnh hưởng tới trải nghiệm người dùng nếu ứng dụng đó mở ngay khi boot OS.

### Cấu trúc gói Java (Java packages architecture)

Java architectures cho Android applications có thể gần tương tự như [Model-View-Controller](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller). Trong Android, [Fragment và Activity thực sự là các controller classes](http://www.informit.com/articles/article.aspx?p=2126865). Mặt khác, rõ ràng chúng là một phần của giao diện (user interface), do đó chúng cũng là views.

Do đó, khó có thể phân loại fragments(hoặc activities) là controllers hay views. Tốt hơn là nên để fragments trong `fragments` package. Activities có thể để ở package cao nhất (top-level package). Nếu bạn có nhiều hơn 2 hoặc 3 activities thì bạnc cũng nên tạo riêng `activities` package.

Trái lại, cấu trúc có thể được xây dựng như mô hình MVC cơ bản, với một `models` package chứa POJOs, lớp có thể chứa dữ liệu sau khi bóc tách JSON từ API responses, và một `views` package chứa những custom Views của bạn như notifications, action bar views, widgets, etc. Adapters giống như chất xám, nằm ở giữa data và views. Tuy nhiên, chúng cơ bản cần export một số Views qua `getView()`, vì vậy bạn có thể đặt `adapters` subpackage trong `views`.

Một số lớp controllers ở mức application và gần tới Android system thì nên để trong `managers` package. Còn các lớp xử lý data, ví dụ như "DateUtils" nên được đặt trong `utils` package. Các lớp đảm nhiệm vai trò tương tác với backend nên đặt trong `network` package.

Tất cả package nên được sắp xếp từ các lớp phía backend tới các lớp phía user.

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

**Đặt tên(Naming).** Dùng tên của loại resources để đặt làm tiếp đầu ngữ cho tên file, như `type_foo_bar.xml`. Ví dụ: `fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`.

**Sắp xếp file layout XMLs.** Nếu bạn không chắc chắn trong việc định dạng như nào cho đúng một file layout XML, bạn có thể tham khảo các quy ước dưới đây.

-  Đặt một thuộc tính (attribute) trên một dòng, lùi vào 4 spaces
- `android:id` luôn là attribute đầu tiên
- `android:layout_****` nên đặt trên
- `style` nên đặt dưới cùng
- Tag closer `/>` nên để trên cùng một dòng, để tạo điều kiện thêm và sắp xếp attributes.
- Thay vì đặt cố định text(hard-coding) trong`android:text`, nên sử dụng [Designtime attributes](http://tools.android.com/tips/layout-designtime-attributes) có sẵn trong Android Studio.

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

Như là một quy tắc chuẩn,các attributes `android:layout_****` nên được định nghĩa trong file layout XML, còn các attributes `android:****` nên đặt trong file style XML. Quy tắc này cũng có một số ngoại lệ nhưng nhìn chung đúng với hầu hết các attributes. Theo như quy tắc này thì chúng ta chỉ nên đặt các layout và content attributes(positioning, margin, sizing) trong file layouts, còn các thuộc tính định nghĩa về chi tiết bề ngoài (như colors, padding, font) nên đặt trong file styles.

Lưu ý một số ngoại lệ:

- `android:id` hiển nhiên là nên đặt trong file layouts.
- `android:orientation` trong `LinearLayout` cũng nên đặt trong file layouts.
- `android:text` nên đặt trong file layouts vì nó định nghĩa nội dung(content).
- Trong một vài trường hợp nên định nghĩa style chung cho `android:layout_width` and `android:layout_height` nhưng mặc định thì những thuộc tính này nên đặt trong file layouts.

<a name="styles"></a>
**Sử dụng styles.** Hầu hết các project cần sử dụng styles một các phù hợp, vì nếu không nó sẽ dẫn đến việc lặp lại việc khai báo thuộc tính cho một view. Ít nhất, bạn nên có một file style chung cho toàn bộ text content trong application. Ví dụ: 

```xml
<style name="ContentText">
    <item name="android:textSize">@dimen/font_normal</item>
    <item name="android:textColor">@color/basic_black</item>
</style>
```

Áp dụng vào TextViews:

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/price"
    style="@style/ContentText"
    />
```

Bạn có thể sẽ cần làm điều tương tự với buttons, nhưng đừng chỉ dừng lại ở hai thành phần trên. Nên khai báo các thành phần `android:****` attributes liên quan và có tính lặp lại vào một file style chung.

<a name="splitstyles"></a>
**Chia một file style lớn thành các file styles nhỏ hơn.** Bạn không cần đặt mọi thứ trong một file `styles.xml`. Android SDK hỗ trợ nhiều files khác cùng nhau, tên file sẽ không ảnh hưởng vì Android quan tâm đến nội dung trong tags `<style>` bên trong file. Do đó bạn có thể khai báo files `styles.xml`, `styles_home.xml`, `styles_item_details.xml`, `styles_forms.xml`. Không giống như tên của thư mục resource cần phải đặt đúng để cho build system hiểu, tên file trong `res/values` có thể đặt tuỳ ý.

<a name="colorsxml"></a>
**`colors.xml` được coi là một bảng màu.** File `colors.xml` chỉ nên giữ nhiệm vụ là định nghĩa tên màu với giá trị RGBA chứ không nên có nội dung nào khác. Không nên định nghĩa giá trị RGBA cho các loại buttons khác nhau.

*Không nên khai báo như dưới đây:*

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

Bạn sẽ dễ khai báo lặp lại giá trị RGBA trong kiểu định nghĩa này và sẽ phức tạp khi thay đổi một màu cơ bản khi cần. Bên cạnh đó, việc định nghĩa này liên quan đến một số trường hợp nhất đinh, giống như "button" hay "comment", và nên đặt trong a button style, không phải trong `colors.xml`.

Thay vì đó, nên khai báo như sau:

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
Nên hỏi bảng màu này từ designer. Tên có thể không nhất thiết phải là tên màu như "green", "blue", etc. Dùng các tên như "brand_primary", "brand_secondary", "brand_negative" hoàn toàn được chấp nhận. Việc định dạng colors như vậy sẽ giúp việc thay đổi colors dễ dàng và biết rõ ràng rằng có bao nhiêu màu khác nhau đang được sử dụng. Về tính thẩm mỹ, việc giảm số lượng màu cần dùng là rất quan trọng.

<a name="dimensxml"></a>
**Đối với file dimens.xml cũng tương tự** Bạn cũng nên định nghĩa một "bảng màu" cho typical spacing and font sizes, như việc định nghĩa màu trong file colors.xml. Một ví dụ cho một file dimens:

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

Bạn nên sử dụng `spacing_****` dimensions cho việc bố trí(layouting), trong margins và paddings, thay vì các giá trị hard-coded, giống như strings. Việc này sẽ tạo sự thống nhất và dễ dàng cho việc sắp xếp và thay đổi styles và layouts.

**strings.xml**

Đặt tên strings với keys mà giống với namespaces, và việc lặp lại một giá trị cho 2 hoặc nhiều keys cũng không thành vấn đề. Ngôn ngữ thường phức tạp, vì vậy namespaces rất cần thiết cho việc định nghĩa ngữ cảnh (context) và mang lại sự rõ ràng.

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

Không nên viết hoa toàn bộ chữ cái trong string. Nên sử dụng quy ước bình thường cho text (Ví dụ: chỉ viết hoa chữ cái đầu tiên). Nếu bạn cần hiển thị string dưới dạng các chữ cái viết hoa, thì bạn có thể sử dụng attribute [`textAllCaps`](http://developer.android.com/reference/android/widget/TextView.html#attr_android:textAllCaps) cho một TextView.

**Bad**
```xml
<string name="error_message_call">CALL FAILED</string>
```

**Good**
```xml
<string name="error_message_call">Call failed</string>
```

<a name="deephierarchy"></a>
**Tránh sử dụng một hệ phân cấp sâu cho views.** Đôi khi bạn chỉ dùng LinearLayout cho việc sắp xếp các Views. Ví dụ như:

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

Một số vấn đề có thể xảy ra. Ví dụ bạn sẽ gặp phải vấn đề về hiệu năng, bởi vì cây UI (UI tree) phức tạp sẽ làm processor xử lý khó hơn. Một vấn đề nghiêm trọng hơn là khả năng gặp phải lỗi này [StackOverflowError](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views).

Vì vậy, cố gắng giữ cho hệ phân cấp Views ít cấp bậc nhất có thể. Bạn nên học cách sử dụng [RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html), và [tối ưu layouts](http://developer.android.com/training/improving-layouts/optimizing-layout.html) và sử dụng [`<merge>` tag](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts).

<a name="webviews"></a>
** Nên chú ý vấn đề liên quan đến WebViews.** Khi bạn phải hiện thỉ một web page, ví dụ cho một bài báo, nên tránh xử lý phía client để clean the HTML hơn là yêu cầu "*pure*" HTML từ lập trình viên backend. [WebViews cũng có thể gây ra rò rỉ bộ nhớ](http://stackoverflow.com/questions/3130654/memory-leak-in-webview) khi bạn giữ một tham chiếu tới Activity, thay vì bound to the ApplicationContext. Tránh sử dụng một WebView cho các texts hay buttons đơn giản mà nên sử dụng TextViews và Buttons.


### Test frameworks

Android SDK's testing framework vẫn trong giai đoạn phát triển, đặc biệt là UI tests. Android Gradle currently hiện tại áp dụng một test task [`connectedAndroidTest`](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing). Test task này chạy JUnit tests mà bạn tạo ra, sử dụng extension của JUnit kết hợp với helpers cho riêng Android[extension of JUnit with helpers for Android](http://developer.android.com/reference/android/test/package-summary.html). Điều này có nghĩa là bạn cần thiết bị thật hoặc emulator để run tests. Bạn có thể theo dõi hướng dẫn chính thức của Google [[1]](http://developer.android.com/tools/testing/testing_android.html) [[2]](http://developer.android.com/tools/testing/activity_test.html) for testing.

**Chỉ sử dụng [Robolectric](http://robolectric.org/) cho unit tests, không phải cho views.** Đây là một test framework với mục đích giúp test mà không cần thiết bị "disconnected from device" giúp tăng tốc độ phát triển, đặc biệt phù hợp với unit tests trên models và view models. Tuy nhiên, sử dụng Robolectric sẽ sinh ra lỗi nếu áp dụng cho UI tests. Bạn sẽ gặp vấn đề khi khi test thành phần UI liên quan đến animations, dialogs, etc, và việc này sẽ rất phức tạp bởi thực sự bạn đang "đi trong bóng tối" (test mà không nhìn thấy màn hình điều khiển).

**[Robotium](https://code.google.com/p/robotium/) trong khi đó lại giúp việc viết UI tests dễ dàng.** Ví dụ, viết test cases sẽ đơn giản như sau:

```java
solo.sendKey(Solo.MENU);
solo.clickOnText("More"); // searches for the first occurrence of "More" and clicks on it
solo.clickOnText("Preferences");
solo.clickOnText("Edit File Extensions");
Assert.assertTrue(solo.searchText("rtf"));
```

### Emulators

Nếu nghề nghiệp chính của bạn là phát triển Android, thì bạn nên mua bản Pro (có license) cho [Genymotion emulator](http://www.genymotion.com/). Genymotion emulators chạy với tốc độ frames/sec nhanh hơn AVD emulators mặc định. Họ còn có tools cho việc demo app, giả lập chất lượng kết nối mạng, vị trí GPS,... Bên cạnh đó, Genymotion cũng rất lý tưởng cho connected tests. Bạn có thể dùng nhiều (không phải tất cả) các thiết bị khác nhau, vì vậy giá cho Genymotion license thực sự rẻ hơn việc mua nhiều thiết bị thật.

Điều nên chú ý là: Genymotion emulators không cài sẵn Google services như Google Play Store và Maps. Bạn cũng có thể cần test một số APIs của Samsung, vì vậy có một thiết bị Samsung thật là cần thiết.

### Cấu hình Proguard

[ProGuard](http://proguard.sourceforge.net/) về cơ bản được dùng để giảm kích thước và che đậy code trong package.

Bạn có thể dùng hoặc không dùng ProGuard, mặc định Gradle sẽ khai báo việc cấu hình Proguard khi build bản apk release.

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

Để xác định phần code nào nên giữa lại, phần code nào cần che giấu (obfuscated), bạn phải chỉ rõ một hoặc vài điểm đánh dấu (entry points). Những entry points về cơ bản là các classes với main methods, applets, midlets, activities, etc.
Android framework sử dụng một cấu hình mặc định cho Proguard và bạn có thể tìm thấy trong `SDK_HOME/tools/proguard/proguard-android.txt`. Sử dụng cấu hình trên, phần chỉnh sửa thêm cho riêng từng project sẽ được định nghĩa trong file `my-project/app/proguard-rules.pro`. Những khai báo này sẽ bổ sung thêm cho phần cấu hình mặc định.

Nếu bạn cấu hình sai ProGuard thì thường bạn sẽ thấy ứng dụng crash ngay khi startup và báo lỗi `ClassNotFoundException` hay `NoSuchFieldException` hoặc tương tự, thậm chí build command (i.e. `assembleRelease`) không hề báo lỗi hay cảnh báo.
Điều này thường xảy ra bởi vì một trong hai lý do: 

1. ProGuard đã xoá class, enum, method, field hoặc annotation, mà nó tự xem là không cần thiết.
2. ProGuard đã thay đổi tên class, enum hoặc field, nhưng nó lại được sử dụng gián tiếp bằng tên chính.Ví dụ: qua Java reflection.

Kiểm tra `app/build/outputs/proguard/release/usage.txt` để xem các object đã bị xoá.
Kiểm tra `app/build/outputs/proguard/release/mapping.txt` để xem các object đã bị đổi tên.

Để tránh việc ProGuard *xoá* classes hoặc class members, dùng `keep` trong ProGuard config:
```
-keep class com.futurice.project.MyClass { *; }
```

Để tránh việc Proguard *đổi tên* classes hoặc class members,  dùng `keepnames`:
```
-keepnames class com.futurice.project.MyClass { *; }
```

Tìm hiểu thêm các ví dụ [Proguard](http://proguard.sourceforge.net/#manual/examples.html).

**Ngay từ ban đầu, hãy build bản release** để kiểm tra ProGuard có được cấu hình đúng hay không. Bên cạnh đó, bất cứ khi nào thêm một thư viện mới, hãy build bản release và test apk trên thiết bị. Đừng đợi đến khi app của bạn hoàn thành version "1.0" mới build bản release. Việc này giúp gặp và sửa vấn đề nhanh hơn.

**Gợi ý.** Lưu file `mapping.txt` cho mỗi bản release mà bạn publish cho users. Bằng cách giữ bản copy của file `mapping.txt` cho mỗi bản build release, bạn sẽ đảm bảo rằng bạn có thể debug một vấn đề nếu user gặp phải và submit một bản obfuscated stack trace.

**DexGuard**. Nếu bạn cần một công cụ tốt hơn cho việc tối ưu hay che giấu code khi release, hãy xem xét việc dùng [DexGuard](http://www.saikoa.com/dexguard), bản thương mại của ProGuard. Nó cũng giúp cho việc chia nhỏ Dẽ files để giải quyết vấn đề 65k methods limitation.

### Lưu trữ dữ liệu (Data storage)


#### SharedPreferences
Nếu bạn cần lưu trữ các dữ liệu đơn giản như flags và ứng dụng bạn chạy trên tiến trình đơn (single process) thì SharedPreferences có lẽ đủ cho bạn. Đây nên là một lựa chọn mặc định.

Có 2 trường hợp bạn không nên sử dụng SharedPreferences:

* *Hiệu năng(Performance)*: Dữ liệu của bạn phức tạp hoặc nhiều.
* *Xử lý truy cập dữ liệu đa tiến trình (Multiple processes accessing the data)*: Ví dụ, bạn có widgets và remote services chạy trên tiến trình riêng và cần dữ liệu đồng bộ (synchronized data).


#### ContentProviders

Trong trường hợp SharedPreferences không đủ cho bạn, bạn nên sử dụng platform standard ContentProviders, vì nó nhanh và an toàn.

Một vấn đề của ContentProviders là nó tốn nhiều thời gian cho việc viết code khi setup và cũng rất ít hướng dẫn chất lượng về vấn đề này. Tuy nhiên, nếu có thể thì nên tạo ContentProvider bằng cách sử dụng thư viện như [Schematic](https://github.com/SimonVT/schematic), nó sẽ giúp giảm đáng kể công sức và thời gian.

Nhưng đôi khi bạn vẫn cần tự viết parsing code để đọc data objects từ Sqlite và ngược lại. Bạn có thể serialize data objects, ví dụ dùng Gson, và chỉ lưu trữ dữ liệu dưới dạng string. Trong trường hợp này, performance sẽ kém đi nhưng bạn giảm được việc phải khai báo column cho tất cả các trường trong lớp dữ liệu (data class).


#### Sử dụng một thư viện ORM

Nhìn chung, chúng tôi không khuyến khích sử dụng Object-Relation Mapping library trừ khi bạn có dữ liệu đặc biệt phức tạp và vô cùng cần thiết. Thư viện ORM thường phức tạp và cần thời gian tìm hiểu. Nếu bạn quyết định dùng nó, bạn nên xác định xem nó có được xử lý an toàn _process safe_ hay không, vì có một sự đáng ngạc nhiên là rất nhiều các giải pháp ORM hiện tại không được xử lý tốt.


### Sử dụng Stetho 

[Stetho](http://facebook.github.io/stetho/) là một debug bridge cho Android applications được phát triển bởi Facebook. Nó tích hợp được với Chrome desktop browser's Developer Tools. Stetho cho phép bạn dễ dàng giám sát ứng dụng, đáng chú ý là network traffic. Nó cũng cho phép bạn giám sát, theo dõi và dễ dàng thay đổi SQLite databases và shared preferences. Tuy nhiên, bạn nên đảm bảo rằng Stetho chỉ được bật khi build bản debug chứ không phải bản release.

### Gửi lời cảm ơn tới (Thanks to)

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton and other Futurice developers for sharing their knowledge on Android development.

### Giấy phép (License)

[Futurice Oy](http://www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)
