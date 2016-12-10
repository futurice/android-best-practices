#Android开发最佳实践
---
从[Futurice][link_Futurice_Oy]团队的Android开发者中学到的经验。 请遵循以下准则，避免重复发明轮子。若您对开发iOS或Windows Phone 有兴趣， 请看[iOS Good Practices][link_ios_practices] 和 [Windows client Good Practices][link_wp_practices]这两篇文章。

[link_ios_practices]:https://github.com/futurice/ios-good-practices
[link_wp_practices]:https://github.com/futurice/windows-app-development-best-practices

##总览
---
- **使用Gradle和Google推荐的项目结构**

- **把密码和敏感信息放在gradle.properties文件里**

- **不要自己去实现Http客户端，使用Volley或者OkHttp库**

- **使用Jackson库去解析Json数据**

- **由于Android的至多65000个方法的限制，请避免使用Google的Guava库并且尽量少的使用外部程序包**

- **使用Fragments来展示UI层**

- **Activities仅被用做管理Fragments**

- **布局文件(Layout XMLs)也是代码，把他们的结构组织好**

- **在布局文件(Layout XMLs)中使用styles来避免(翻译为"取代"也许更好)重复的属性值**

- **使用多个style文件而不是都写在一个文件里**

- **请保持你的color.xml短小简单(DRY)，只是定义成调色板就好**

- **也请保持你的dimens.xml简单(DRY)一些，只定义一些常量**

- **不要创建层次过多的ViewGroup**

- **避免在客户端进程中的跑WebView，小心内存泄露**

- **使用Robolectric来做单元测试，Robotium做连接(UI)测试**

- **使用Genymotion作为你的模拟器**

- **使用ProGuard或者DexGuard作为混淆器来混淆你的Android项目**

- **使用SharedPreferences来存储简单的持久化变量(persistence),其他的使用ContentProviders**
_________________________________________________________

##Android SDK
将你的Android SDK放置在你的/home路径下或者别的什么与应用程序无关的位置，一些IDE在下载的时候会包含SDK，并且可能会把SDK放置在和IDE相同的目录内。在这种情况下，当你升级或者重装IDE抑或改变IDE的时候会变得十分麻烦，对Linux和Mac os而言，同样需要避免将SDK放在其他系统级别的目录下，因为当你的IDE不在root用户下运行时，这可能会要求管理员权限。
##构建系统(Build system)
你的默认选择应该是Gradle，Ant有太多的限制和冗余。使用Gradle，你可以轻松的完成下面这些事情:

- 为你的App构建不同的风格或是版本
- 制作简单的脚本化(script-like)任务
- 管理和下载依赖
- 可定制化的密匙库(keystores)
- 等等...

并且Google一直在完善Android的Gradle插件，并打算将其作为一种新的标准构建系统。

##项目结构
现在有两种比较受欢迎的结构：一种是旧的使用Ant构建，并Eclipse ADT开发的项目结构，另一种是新的使用Gradle构建，Android Studio开发的项目结构。你应该选择后者，如果你的项目使用的是旧的结构，请考虑一下把它换成新的项目结构。

旧的结构:
	
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
新的结构:
	
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
---
看下面之前先科普一个概念'source sets',Gradle里的一个概念，我翻译为'源文件的位置'，我的理解是项目中不同的目录位置所包含的源文件的种类是不同的并且不能轻易改变。

比方说，你可以在`/res/drawable`目录下新建一个MainActivity.java文件，但是程序无法识别那个文件，因为在gradle构建的体系结构下，项目中每个目录都有其规定的职能。

继续翻译...

---

新旧两种结构主要的不同就在于，新的结构明确的区分了'source sets'(`main`, `androidTest`)，举个例子，你可以向`src`中添加'paid'和'free'两个源组，这将会称为你的app中包含付费和免费的两种模式的源代码。

拥有一个顶级包名`app`可以帮助你把自己的app和其他第三方项目库(例如, `library-foobar`)区分开，而`settings.gradle`可以不断引入这些库项目，其中`app/build.gradle`可以引用。

##Gradle配置

