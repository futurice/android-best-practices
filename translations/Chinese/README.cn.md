# Android 开发最佳实践
 
从 [Futurice](http://www.futurice.com) 公司的 Android 开发者中学到的经验。
遵循以下准则，避免重复发明轮子。若您对开发 iOS 或 Windows Phone 有兴趣，请看[**iOS Good Practices**](https://github.com/futurice/ios-good-practices) 和 [**Windows client Good Practices**](https://github.com/futurice/win-client-dev-good-practices) 这两篇文章。

## 摘要

* 使用 Gradle 和它推荐的工程结构
* 把密码和敏感数据放在 gradle.properties
* 不要自己写 HTTP 客户端,使用 Volley 或 OkHttp 库
* 使用 Jackson 库解析 JSON 数据
* 避免使用 Guava 同时使用一些类库来避免 *65k method limit*（一个 Android 程序中最多能执行 65536 个方法）
* 使用 Fragments 来呈现 UI 视图
* 使用 Activities 只是为了管理 Fragments
* Layout 布局是 XMLs 代码，组织好它们
* 在 layout XMLs 布局时，使用 styles 文件来避免使用重复的属性
* 使用多个style文件来避免单一的一个大style文件
* 保持你的 colors.xml 简短并且无水分(不要重复自己)，只是定义调色板
* 总是保持 dimens.xml 没有水分(不要重复自己)，定义通用常数
* 不要做一个深层次的 ViewGroup
* 在使用 WebViews 时避免在客户端做处理，当心内存泄露
* 使用 Robolectric 单元测试，Robotium 做UI测试
* 使用 Genymotion 作为你的模拟器
* 总是使用 ProGuard 和 DexGuard 来混淆项目


----------

### Android SDK

将你的 [Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools) 放在你的 home 目录或其他应用程序无关的位置。
当安装包含 SDK 的 IDE 的时候，可能会将 SDK 放在 IDE 的同一目录下，当你需要升级（或重新安装）IDE 或更换 IDE 时，会非常麻烦。
此外，如果你的 IDE 是在普通用户，不是在 root 下运行，还要避免把 SDK 放到一个需要 sudo 权限的系统级别目录下。

### 构建系统

你的默认编译环境应该是 [Gradle](http://tools.android.com/tech-docs/new-build-system)。
Ant 有很多限制，也很冗余。使用 Gradle，完成以下工作很方便：

- 构建 APP 不同版本的变种
- 制作简单的类似脚本的任务
- 管理和下载依赖
- 自定义密钥
- 更多

同时，Android Gradle 插件作为新标准的构建系统正在被 Google 积极地开发。

### 工程结构

有两种流行的结构：老的 Ant & Eclipse ADT 工程结构，和新的 Gradle & Android Studio 工程结构，你应该选择新的工程结构，如果你的工程还在使用老的结构，考虑放弃吧，将工程移植到新的结构。


老的结构:

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

新的结构

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

主要的区别在于，新的结构明确地分开了 'source sets' (`main`,`androidTest`)，Gradle 的一个理念。
你可以做到，例如，添加源组 ‘paid’ 和 ‘free’ 在 src 中，这将成为您的应用程序的付费和免费的两种模式的源代码。

你的项目引用第三方项目库时（例如，library-foobar），拥有一个顶级包名`app`从第三方库项目区分你的应用程序是非常有用的。
然后 `settings.gradle` 保持对这些库项目的引用，让 `app/build.gradle` 得以引用它们。

### Gradle 配置

**常用结构** 参考 [Google's guide on Gradle for Android](http://tools.android.com/tech-docs/new-build-system/user-guide)


**小任务** 除了(shell, Python, Perl, etc)这些脚本语言，你也可以使用 Gradle 制作任务。
更多信息请参考 [Gradle's documentation](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF)。


**密码** 在做版本的发布时你 app 的 `build.gradle` 需要定义 `signingConfigs`。此时你应该避免以下内容：


_不要这样做_。 这会出现在版本控制中。

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
	
而是，建立一个_不_加入版本控制系统的 `gradle.properties` 文件。

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```


那个文件是 gradle 自动引入的，你可以在 `build.gradle` 文件中使用，例如：

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
	

**使用 Maven 依赖方案代替使用导入 jar 包方案** 如果在你的项目中你明确使用了 jar 文件，那么它们可能成为冻结的版本，如 `2.1.1` .下载 jar 包更新他们是很繁琐的，这个问题 Maven 已经很好的解决了，这在 Android Gradle 构建中也是推荐的方法。例如：

```groovy
dependencies {
    compile 'com.squareup.okhttp:okhttp:2.2.0'
    compile 'com.squareup.okhttp:okhttp-urlconnection:2.2.0'
}
```    

**避免使用 Maven 动态依赖解决方案**
避免使用例如 `2.1.+` 之类的动态版本，因为它们可能导致构建不稳定，或者是不同构建之间 微妙的、没有被追踪到的不同。静态地使用例如 `2.1.1` 之类的版本号可以帮助创建一个更加可靠、可预测且可重复的开发环境。

### 集成开发环境和文本编辑器

**无论使用什么编辑器，一定要构建一个良好的工程结构** 编辑器每个人都有自己的选择，让你的编辑器根据工程结构和构建系统运作，那是你自己的责任。

当下首推 [Android Studio](https://developer.android.com/sdk/installing/studio.html)，因为它是由谷歌开发的，最接近 Gradle，默认使用最新的工程结构，已经到 beta 阶段（目前已经有 release 1.0 了），它就是为 Android 开发定制的。

你也可以使用 [Eclipse ADT](https://developer.android.com/sdk/installing/index.html?pkg=adt) ，但是你需要对它进行配置，因为它使用了旧的工程结构和 Ant 作为构建系统。你甚至可以使用纯文本编辑器如 Vim，Sublime Text，或者 Emacs。如果那样的话，你需要使 用Gardle 和 `adb` 命令行。如果使用 Eclipse 集成 Gradle 不适合你，或者你只是使用命令行构建工程，或迁移到 Android Studio 的话，这是最好的选择，因为 ADT 插件已经在最近被建议不再使用。

无论你使用何种开发工具，都要确保 Gradle 和新的项目结构保持官方的方式构建应用程序，避免你的编辑器配置文件加入到版本控制。例如，避免加入 Ant `build.xml` 文件。
特别地，如果你改变 Ant 的配置，不要忘记保持 `build.gradle` 是最新和起作用的。同时，善待其他开发者，不要强制改变他们的开发工具和偏好。

### 类库


**[Jackson](http://wiki.fasterxml.com/JacksonHome)** 是一个将 Java 对象与 JSON 互相转换的类库。[Gson](https://code.google.com/p/google-gson/) 是解决这个问题的流行方案，然而我们发现 Jackson 更高效，因为它支持替代的方法处理 JSON ：流、内存树模型，和传统 JSON-POJO 数据绑定。不过，请记住，Jsonkson 库比起 GSON 更大，所以根据你的情况选择，你可能选择 GSON 来避免 APP 65k 个方法限制。其它选择: [Json-smart](https://code.google.com/p/json-smart/) 和 [Boon JSON](https://github.com/RichardHightower/boon/wiki/Boon-JSON-in-five-minutes)


**网络请求，缓存，图片** 执行请求后端服务器，有几种交互的解决方案，你应该考虑实现你自己的网络客户端。使用 [Volley](https://android.googlesource.com/platform/frameworks/volley) 或 [Retrofit](http://square.github.io/retrofit/)。Volley 同时提供图片缓存类。如果你选择使用 Retrofit ，那么考虑使用 [Picasso](http://square.github.io/picasso/)来加载图片和缓存，同时使用 [OkHttp](http://square.github.io/okhttp/)作为高效的网络请求。Retrofit，Picasso 和 OkHttp都是由同一家公司开发（注：
是由 [Square](https://github.com/square) 公司开发），所以它们能很好地在一起运行。[OkHttp 同样可以和 Volley 在一起使用 Volley](http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835)。

**RxJava** 是函数式反应性的一个类库，换句话说，能处理异步的事件。
这是一个强大的和有前途的模式，同时也可能会造成混淆，因为它是如此的不同。
我们建议在使用这个库架构整个应用程序之前要谨慎考虑。
有一些项目是使用 RxJava 完成的，如果你需要帮助可以跟这些人取得联系：
Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen.
我们也写了一些博客：
[[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android),
[[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754),
[[4]](http://blog.futurice.com/android-development-has-its-own-swift).


如若你之前有使用过 Rx 的经历，从 API 响应开始使用它。
另外，从简单的 UI 事件处理开始运用，如单击事件或在搜索栏输入事件。
若对你的 Rx 技术有信心，同时想要将它应用到你的整体架构中，那么请在复杂的部分写好 Javadocs 文档。
请记住，其他不熟悉 RxJava 的开发人员，可能会非常难理解整个项目。
尽你的的全力帮助他们理解你的代码和 Rx。

**[Retrolambda](https://github.com/evant/gradle-retrolambda)** 是一个在 Android 和预 JDK8 平台上的使用 Lambda 表达式语法的 Java 类库。
它有助于保持你代码的紧凑性和可读性，特别当你使用如RxJava函数风格编程时。
使用它时先安装 JDK8 ，在 Android Studio 工程结构对话框中把它设置成为 SDK 路径，同时设置 `JAVA8_HOME` 和 `JAVA7_HOME` 环境变量，
然后在工程根目录下配置 build.gradle：

```groovy
dependencies {
	classpath 'me.tatarka:gradle-retrolambda:2.4.+'
}
```

同时在每个 module 的 build.gradle 中添加

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

Android Studio 为 Java8 lambdas 提供代码补全。如果你对 lambdas 不熟悉，只需参照以下开始学习吧：

- 任何只包含一个接口的方法都是"lambda 友好的"，代码可以被折叠成更紧凑的语法
- 如果对参数或类似有疑问，就写一个普通的匿名内部类，然后让 Android Studio 为你生成一个 lambda。

**当心 dex 方法数限制，同时避免使用过多的类库** Android apps，当打包成一个 dex 文件时，有一个 65536 个应用方法强硬限制[[1]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [[2]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [[3]](http://jakewharton.com/play-services-is-a-monolith/)。
当你突破 65k 限制之后你会看到一个致命错误。因此，使用一个正常范围的类库文件，同时使用 [dex-method-counts](https://github.com/mihaip/dex-method-counts) 工具来决定哪些类库可以在 65k 限制之下使用，特别地，避免使用 Guava 类库，因为它包含超过 13k 个方法。

### Activities 和 Fragments

[Fragments](http://developer.android.com/guide/components/fragments.html)应该作为你实现 UI 界面默认选择。你可以重复使用 Fragments 用户接口来组合成你的应用。我们强烈推荐使用 Fragments 而不是 Activity 来呈现 UI 界面，理由如下：

-  **提供多窗格布局解决方案** Fragments 的引入主要将手机应用延伸到平板电脑，所以在平板电脑上你可能有 A、B 两个窗格，但是在手机应用上 A、B 可能分别充满整个屏幕。如果你的应用在最初就使用了 Fragments，那么以后将你的应用适配到其他不同尺寸屏幕就会非常简单。

- **屏幕间数据通信** 从一个 Activity 发送复杂数据(例如 Java 对象)到另外一个 Activity ，Android 的 API 并没有提供合适的方法。不过使用 Fragment，你可以使用一个 activity 实例作为这个 activity 子 fragments 的通信通道。即使这样比 Activity 与 Activity 间的通信好，你也想考虑使用 Event Bus 架构，使用如 [Otto](https://square.github.io/otto/) 或者 [greenrobot EventBus](https://github.com/greenrobot/EventBus) 作为更简洁的实现。
如果你希望避免添加另外一个类库，RxJava 同样可以实现一个 Event Bus。


- **Fragments 一般通用的不只有 UI** 你可以有一个没有界面的 Fragment 为 Activity 提供后台工作。
进一步你可以使用这个特性来创建一个 [fragment 包含改变其它fragment的逻辑](http://stackoverflow.com/questions/12363790/how-many-activities-vs-fragments/12528434#12528434)
而不是把这个逻辑放在 activity 中。

- **甚至 ActionBar 都可以使用内部 fragment 来管理** 你可以选择使用一个没有 UI 界面的 fragment 来专门管理 ActionBar，或者你可以选择使用在每个 Fragment 中添加它自己的 action 来作为父 Activity 的 ActionBar。[参考](http://www.grokkingandroid.com/adding-action-items-from-within-fragments/)。

很不幸，我们不建议广泛的使用[嵌套的fragments](https://developer.android.com/about/versions/android-4.2.html#NestedFragments)，因为有时会引起[matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/)。我们只有当它有意义(例如，在水平滑动的 ViewPager 在像屏幕一样 fragment 中)或者他的确是一个明智的选择的时候才广泛的使用fragment。

在一个架构级别，你的 APP 应该有一个顶级的 activity 来包含绝大部分业务相关的 fragment 。你也可能还有一些辅助的 activity，这些辅助的 activity 与主 activity 通信被很简单地限制在这两种方法：[`Intent.setData()`](http://developer.android.com/reference/android/content/Intent.html#setData(android.net.Uri)) 或 [`Intent.setAction()`](http://developer.android.com/reference/android/content/Intent.html#setAction(java.lang.String))或类似的方法。


### Java 包结构

Android 应用程序在架构上大致是 Java 中的[Model-View-Controller](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)结构。
在 Android 中 Fragment 和 Activity 通常上是[控制器类](http://www.informit.com/articles/article.aspx?p=2126865)。
换句话说，它们是用户接口的部分，同样也是 Views 视图的部分。


正是因为如此，才很难严格地将 fragments (或者 activities) 严格的划分成控制器 controlloers 还是视图 views。
最好还是将它们放在自己单独的 `fragments` 包中。只要你遵循之前提到的建议，Activities 则可以放在顶级目录下。
若果你规划有 2 到 3 个以上的 activity，那么还是同样新建一个`activities` 包吧。

然而，这种架构可以看做是另一种形式的 MVC，它包含要被解析 API 响应的 JSON 数据，来填充的 POJO 的 `models` 包中和一个`views`包来包含你的自定义视图、通知、导航视图，widgets 等等。
适配器 Adapter 是在数据和视图之间。然而他们通常需要通过 `getView()` 方法来导出一些视图，所以你可以将 `adapters` 包放在 `views` 包里面。

一些控制器角色的类是应用程序级别的，同时是接近系统的。
这些类放在 `managers` 包下面。
一些繁杂的数据处理类，比如说 "DateUtils"，放在 `utils` 包下面。
与后端交互负责网络处理类，放在 `network` 包下面。


总而言之，以最接近用户的方式而不是最接近后端的去安排他们。

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


### 资源文件


- **命名** 遵循前缀表明类型的习惯，形如 `type_foo_bar.xml`。例如：`fragment_contact_details.xml`，`view_primary_button.xml`,`activity_main.xml`。

**组织布局文件** 若果你不确定如何排版一个布局文件，遵循以下规则可能会有帮助。

- 每一个属性一行，缩进 4 个空格
- `android:id` 总是作为第一个属性
- `android:layout_****` 属性在上边
- `style` 属性在底部
- 关闭标签`/>`单独起一行，有助于调整和添加新的属性
- 考虑使用 [设计时布局属性](http://tools.android.com/tips/layout-designtime-attributes)，Android Studio已经提供支持，而不是硬编码 `android:text` 
(译者注：墙内也可以参考 stormzhang 的[这篇博客](http://stormzhang.com/devtools/2015/01/11/android-studio-tips1/))。

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

作为一个经验法则，`android:layout_****` 属性应该在 layout XML 中定义，同时其它属性 `android:****` 应放在 style XML中。此规则也有例外，不过大体工作得很好。这个思想整体是保持 layout 属性(positioning，margin，sizing) 和 content 属性在布局文件中，同时将所有的外观细节属性（colors，padding，font）放在 style 文件中。


有以下这些例外:

- `android:id` 明显应该在 layout 文件中
- layout 文件中 `android:orientation` 对于一个 `LinearLayout` 布局通常更有意义
- `android:text` 由于是定义内容，应该放在layout文件中
- 有时候将`android:layout_width` 和 `android:layout_height`属性放到一个 style 中作为一个通用的风格中更有意义，但是默认情况下这些应该放到 layout 文件中。

**使用 styles** 几乎每个项目都需要适当地使用 style 文件，因为对于一个视图来说有一个重复的外观是很常见的。
在应用中对于大多数文本内容，最起码你应该有一个通用的 style 文件，例如：

```xml
<style name="ContentText">
	<item name="android:textSize">@dimen/font_normal</item>
	<item name="android:textColor">@color/basic_black</item>
</style>
```

应用到 TextView 中:

```xml
<TextView
	android:layout_width="wrap_content"
	android:layout_height="wrap_content"
	android:text="@string/price"
	style="@style/ContentText"
	/>
```


你或许需要为按钮控件做同样的事情，不要停止在那里。将一组相关的和重复 `android:****` 的属性放到一个通用的style中。


**将一个大的 style 文件分割成多个文件** 你可以有多个 `styles.xml` 文件。Android SDK 支持其它文件，`styles` 这个文件名称并没有作用，起作用的是在文件
里 xml 的`<style>`标签。因此你可以有多个 style 文件`styles.xml`，`style_home.xml`，`style_item_details.xml`，`styles_forms.xml`。
不用于资源文件路径需要为系统构建起的有意义，在 `res/values` 目录下的文件可以任意命名。



**`colors.xml` 是一个调色板** 在你的 `colors.xml` 文件中应该只是映射颜色的名称一个 RGBA 值，而没有其它的。不要使用它为不同的按钮来定义 RGBA 值。

*不要这样做*

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


使用这种格式，你会非常容易地开始重复定义 RGBA 值，如果这样的话，改变基本色将会变得很复杂。同时，这些定义是跟一些环境关联起来的，如 `button` 或者 `comment` ,
应该放到一个按钮风格中，而不是在 `color.xml` 文件中。


相反地，应该这样做:

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

向应用设计者那里要这个调色板，名称不需要跟"green"，"blue" 等等相同。
"brand_primary"，"brand_secondary"，"brand_negative" 这样的名字也是完全可以接受的。
像这样规范的颜色很容易修改或重构，会使应用一共使用了多少种不同的颜色变得非常清晰。
通常一个具有审美价值的 UI 来说，减少使用颜色的种类是非常重要的。


**像对待 colors.xml 一样对待 dimens.xml 文件** 与定义颜色调色板一样，你同时也应该定义一个空隙间隔和字体大小的“调色板”。
一个好的例子，如下所示：

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
	
布局时在写 margins 和 paddings 时，你应该使用 `spacing_****`尺寸格式来布局，而不是像对待String字符串一样直接写值。
这样写会非常有感觉，会使组织和改变风格或布局是非常容易。

**避免深层次的视图结构** 有时候为了摆放一个视图，你可能尝试添加另一个LinearLayout。你可能使用这种方法解决：

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


即使你没有非常明确的在一个 layout 布局文件中这样使用，如果你在 Java 文件中，从 views 填充（inflate）到其他 views 当中，也是可能会发生的。

它可能会导致一系列的问题。你可能会遇到性能问题，因为处理起需要处理一个复杂的 UI 树结构。
还可能会导致以下更严重的问题 [StackOverflowError](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views)。


因此尽量保持你的视图层次：学习如何使用 [RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html)，如何 [优化你的布局](http://developer.android.com/training/improving-layouts/optimizing-layout.html) 和如何使用[`<merge>` 标签](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts)。


**小心关于WebViews 的问题.** 如果你必须显示一个 web 视图，
比如说对于一个新闻文章，避免做客户端处理 HTML 的工作，
最好让后端工程师协助，让他返回一个 "*纯*" HTML。
[WebViews 也能导致内存泄露](http://stackoverflow.com/questions/3130654/memory-leak-in-webview)当保持引用他们的Activity，而不是被绑定到 ApplicationContext 中的时候。
当使用简单的文字或按钮时，避免使用 WebView。这时使用 TextView 或 Buttons 更好。

### 测试框架


Android SDK 的测试框架还处于初级阶段，特别是关于 UI 测试方面。Android Gradle 目前实现了一个叫[`connectedAndroidTest`](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing) 的测试，它使用一个 [JUnit 为 Android 提供的扩展插件 extension of JUnit with helpers for Android](http://developer.android.com/reference/android/test/package-summary.html)。可以运行你生成的 JUnit 来测试。


**只当做单元测试时使用 [Robolectric](http://robolectric.org/) ，不用views**
它是一个只求提供"不连接设备的"的为了加速开发的测试。它最适合用来做 models 和 view models 的单元测试。
然而，使用 Robolectric 测试时，它不精确地，也不完全对UI测试。
当你对有关动画的 UI 元素、对话框等做测试时会有问题，这主要是因为你是 “在黑暗中工作”（在没有可控的界面情况下测试）


**[Robotium](https://code.google.com/p/robotium/) 使写UI 测试非常简单。
** 对于 UI 测试你不需 Robotium 运行与设备连接的测试。
但它可能会对你有益，是因为它有许多来帮助类的获得和分析视图，控制屏幕。
测试用例看起来像这样简单：

```java
solo.sendKey(Solo.MENU);
solo.clickOnText("More"); // searches for the first occurence of "More" and clicks on it
solo.clickOnText("Preferences");
solo.clickOnText("Edit File Extensions");
Assert.assertTrue(solo.searchText("rtf"));
```


### 模拟器

如果你全职开发 Android App，那么买一个 [Genymotion emulator](http://www.genymotion.com/)许可吧。
Genymotion 模拟器比起典型的 AVD 模拟器有着更高的每秒帧数（FPS）。它有演示你 APP 的工具，高质量的模拟网络连接，GPS 位置，等等。它同时还有理想的连接测试。
你若涉及适配使用很多不同的设备，买一个 Genymotion 许可是比你买很多真设备便宜多的。

注意：Genymotion 模拟器没有装载所有的 Google 服务，如 Google Play Store 和 Maps。你也可能需要测试 Samsung 指定的 API，若这样的话你还是需要购买一个真实的 Samsung 设备。


### 混淆配置

[ProGuard](http://proguard.sourceforge.net/) 是一个在 Android 项目中广泛使用的压缩和混淆打包的源码的工具。

你是否使用 ProGuard 取决你项目的配置，当你构建一个 release 版本的 apk 时，通常你应该配置 gradle 文件。

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

为了决定哪些代码应该被保留，哪些代码应该被混淆，你不得不指定一个或多个实体类在你的代码中。
这些实体应该是指定的包含main 方法，applets，midlets，activities的类，等等。
Android framework 使用一个默认的配置文件，可以在 `SDK_HOME/tools/proguard/proguard-android.txt`目录下找到。自定义的工程指定的 project-specific 混淆规则，如在 `my-project/app/proguard-rules.pro` 中定义的，则会被添加到默认的配置中。

关于 ProGuard 一个普遍的问题，是看应用程序是否崩溃并报 `ClassNotFoundException` 或者 `NoSuchFieldException` 或类似的异常，即使编译是没有警告并运行成功。
这意味着以下两种可能：

1. ProGuard 已经移除了类，枚举，方法，成员变量或注解，考虑一下它是否是必要的。
2. ProGuard 混淆了类，枚举，成员变量的名称，但是这些名字又被拿原始名称使用了，比如通过 Java 的反射。

检查 `app/build/outputs/proguard/release/usage.txt`文件看有问题的对象是否被移除了。
检查 `app/build/outputs/proguard/release/mapping.txt` 文件看有问题的对象是否被混淆了。

为防 ProGuard *剥离* 需要的类和类成员，添加一个 `keep`选项在你的 proguard 配置文件中：
```
-keep class com.futurice.project.MyClass { *; }
```

防止 ProGuard *混淆* 一些类和成员，添加 `keepnames`:
```
-keepnames class com.futurice.project.MyClass { *; }
```

查看[本模版的 ProGuard 配置](https://github.com/futurice/android-best-practices/blob/master/templates/rx-architecture/app/proguard-rules.pro) 中的一些例子。
更多例子请参考 [Proguard](http://proguard.sourceforge.net/#manual/examples.html)。

**在构建项目之初，发布一个版本** 来检查 ProGuard 规则是否正确的保持了重要的部分。
并且，无论何时你添加了新的类库，做一个发布版本，并让这个 apk 在设备上跑起来测试一下。
不要等到你的 app 要发布 "1.0" 版本了才做版本发布，那时候你可能会碰到好多意想不到的异常，需要一些时间去修复他们。

**Tips** 每次发布新版本都要写 `mapping.txt`。每发布一个版本，如果用户遇到一个 bug，同时提交了一个混淆过的堆栈跟踪。通过保留 `mapping.txt` 文件，可以确定你可以调试的问题。

**DexGuard** 若果你需要核心工具来优化，和专门混淆的发布代码，考虑使用 [DexGuard](http://www.saikoa.com/dexguard)。它说
一个商业软件，ProGuard 也是由这个团队开发的。
它会很容易将 Dex 文件分割，来解决 65K 个方法限制问题。


### 致谢

感谢Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton 这些人和 Futurice 开发者分享他们的 Android 开发经验。

### 声明 License

[Futurice Oy](www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)

### 翻译 Translation

Translated to Chinese by [andyiac](https://github.com/andyiac)
Typo and compose by [Geno1024](https://github.com/geno1024)