**通用结构**，请查看 [Google's guide on Gradle for Android][link_google]。

[link_google]:http://tools.android.com/tech-docs/new-build-system/user-guide

**一些小任务**，区别于像shell，Python，Perl等等一类的脚本语言，你可以使用Gradle来创建任务，你可以通过查阅[Gradle's documentation][line_Gradle's_documentation]来获取更多信息。

[line_Gradle's_documentation]:https://docs.gradle.org/current/userguide/userguide_single.html#N10CBF

**密码**，在你的App的build.gradle中你需要定义`signingConfigs`以便于发布版本(release)的构建，下面使你应该避免的:

不要这样做！这样这些信息会出现在你的版本控制系统中。

	signingConfigs {
    	release {
        	storeFile file("myapp.keystore")
        	storePassword "password123"
        	keyAlias "thekey"
        	keyPassword "password789"
    	}
	}
取而代之的是，创建一个**不会**被添加进版本控制系统的gradle.properties文件:

	KEYSTORE_PASSWORD=password123
	KEY_PASSWORD=password789
这个文件会自动被gradle导入，所以你可以在build.gradle中这样做:

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
**尽量使用Maven依赖解决找不到jar包的问题，而不是导入jar文件**，如果你二话不说直接导入jar文件，那么这些jar包将变成明确的固定版本，比如`2.1.1`或是其他，而且手动下载和更新jar文件非常的麻烦，然而Maven很好的解决了这个问题，并且在Android的Gradle构建中鼓励使用这种方式，举个例子:

	dependencies {
    	compile 'com.squareup.okhttp:okhttp:2.2.0'
    	compile 'com.squareup.okhttp:okhttp-urlconnection:2.2.0'
	} 

**避免在Maven中使用动态的依赖解决方案**，避免使用不确定或者说动态的版本，比如`2.1+`，这可能会导致不同的不稳定的构建，或是在不同的构建中产生细微到无法察觉的区别。使用类似`2.1.1`的静态版本可以带来更加稳定，可预测以及可以重复利用的开发环境。

##集成开发环境(IDE)和文本编辑器
**无论使用什么编辑器，它都要能很好的适应项目结构**，文本编辑器同样是一种个人选择，这需要自己将编辑器配置好并且在Android的项目结构和编译系统下运行正常。

现在更加推荐的IDE是Android studio，因为这是谷歌开发的，最接近Gradle构建方式，并且默认使用最新的项目结构，而且现在已经发布了稳定版本，完完全全是为Android开发量身定做的。

如果你愿意的话你也可以使用Ecilpse ADT做开发，但是你需要配置一下，因为它默认使用的旧的项目结构并且使用Ant作为构建工具。你甚至可以使用像Vim，Sublime Text或者Emacs这类的编辑器，这样的话，你需要使用命令行来使用Gradle和`abd`，如果eclipse整合Gradle对你来说不那么好用，那么你可以选择使用命令行来构建或者将项目整合到Android Studio，这是最好的选择，因为ADT插件最近被弃用。

无论你用什么编译器，请保证使用Gradle和新的项目结构作为正式的方法去构建你的应用，并且避免把你的编辑器配置文件添加到版本控制系统里。举个例子，不要把用Ant构建的build.xml添加进去。如果你修改了使用Ant构建的配置信息，不要忘记保持build.gradle的更新。再提一句，请友善对待其他开发者，不要强迫其他开发者改变他们惯用的开发工具。

##开发库
**[Jackson][link_Jackson]**是一个可以把对象和Json数据相互转换的Java开发库，当然[Gson][link_Gson]是更受欢迎的一种选择，然而我们发现自从Jackson支持处理Json的替代算法之后，在对数据流，内存树模型(in-memory tree model)和传统的Json与实体的数据绑定等方面，它的效率明显更高。即使这样，请记住，Jackson是一个比Gson更大的开发库，所以请根据你的需要来选择，你可以更倾向于使用Gson来避免至多65k个方法的限制。其他的一些选择:[Json-smart][link_Json-smart]和[Boon JSON][link_Boon Json]

[link_Jackson]:http://wiki.fasterxml.com/JacksonHome
[link_Gson]:https://code.google.com/p/google-gson/
[link_Json-smart]:https://code.google.com/p/json-smart/
[link_Boon Json]:https://github.com/RichardHightower/boon/wiki

**网络请求和图片缓存，**使用[Volley][link_Volley]或[Retrofit][link_Restrofit]，用它们实现的服务端请求方案是经过实践检验过的，所以你应该考虑在你的项目中使用他们。Volley同时提供加载和缓存图片的方法。如果你选择使用Retrofit，可以考虑使用[Picasso][link_Picasso]来加载和缓存图片，使用[OkHttp][link_OkHttp]进行高效率的Http请求。Retrofit，Picasso和OkHttp这三个都是同一个公司开发的，所以他们的兼容性非常好。[OkHttp也可以和Volley一起用来实现网络请求][link_OkHttp&&Volley]。

[link_Volley]:https://android.googlesource.com/platform/frameworks/volley
[link_Restrofit]:http://square.github.io/retrofit/
[link_OkHttp]:http://square.github.io/okhttp/
[link_Picasso]:http://square.github.io/picasso/
[link_OkHttp&&Volley]:http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835

**RxJava**是一个响应式框架，换句话说，处理异步事件。这是一个强大并且有发展空间的范例，但正因为它如此不同，可能会带来一些迷惑。我们建议当你想要把这个框架加入你的应用时小心一些。这里有一些使用Rxjava完成的应用，如果你需要和这些人交流:Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen,我们在这里留下来一些标记方便查阅:[1][link_1],[2][link_2],[3][link_3],[4][link_4]。

如果你之前没有任何关于Rx的经验，开始时只使用它从API中获取答复(Responses)。不然，也可以开始时只用用它来做简单的UI事件处理，像一个搜索栏中的点击事件或者键入事件。如果你对你使用Rx的技巧很自信，并且想把它加到整个应用架构中，那么记得把所有困难地方的帮助文档写好。记住别的程序员可能对RxJava并不熟悉，这可能会让他们很难维护这个项目，请尽你所能帮助他们了解你的代码，以及Rx。

[link_1]:http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android
[link_2]:http://blog.futurice.com/top-7-tips-for-rxjava-on-android
[link_3]:https://gist.github.com/staltz/868e7e9bc2a7b8c1f754
[link_4]:http://futurice.com/blog/android-development-has-its-own-swift

**[Retrolambda][link_Retrolambda]**是一个可以让你在android和其他使用JDK8以前的平台中使用Lambda语法的开发库，它可以帮助你保持你的代码的轻巧和可读性，特别当你使用一种类似RxJava的函数式编程风格时。为了使用这个库，你需要下载JDK8，并且在Android Studio的项目结构对话框中把JDK8作为你的SDK位置，然后配置JAVA8_HOME和JAVA7_HOME的环境变量，然后修改项目根目录下的build.grdle:
	
	dependencies {
    classpath 'me.tatarka:gradle-retrolambda:2.4.1'
	}
然后再每一个module的build.gradle中添加
	
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
Android Studio提供在Java8中lambda代码提示的支持，如果你刚刚接触lamdba，你可以看看下面的引导作为开始：

- 任何仅有一个方法的接口是"对lambda友好的"，并且可以折叠成更紧密的语法。
- 如果你对参数这一类的问题感到疑惑，写一个平常的匿名内部类，然后让Android Studio来帮你把它边转变成lambda函数。

[link_Retrolambda]:https://github.com/evant/gradle-retrolambda

**谨记dex的最大方法数量限制，并且避免使用过多的外部开发库。**Android应用在打包为dex文件的时候，有一个至多65536个被调用方法的牢固限制[[1]][link_dex_1] [[2]][link_dex_2] [[3]][link_dex_3]。如果你调用了超过限制数量的方法，你将会看到一个fatal error。因为这个愿意，引入规模尽量小的开发库，并且可以使用[dex方法计数器][link_dex_method_counts]工具来查看哪一个开发库可以使用来避免超过限制。特别需要避免使用Guava库，因为它包含超过13k个方法。

[link_dex_1]:https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71
[link_dex_2]:http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html
[link_dex_3]:http://jakewharton.com/play-services-is-a-monolith/
[link_dex_method_counts]:https://github.com/mihaip/dex-method-counts

##Activities和Fragments
现在在业界或是Futurice团队内部都没有一个对如何最佳使用Fragments和Activities来组织Android架构的统一意见。Square团队甚至[为了大多数与视图关联的构建架构写了一个开源库][link_mortar]，以避开使用Fragments。但是这仍然不是一个值得在业界广泛推广的方法。

由于Android中API的历史问题，你可以随意的使用Fragments作为UI中的碎片并在你的屏幕上展示。换句话说，Fragments通常依赖于UI层。Activities通常被作为控制器。它们有着十分特殊的的生命周期和状态。然而，你也可能会看到它们扮演不同的角色——activites可能扮演展示UI层的角色([屏幕之间的过渡效果][link_delivering_transitions])，或者[fragments可能只被用来做控制器][link_fragments_controllers]。我们建议你请小心使用，并采取明智的选择，因为只用fragments或者activities又或者使只有views填充的架构都有它们的缺点。这里有一些需要小心的意见，但请保持谨慎的态度:

- 避免到处使用[嵌套的fragments][link_nested_fragments]，因为可能会造成[matryoshka(俄罗斯套娃) bug][link_matryoshka_bugs]。只在有意义时再使用嵌套的fragments(举个例子，将一个在水平滑动的ViewPager中的fragment嵌入一个类似屏幕的fragment中)，抑或你觉得这的确是一个明智的决定。
- 避免在activities中放入过多的代码，任何时候只要有可能，让它们只作为轻量级的容器，并主要作为应用的生命周期，以及其他一些重要接口的API。使用单个fragment和activities的组合而不是直截了当的使用activities，把UI部分的代码让在activity的fragment中。这样可以提高fragment的重用性以防你之后把它修改成标签布局，或者放入一个包含多个fragment的屏幕。避免使用一个没有相配合的fragment的activity，除非你觉得这是一个明智的选择。
- 不要滥用的**Android系统级别的API(可能不准确,原文Android-level APIs)**,比如重度依赖于Intent以维持你的app的内部运作，这样可能会影响Android系统或者其他应用，从而产生一些bug或者卡顿。举个例子，众所周知，如果在你的app中，使用多个Intent在包之间传递信息，如果用户刚刚开机就打开了你的应用，可能导致用户体验到几秒的卡顿。

[link_mortar]:https://github.com/square/mortar
[link_delivering_transitions]:https://developer.android.com/about/versions/lollipop.html
[link_fragments_controllers]:http://developer.android.com/guide/components/fragments.html#AddingWithoutUI
[link_nested_fragments]:https://developer.android.com/about/versions/android-4.2.html#NestedFragments
[link_matryoshka_bugs]:http://delyan.me/android-s-matryoshka-problem/

##Java包架构
Android应用的包结构大致能归纳为模型-视图-控制器结构(MVC)，在Android中，Fragment和Activity实际上作为控制器类。另一方面来说，它们又十分明确的是用户接口的一部分，因此它们也是视图(View)部分。

因为这个原因，导致很难明确的区分fragements或者activities是严格意义上的控制器还是视图，所以最好让他们独自在一个`fragments`的包里。Activities可以放在包结构的最上层只要你照着前面几个部分的建议来做，如果你计划使用多个activities，那么还是把他们也放在一个`activities`的包里比较好。

另一方面，这种架构更像一个典型的MVC架构：有一个`models`的包含各种从API请求返回的JSON数据解析而成的简单Java对象(POJO)；一个`views`包含你自定义的视图、通知、操作栏视图或是组件等等；Adapters是一种看似无法归类的部分，存在于数据和视图之间，然而，它们一般需要通过getView()这个方法来生成一些视图，所以你可以把`adapters`包作为`views`的子包。

一些控制器类是整个应用都会用到的，并且和Android系统联系紧密，他们可以放在`managers`包里。一些复杂的数据处理类，类似'DataUtils'，可以放在`utils`包里面。而负责与后台进行数据交互的类则放在`network`包里就好。

总而言之，从更接近后台的架构转变为更接近用户的架构:

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

##资源文件(Resources)
**命名，**遵循在前缀上加入类型的惯例，形如`type_foo_bar.xml`。举几个例子，`fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`。

**布局文件的组织，**如果你不确定如何格式化布局文件，那么下面几个建议可能对你有所帮助。

- 一个属性一行，并且缩进4个空格
- `android:id`通常作为第一个属性
- `android:layout_****`属性放在顶层
- `style`属性放在底层
- 标签结束符`/>`独自一行，这样方面添加属性
- 不要在`android:text`中使用硬编码，考虑使用Android studio中的[Designtime属性][link_design_time]

[link_design_time]:http://tools.android.com/tips/layout-designtime-attributes

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
头一条规定，`android:layout_****`属性应该在布局文件里定义，而其他的属性`android:****`应该放在style.xml里面。这个规则有一些例外，但是通常都不会有什么问题。这个想法是把布局属性(位置,margin,大小)和内容属性放在布局文件中，而把其他的各种需要的细节(colors,padding,font)放在styles文件里。

这些例外是:
- 很明显，`android:id`应该放在布局文件里。
- `Lineralayout`中的`android:orientation`属性通常在布局文件有它的意义
- `android:text`应在布局文件中，因为它定义了内容
- 有时，在style中定义`android:layout_width`和`android:layout_height`也可以，但是在默认情况下这些应该出新在布局文件中

**使用styles文件。**几乎所有的项目都需要正确的使用styles文件，因为开发过程中很常见重复使用的view,至少你的应用应该有一个共同的文字容器的style，例如:

	<style name="ContentText">
    	<item name="android:textSize">@dimen/font_normal</item>
    	<item name="android:textColor">@color/basic_black</item>
	</style>

应用到TextView上:

	<TextView
    	android:layout_width="wrap_content"
    	android:layout_height="wrap_content"
    	android:text="@string/price"
    	style="@style/ContentText"
    />
你可能需要为按钮也设计一个类似的style，但是不要就此停止，去把那些重复的相关类似`android:****`的属性块移动到公共的style文件里。

**把巨大的style文件分散到其它文件里。**你不需要只用一个`style.xml`文件，Android SDK支持支持其它文件，其实`styles`这个名称没有有任何魔力，只因为它是一个含有`<style>`标签的XML的文件。因此你可以有`styles.xml`，`styles_home.xml`，`styles_item_details.xml`，`styles_forms.xml`。不像资源目录的名称对构建系统有特定的意义，`res/values`里面的文件名可以随便起。

**`colors.xml`只是一个调色板，**除此之外不要把任何其他东西放在你的`colors.xml`里面，让它只是把RGBA色彩和颜色名称匹配起来，不要为不同类型的按钮定义各种RGBA值。

不要这样做！

	<resources>
    	<color name="button_foreground">#FFFFFF</color>
    	<color name="button_background">#2A91BD</color>
    	<color name="comment_background_inactive">#5F5F5F</color>
    	<color name="comment_background_active">#939393</color>
    	<color name="comment_foreground">#FFFFFF</color>
    	<color name="comment_foreground_important">#FF9D2F</color>
    	...
    	<color name="comment_shadow">#323232</color>
这种格式下，你可以轻易的复制这些RGBA值，然而你会发现当你需要改变某个基础颜色时会非常麻烦。另一方面，这些定义的颜色与一些上下文(context)相关，类似"button"或者"comment"，所以他们应该呆在button的style文件内，而不是在`color.xml`里面。

这样做才对:
	
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
向设计师要一份应用的调色板，名称不需要是颜色的名称类似"green","blue"等等，像"brand_primary"，"brand_secondaray"，"brand_negative"也是完全可以接受的。这样写的话可以很方便的修改颜色，并且很清楚的就能看到使用过多少种不同的颜色。对一个好看的UI来说，不要使用太多种颜色使很重要的。

**dimens.xml的风格应该类似colors.xml，**你应该为自定义的间距和字体大小定义一个"调色板"，目的和定义各种基本颜色一样。下面是一个不错的例子:
	
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

你应该在布局中的margin和padding中使用`spacing_****`来表示尺寸，而不是使用魔法数字，这一点比较像处理布局中的string。这样带来一种一致性，同时让组织和修改style和layout变得更加简单。

**strings.xml**

为你的string定义一个类似命名空间的键值，并且不要害怕重复使用两个或者更多的键值来表示一个内容。语言是复杂的，所以命名空间应该体现语境，并且消除歧义。

**不好的**
	
	<string name="network_error">Network error</string>
	<string name="call_failed">Call failed</string>
	<string name="map_failed">Map loading failed</string>
**好的**

	<string name="error.message.network">Network error</string>
	<string name="error.message.call">Call failed</string>
	<string name="error.message.map">Map loading failed</string>
	
不要把string的值写成全部大写。让他们就像普通的文本就好(e.g.,首字母大写)。如果你需要让展示出来的字符串全部大写，那么请在TextView中使用内置的属性`textAllCaps`。

**不好的**

	<string name="error.message.call">CALL FAILED</string>

**好的**
	
	<string name="error.message.call">Call failed</string>

**避免深层次的视图结构。**有时你可能为了完成规定的view布局而只试图添加了另一个LinearLayout。这样一类可能发生下面的情况:

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
即使你没有明确的在布局文件中看到这种使用，但如果你在Java文件中把一个view填充到其他views当中，这也是可能会发生的。

随后一系列的问题可能会出现，你可能会遇到加载问题，因为这是一个复杂的UI树，而进程需要处理它，另一个严重的问题可能是[StackOverflowError][link_StackOverflowError]。

因此，保持你的视图结构尽可能平坦:学习如何使用[RelativeLayout][link_RelativeLayout]，如何[优化你的布局][link_optimize_layout]并且使用[`<merge>`标签][link_merge]

[link_StackOverflowError]:http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views
[link_RelativeLayout]:https://developer.android.com/guide/topics/ui/layout/relative.html
[link_optimize_layout]:http://developer.android.com/training/improving-layouts/optimizing-layout.html
[link_merge]:http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts

**小心有关WebView的问题。**当你要显示一个网页，比如一片新闻文章，避免让客户端进程去完全加载HTML，取而代之的是要后台返回"纯"的HTML。当WebView引用Activity的context而不是和ApplicationContext绑定在一起时，[WbView可能会导致内存泄露][link_leak_memory]。避免使用WebView来显示简单的文字或是按钮，使用TextViews或者Buttons。

[link_leak_memory]:http://stackoverflow.com/questions/3130654/memory-leak-in-webview

##测试框架
Android SDK的测试框架特别是UI测试部分尚未成熟。Android Gradle目前实现了一个测试任务叫做`connectedAndroidTest`，使用一个[JUnit对Android测试的扩展][link_junit_android]，可以运行任何一个你创建的JUnit测试。这意味着你在运行测试的时候需要连接一个设备或者一个模拟器。跟着官方指南[1][link_junit_1][2][link_junit_2]来进行测试。

[link_junit_1]:http://developer.android.com/tools/testing/testing_android.html
[link_junit_2]:http://developer.android.com/tools/testing/activity_test.html

**只使用[Robolectric][link_Robolectric]来做单元测试，但不对View部分测试。**这是一个旨在"无设备连接下"进行测试以提高开发速度的测试框架。是非常适合对模型(model)和视图模型(view model)的单元测试。然而，使用Robolectric测试是不精确的，因为没有完全考虑到UI测试。你可能在测试与动画，弹窗等等有关的UI测试上面遇到问题，并且由于"在黑暗中前进(walking in the dark)"即测试时没有对着屏幕进行操控，使得这些问题可能会更加复杂。

[link_Robolectric]:http://robolectric.org/

**[Robotium][link_Robotium]使得编写UI测试简单化。**你不需要Robotium跑连接测试来测试UI用例，但是它可能会对你更有帮助因为它有很多帮助类来获取和分析视图，并且控制屏幕。测试用例看起来向下面一样简单:
	
	solo.sendKey(Solo.MENU);
	solo.clickOnText("More"); // searches for the first occurence of "More" and clicks on it
	solo.clickOnText("Preferences");
	solo.clickOnText("Edit File Extensions");
	Assert.assertTrue(solo.searchText("rtf"));

[link_Robotium]:https://github.com/robotiumtech/robotium

##模拟器
如果你打算职业开发Android app，买一个[Genymotion模拟器][link_genymotion]的许可(license)吧。Genymotion模拟器比自带的AVD模拟器快了不止一点。它们有很多工具来演示你的程序，模拟网络连接质量，GPS定位等等。它们同时也可以进行很多理想的连接测试，你可以接触到很多不同(不是全部)的设备，所以花费在Genymotion许可证上的钱比买多个真机来测试便宜多了。

需要注意的是:Genymotion模拟器没有装载所有Google的服务，比如Google Play Store和Map。你也可能需要测试一些三星特有的API，那么买一个真实的三星设备还是很有必要的。

[link_genymotion]:http://www.genymotion.com/

##混淆配置
[ProGuard][link_proguard]通常被用来把Android项目中已打包的代码进行压缩和混淆。

是否使用ProGuard取决于你的项目配置。通常在你要发布apk的时候你可以通过配置gradle来使用ProGuard。

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
如果你想知道哪些代码需要保护起来哪些需要混淆或者丢在一旁，你必须在你的代码中具体声明一个或者多个进入点，这些进入点一般是main方法，applets,midlets或是activity等等。Android的框架使用一套默认的配置信息，你可以在`SDK_HOME/tools/proguard/proguard-android.txt`中找到。使用上面的配置，并定义自己项目特有的ProGuard混淆规则,一旦定义在`my-project/app/proguard-rules.pro`中，这些信息会被添加到默认配置中。

一个与ProGuard相关的共同问题是在应用启动时崩溃因为`ClassNotFoundException`或者`NoSuchFieldException`或者相似的异常。即使构建命令(i.e.`assembleRelease`)成功运行并且没有警告。这意味着发生了下面两个事情之一:

- 考虑一下ProGuard移除了的类，枚举，方法，成员变量或者注释，想想是否都必要。
- ProGuard已经混淆或者重命名了类，枚举类型或者成员变量名，但是它还是间接的被调用原来的名称，比如通过Java的反射。

检查`app/build/outputs/proguard/release/usage.txt`来查看有问题对象是否移除了，检查`app/build/outputs/proguard/release/mapping.txt`哪些有问题的对象被混淆过了。

为了防止ProGuard剥离你需要的类或者类成员，在你的ProGuard配置中添加一个`keep`选项:

	-keep class com.futurice.project.MyClass { *; }

为了防止ProGuard混淆了一些类和类成员，添加一个`keepnames`:

	-keepnames class com.futurice.project.MyClass { *; }
你可以学习下[这个ProGuard配置模板][link_proguard_config]里面的例子。你也可以在[Proguard][link_Proguard_example]里读到更多的样例。

[link_proguard]:http://proguard.sourceforge.net/
[link_proguard_config]:https://github.com/futurice/android-best-practices/blob/master/templates/rx-architecture/app/proguard-rules.pro
[link_Proguard_example]:http://proguard.sourceforge.net/#manual/examples.html

**在构建项目之初，发布一个版本**来检查ProGuard的规则是否正确保留了任何正确的东西。并且当你导入新的开发库的时候，你也需要发布一个版本并且在设备上测试apk。不要等到你的app到"1.0"版本的时候再做版本发布，这样你可能会遇到几个不那么令人开心的惊喜...并且没有充裕的时间让你去修复他们。

**小贴士。**每当你向你的用户发布一个版本的时候记得保存`mapping.txt`。通过保留每一个发布版本的`mapping.txt`的拷贝，在你的用户遇到一个bug同时提交一个混淆过的堆栈跟踪时，这可以保证你能调试这个问题。

**DexGuard。**如果你需要一个硬核一点的工具来优化并专门混淆发布的代码，那么考虑一下`DexGuard`。编写ProGuard的团队发布的一款商业软件。你可以很方便的分割Dex文件以解决至多65k个方法的限制。

##数据存储
###SharedPreference
如果你只需要存储简单的标志并且你的应用是单进程的，那么SharedPreference对你而言足够了，这是一个不错的默认选项。

如果你不想用SharePreference可能因为两个原因:

- 性能:你的数据可能复杂的多或者数据量很大
- 多个进程同时存取数据:你的组件或者远程服务在他们各自的进程里运行并请求同步数据。

###ContentProvider
如果SharedPreference不能满足你，你应该使用平台独立的ContentProvider，它更快，而且进程安全。

使用ContentProvider的唯一问题使需要构造大量的样板代码，就如同低质量的辅导课一样。然后是可以通过使用一个类似Schematic的开发库生成ContentProvider来显著提高效率的。

你仍然需要亲自写一些解析代码以方便数据对象和Sqlite中的数据行相互转换。你也可以把对象序列化，比如使用Gson，并且坚持把结果变成string类型的。这样一来你可能会损失一些效率，但另一方面，你不需要在数据行中声明各种类型的数据。

###使用ORM
我们通常不推荐使用ORM(对象-关系映射)库除非你的数据复杂的不同寻常并且你十分需要这样做。他们往往更加复杂而且需要更多时间去学习，如果你仍然决定使用ORM，请注意你应用的线程安全问题，因为现存的ORM解决方案都令人吃惊的没有注意到这一点。

##致谢
感谢Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton 以及其他Futurice开发者分享他们Android开发的知识。
##版权说明
[Futurice Oy][link_Futurice_Oy] Creative Commons Attribution 4.0 International (CC BY 4.0)

[link_Futurice_Oy]:http://futurice.com/