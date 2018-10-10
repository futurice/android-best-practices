# Androidの開発におけるベストプラクティス

以下は[Futurice](http://www.futurice.com)で働くAndroidアプリ開発者が学んだ教訓である. これらをしっかり読んで車輪の再開発はやめよう. もしiOSやWindows Phoneの開発に興味があるなら,[**iOS Good Practices**](https://github.com/futurice/ios-good-practices)と[**Windows client Good Practices**](https://github.com/futurice/win-client-dev-good-practices)を必ず確認しよう。

フィードバックは歓迎しているが、まずは[ガイドライン](https://github.com/futurice/android-best-practices/tree/master/CONTRIBUTING.md)を読んでほしい.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--best--practices-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1091)

## Summary

#### Gradleで推奨されるプロジェクト構成で開発しよう
#### パスワードや注意を要するデータはgradle.propertiesに書こう
#### 自分でHTTP Clientは作らず、VolleyやOkHttpを使おう
#### JSONをパースするならJacksonを使おう
#### メソッド数に65kの制限があるので、Guavaは避けて、かつライブラリは最小限に抑えよう
#### UIの描画はFragmentを使おう
#### ActivityはFragmentをただ管理するために使おう
#### Layout xmlをうまく整理しよう
#### Layout xmlの属性が重複するのを避けるためStyleを使おう
#### 大きなStyleを定義するよりも複数のStyleを定義しよう
#### colors.xmlは短くDRY（「Don't Repeat Yourself」意味が重複しないよう）にして、パレットで定義しよう
#### dimens.xmlもDRYにして、一般の定数を定義しよう
#### ViewGroupのネストは深くせずに浅くしよう
#### WebViewはメモリリークするため、クライアント側での処理は避けよう
#### ユニットテストにはRobolectricを、結合テストにはRobotiumを使おう
#### emulatorはGenymotionで決まり
#### 必ずProGuardもしくはDexGuardを使おう


----------

### Android SDK

[Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools)はホームディレクトリか他のアプリから独立した場所に置こう。いくつかのIDEはSDKを含んでいて、インストール時にSDKをIDEと同じディレクトリに置く事がある。このままではIDEをアップグレードや再インストールする場合、またIDEを変更する場合に面倒になる。
また、IDEがrootでないアカウントで動いている場合に、sudoが必要な他のシステムレベルのディレクトリにSDKを置く事も避けよう。

### Build system

デフォルトオプションに[Gradle](http://tools.android.com/tech-docs/new-build-system)を使おう。Antは制限が多く、コストが大きい。しかし、Gradleなら下記のことがシンプルに可能だ。

- あなたのアプリの異なるFlavorやVariantをビルドできる
- スクリプトのようにタスクを作る事ができる
- 依存関係を管理しつつダウンロードできる
- keystoreをカスタマイズできる
- その他諸々

そして、Googleは、AndroidのGradleプラグインを標準のビルドシステムとして盛んに開発している。

### プロジェクト構成

プロジェクト構成については、これまでのAnt & Eclipse ADTのプロジェクト構成と 新しいGradle & Android Studioのプロジェク構成の二つが有名であるが、後者の新しいプロジェクト構成を選ぶべきだ。もし前者の古いプロジェクト構成をつかっているなら、それは遺産だと考えて、新しいプロジェクト構成に変える事を考えた方がいい。

古い構成:

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

新しい構成:

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

一番の違いは新しいプロジェクト構成では'Source sets'(main, androidTest)が明確に分けられている事だ。これはGradleのコンセプトでもある。
これによって例えば`paid`と`free`というSource setを`src`の中に追加すると、'paid'と'free'というFlavorができる。

さらに`app`がtop-levelにあると、アプリで参照される`library-foobar`などの他のライブラリプロジェクトを区別するのに役立つ。`setting.gradle`が各ライブラリへの参照をもち、`app/build.gradle`から参照する事ができる。

### Gradleの設定

**一般的な構成** 
[Google's guide on Gradle for Android](http://tools.android.com/tech-docs/new-build-system/user-guide)を参照の事。

**小さなタスク** 
shell, Python, Perlなどのスクリプトの代わりに、Gradleの中にタスクをつくることができる。詳細は[Gradle's documentation](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF)を参照の事。

**パスワード** 
リリースビルドのために`build.gradle`の中で`signingConfigs`を定義しなければならないときがある。

下記はダメなケース。これではバージョン管理システムで管理されてしまう。

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

その代わりに、`gradle.properties`に下記のように書いて、このファイルをバージョン管理の管理外としよう。

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```

このファイルはgradleによって自動でimportされるので、このときの`build.gradle`は下記のように書くとよい。

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

**jarファイルを直接importするよりMavenを使う方が良い** 
jarファイルをプロジェクトに直接includeしている場合、version`2.1.1`のようにversionが固定されてしまう。さらに、jarファイルをダウンロードして、手動でアップデートするはめになり効率が悪い。Mavenならこれを解決できるし、Android StudioもMevenの使用をすすめている。`2.1.+`というようにversionの範囲を指定することもでき、指定した範囲の中での最新にアップデートしてくれる。
たとえば下記のように書く。

```groovy
dependencies {
    implementation 'com.netflix.rxjava:rxjava-core:0.19.+'
    implementation 'com.netflix.rxjava:rxjava-android:0.19.+'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.4.+'
    implementation 'com.fasterxml.jackson.core:jackson-core:2.4.+'
    implementation 'com.fasterxml.jackson.core:jackson-annotations:2.4.+'
    implementation 'com.squareup.okhttp:okhttp:2.0.+'
    implementation 'com.squareup.okhttp:okhttp-urlconnection:2.0.+'
}
```

### IDEとテキストエディタ

**エディターは何を使っても良いが、例のプロジェクト構成のまま扱えるものが良い** 
エディターはプロジェクト構成、ビルドシステムにあったものを選べば良い。

一番推奨されるIDEは[Android Studio](https://developer.android.com/sdk/installing/studio.html)である。Google が開発しており、Gradleと親和性が高いうえ、プロジェクト構成もデフォルトで推奨されているものを採用しているからだ。

もしお望みなら[Eclipse ADT](https://developer.android.com/sdk/installing/index.html?pkg=adt)をつかってもいいが、デフォルトで古いプロジェクト構成でかつAntを採用しているので設定が必要である。EclipseのGradleプラグインが動かない場合はコマンドラインでやるか、Android Studioに移行するかしかない。
またVim、Sublime TextやEmacsといった純粋なテキストエディタを使用することさえできる。その場合は、コマンドラインからGradleとadbを使えるよう準備する必要がある。

何を使っても良いが、Gradleを使う事と新しいプロジェクト構成で開発する事がオフィシャルな方法である事を頭に入れておかねばならない。またAntの`build.xml`などのエディタ特有の設定ファイルなどはバージョン管理外とすることもお忘れなく。特に、Antのビルド設定を変更した際は`build.gradle`が最新であり機能する事を確認することを怠ってはならない。また他の開発者が使っているツールの設定を強要することがないようにしよう。

### Libraries

**[Jackson](http://wiki.fasterxml.com/JacksonHome)** はObjectをJSONに変換、またその逆を行うライブラリである。[GSON](https://code.google.com/p/google-gson/)もJsonのライブラリとして有名だが、streaming、in-memory tree model, Json data binding等をサポートしている点でJacksonの方がいくらか優れていると判断した。ただ覚えておいてほしいのはJacksonがGsonよりボリュームの大きなライブラリである事だ。65k制限を避ける為にGSONの方が有効なケースもあり得る。また他には[json-smart](https://code.google.com/p/json-smart/)、[Boon JSON](https://github.com/boonproject/boon/wiki/Boon-JSON-in-five-minutes)という選択肢もある。

**ネットワーク、キャッシュ、画像処理について。**バックエンドへのリクエスト処理の実装についていろいろ試した結果言えるのは、自分でクライアントを実装しない方がいいということだ。[Volley](https://android.googlesource.com/platform/frameworks/volley)や[Retrofit](http://square.github.io/retrofit/)を使おう。Volleyはまた画像のロード、キャッシュのヘルパーを提供してくれている。Retrofitを選ぶ人は、[Picasso](http://square.github.io/picasso/)という画像ライブラリ、またHttpリクエストに有効な[OkHttp](http://square.github.io/okhttp/)の導入も考えると良い。この三つのRetrofit、Picasso、OkHttpは一つの会社で作られている。そのため、これらのライブラリは互いをよく補っている。現に[OkHttpはVolleyと共に使われることがよくある。](http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835)

**RxJava**はリアクティブプログラミングを行う、つまり非同期イベントを扱う為のライブラリだ。これは強力で有望なものだが、通常のプログラミングと異なりすぎる為に困惑をもたらす事がある。私たちはこのライブラリをアプリ全体のアーキテクチャに使う前に注意して扱うことをお勧めする。RxJavaを用いて作った我々のプロジェクトがいくつかあった。もし助力が必要なら次のメンバに話してみると良いかもしれない: Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen
またいくつかブログの記事も書いている。[\[1\]](http://futurice.com/blog/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android) [\[2\]](http://futurice.com/blog/top-7-tips-for-rxjava-on-android) [\[3\]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754) [\[4\]](http://futurice.com/blog/android-development-has-its-own-swift)

もしRxを使った経験が以前にないのなら、まずはAPIのレスポンスのみ、もしくはクリックイベントや検索フィールドのテキスト変更イベントなどのUIのイベントハンドリングのみに適用するところから始めると良い。
逆にRxに自信があってプロジェクト全体で使いたい場合は、トリッキーな場所にJavadocを書くと良い。RxJavaのようなよく知られていない他のプログラミング手法を使用する場合はメンテナンスが大変であることを忘れてはいけない。あなたのRxで書かれたコードが他の人も理解できるようにベストを尽くそう。

**[Retrolambda](https://github.com/evant/gradle-retrolambda)**はAndroidやJDK8以前のプラットフォームでlambda記法を使う事ができるようになるライブラリである。特にRxJavaなど関数型スタイルを採用する場合において、これはコードを短く、見やすくする。
JDK8をインストールして、SDKのときと同じようにAndroid Studioに設定する必要がある。
`JAVA8_HOME`と`JAVA7_HOME`を設定後、ルートのbuild.gradleを下記のように書き

```groovy
dependencies {
    classpath 'me.tatarka:gradle-retrolambda:2.4.+'
}
```

そしてそれぞれのモジュールをbuild.gradleに追加する

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

Android StudioはJava8のlambda記法のためのコードアシストをサポートしている。もし、あなたがlambdaのビギナーであれば、下記にならって使ってみよう

- 一つだけメソッドがあるインターフェイスはどれも“lambdaに親和性あり“で、構文をより短くできる
- もしパラメータなどに迷ったときは、普通のインナークラスを書いて、Android Studioでそれをlambdaに盛り込ませてみよう

**65k制限に注意して、たくさんのライブラリを使うのを避けよう。** 
Androidアプリはdexファイルにパッケージングする際に関数の参照は65536個までという厳格な制限がある。[\[1\]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [\[2\]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [\[3\]](http://jakewharton.com/play-services-is-a-monolith/)。制限を超えた場合はFatal Errorが起きる、そのため、ライブラリは最小限とし、制限内に収まるよう[dex-method-counts](https://github.com/mihaip/dex-method-counts)ツールを使って使用するライブラリを決めよう。特にGuavaは13kも関数があるので避けた方がいい。

### Activities and Fragments

AndroidにおいてはUIの実装は[Fragments](http://developer.android.com/guide/components/fragments.html)で行うべきである。Fragmentsは、アプリ内で構成出来る再利用可能なUIである。ActiivtyではUIの実装をせずにFragmentsで行うことをすすめる。理由は下記である。

- **multi-pane layoutの解決について。** Fragmentは元はphoneアプリケーションをTableアプリケーションへ拡張するためのものとして紹介された。phoneアプリでAもしくはBというpaneが占めている場合に、TabletではAとBを表示することができる。はじめからfragmentを使っているなら、あとで異なるフォームファクタへ変更する事が簡単になる。

- **スクリーン間のコミニケーションについて。** AndroidはActivity間の通信として複雑なデータ(たとえばJava Object)を送るAPIを提供していない。Fragmentであれば、その子Fragment間の通信経路としてActivityのインスタンスを使う事ができる。とはいえ、[Otto](https://square.github.io/otto/)や[greenrobot-EventBus](https://github.com/greenrobot/EventBus)を使ってEventBusを使いたいと思うだろう。他のライブラリの追加を避けたい場合はRxJavaを用いてEventBusを実装する事も可能である。

- **FragmentはUIだけじゃなくても十分につかえる。** ActivityのbackgroundワーカとしてfragmentをUIなしでもつこともできる。Activityにロジックを持たせる代わりに、[複数のfragmentを変更するロジックを持ったfragmentを作ることも可能である](http://stackoverflow.com/questions/12363790/how-many-activities-vs-fragments/12528434#12528434)。

- **FragmentからActionBarを管理できる。** ActionBarを管理するだけのUIを持っていないFragmentをもっても良いし、また現在表示されているFragmentにActionBarの振る舞いをまかせても良い。詳しくは[こちら](http://www.grokkingandroid.com/adding-action-items-from-within-fragments/)を参考にしてほしい。

[matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/)が起きるので、[Fragmentを大規模にネスト](https://developer.android.com/about/versions/android-4.2.html#NestedFragments)すべきではない。例えば、横スライドするViewPagerをもったfragmentのなかに表示するfragmentといった理にかなったケースもしくは十分に考えられている場合のみ使用すべきである。

設計レベルの話をすると、アプリは一つのトップレベルのActivityを持つべきである。他のActivityは[`Intent.setData()`](http://developer.android.com/reference/android/content/Intent.html#setData(android.net.Uri))や[`Intent.setAction()`](http://developer.android.com/reference/android/content/Intent.html#setAction(java.lang.String))などで簡単に遷移でき、メインのActivityをサポートするくらいにすると良い。

### Java package 構成

Androidにおける設計はおおよそ[Model-View-controller](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)である。[Androidにおいては、FragmentとActivityがControllerに相当する](http://www.informit.com/articles/article.aspx?p=2126865)が、一方で、これらは明らかにユーザインターフェイスであり、Viewでもある。

以上のことから、FragmentまたはActivityを厳格にControllerかViewかに分類する事は難しい。そのため分類せずにfragmentは`fragments`というパッケージに納めると良い。Activityはトップレベルのパッケージに置いても良いが、もし2つ３つ以上あるなら`activities`というパッケージを作ると良い。

その他は典型的なMVCだ。`models`パッケージはAPIレスポンスをJSONパースした結果のPOJOを入れ、`views`にはカスタムView,Notifications,ActionBar View, ウィジェットなどを配置する。Adapterはviewとデータの間にいるので微妙だが、`getView()`メソッドからViewを生成する必要がよくあるので、`views`パッケージのサブパッケージとして`adapters`を作りそこに配置すると良い。

アプリ内で多様に使われAndroidシステムに密接なコントローラクラスは`managers`パッケージへ、各Dataを混ぜこぜにつかうDataUtilのようなクラスは`utils`へ、バックエンドとインタラクティブに反応するクラスは`network`パッケージに配置しよう。

バックエンドに最も近いところからユーザに最も近いところへという順で並べると下記のような構成になる。

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

**命名規則について。** 
たとえば、`fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`といったように、`type_foo_bar.xml`というタイプを接頭辞とする慣習に従おう。

**Layout xmlを整理しよう。** 
もしLayout xmlのフォーマット方法が不明確なら下記の慣習が役立つと思われる。

- 1 attribute につき 1 lineでインデントは4スペース
- `android:id`は常に一番始めに
- `android:layout_****`は上に
- `style`は一番下
- attributeを追加しやすいように`/>`のみで1 line
- `android:text`をハードコーディングするよりは、[Designtime attributes](http://tools.android.com/tips/layout-designtime-attributes)を使う事を考えた方が良い

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

大雑把に言うと、`android:layout_****`はLayout xmlで定義して、それ以外の`android:****`はStyle xmlの中で定義すると良い。下記を除くと大抵この方法でうまくいく。

- `android:id`は確実にLayout xml内に
- LinearLayoutの'android:orientation`はLayout xml内に
- `android:text`はLayout xmlに
- 時々Style xmlに`android:layout_width`と`android:layout_height`を定義してうまく行く事がある。(しかし、デフォルトではこれらはlayout filesの中にある)

**Styleを使おう。** 
Viewに統一感を持たせる為にStyleを適切に使う必要がある。Viewが繰り返し出ることはよくあることだからだ。少なくとも下記のようにText用のStyleは持っておいた方が良い。

```xml
<style name="ContentText">
    <item name="android:textSize">@dimen/font_normal</item>
    <item name="android:textColor">@color/basic_black</item>
</style>
```

このスタイルはTextViewで下記の用に使う事ができる。

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/price"
    style="@style/ContentText"
    />
```

同じことをボタンにもする必要があるが、そこで終わりにせず、関連性のあるまたは繰り返されたグループを移動して、`android:****`を共通のStyleに書き出そう。

**大きなStyle Fileを避け、複数に分けよう。** 
１つの`styles.xml`だけを持つ事は止めた方が良い。styleファイルは`style_home.xml`、`style_item_details.xml`、`styles_forms.xml`と言ったように複数持つ事ができる。`res/values`の中のファイル名は任意である。

**`color.xml`はカラーパレットである。** 
colors.xmlは色の名前で定義しよう。下記のように各ボタンによって定義するといったようなことはすべきじゃない。

*下記は良くない例。*

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

こういう書き方をしてしまうと基本色を変更する場合などに対応しづらい。"button"や"comment"といった内容はbutton styleで定義すればよく、`colors.xml`の中に定義すべきではない。

`colors.xml`は下記のように定義しよう。

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

nameは色の名前でなく"brand_primary", "brand_secondary", "brand_negative"などとしても良い。そうする事で色の変更がしやすくなり、またどれだけの色が使われているかがわかりやすい。通常、きれいなUIの為には使用する色を減らす事も重要だ。

**dimens.xmlもcolors.xmlのように扱おう。** 
典型的なスペースやフォントサイズをcolors.xmlのパレットのように定義しよう。下記は良い例である。

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

marginやpaddingをハードコードするのではなく、`spacing_****`を使用するようにしよう。そうする事で簡単に全体に統一感を持たす事ができ、また整理も簡単にできる。

**Viewの深いネストは止めよう。** 
下記のようにLinearLayoutを組み合わせてViewを作ろうとすることがある。
そうするとLayout xmlは下記のようになる。

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

もし、一つのLayout ファイルに書いていなくてもJava側でinflateした際に同じ状況になる事もあり得る。

これはいくつかの問題起こす。まずはきっとあなたも経験しただろう、UIの煩雑さによるパフォーマンス低下の問題である。他にも深刻な[StackOverFlow](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views)を起こす可能性もある。

以上の理由から、Viewの階層はなるべくフラットにするべきである。そのために[RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html)の使い方、[Layoutの最適化](http://developer.android.com/training/improving-layouts/optimizing-layout.html)の方法、[\<merge\>タグ](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts)の使い方を知っておこう。

**WebViewの参照問題に気をつけよう。** 
例えばNewsの記事などのweb pageを表示する必要がある場合、クライアントサイドでHTMLを整形する事は止めた方が良い。HTMLはバックグラウンドプログラマに用意してもらおう。また[WebViewはActivityの参照を持つときにメモリリークしうる](http://stackoverflow.com/questions/3130654/memory-leak-in-webview)。Activityの代わりにApplicationContextを使用しよう。単純なテキストやボタンを表示するのにTextViewやButtonではなくWebViewを使用する事も避けた方がいい。


### テストフレームワーク

Android SDKのテストフレームワークは特にUIテストにおいてまだまだ未熟なところがある。
Android Gradleに、あなたがAndroidのJUnitのヘルパーを使って書いたJUnitのテストを走らせる[connectedAndroidTest](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing)がある。これはdeviceもしくはemulatorをつなぐ必要がある。次のテストガイドを見ておこう。[\[1\]](http://developer.android.com/tools/testing/testing_android.html) [\[2\]] (http://developer.android.com/tools/testing/activity_test.html)

**viewを使わないUnitテストには[Robolectric](http://robolectric.org/)を使おう。** 
このテストフレームワークはデバイスにつなぐ必要がないため開発効率があがる。UIのテストには向いていないがモデルとViewモデルのテストに適している。

**[Robotium](https://code.google.com/p/robotium/)は簡単にUIテストを作る事ができる。** 
このテストフレームワークにはViewの取得、解析する為のヘルパーメソッド、スクリーンをコントロールする為のヘルパーメソッドが多く用意されている。テストケースも下記のようにシンプルに書く事ができる。

```java
solo.sendKey(Solo.MENU);
solo.clickOnText("More"); // searches for the first occurence of "More" and clicks on it
solo.clickOnText("Preferences");
solo.clickOnText("Edit File Extensions");
Assert.assertTrue(solo.searchText("rtf"));
```

### Emulator

Androidアプリを専門で開発していくなら[Genymotion emulator](http://www.genymotion.com/)のライセンスは買っておいた方が良い。Genymotionは通常のAVD Emulatorよりも早い。またアプリのデモ、ネットワーク接続品質のエミュレート、GPS、などなどを行う為のツールがそろっている。
テストはたくさんの端末で行わなければならないが、実際にたくさんの端末を買うよりはGenymotionのライセンスを買う方がコスパが良い。

注: GenymotionはGoogle Play StoreやMapなどがインストールされていない。またSamsungの特定のAPIをテストしたい場合は、実際のSamsungの端末を使う必要がある。

### Proguardの設定

[ProGuard](http://proguard.sourceforge.net/)はAndroid Projectでコードを圧縮、難読化するために使われる。

Proguardを使用するか否かはプロジェクトの設定に依る。通常リリースapkをビルドする際にProguardを使う場合gradleを下記のように設定する。

```groovy
buildTypes {
    debug {
        runProguard false
    }
    release {
        signingConfig signingConfigs.release
        runProguard true
        proguardFiles 'proguard-rules.pro'
    }
}
```

どのコードを保存し、どのコードを捨て難読化するかをを明確に示さなければならない。デフォルトの設定では`SDK_HOME/tools/proguard/proguard-android.txt`を使用する。また`my-project/app/proguard-rules.pro`に定義する事でデフォルトのものに追加することができる。

ProGuard関連のよくある問題でビルドが成功したにもかかわらず、アプリの起動で`ClassNotFoundException`や`NoSuchFieldException`などのExceptionを発生してアプリがクラッシュすることがある。これは以下の二つのどちらかを意味する。

1. ProGuardが必要なクラス、enum、関数、フィールド、アノテーションを削除してしまった。
2. リフレクションなどを使っており難読化によってリネームされたクラスへの参照ができない。

もしあるオブジェクトが削除されている疑いがあるなら`app/build/outputs/proguard/release/usage.txt`を確認しよう。オブジェクトの難読化結果を見るなら`app/build/outputs/proguard/release/mapping.txt`を確認しよう。

必要なクラスや関数の削除を防ぐには`keep`オプションをproguard configに追加しよう。
```
-keep class com.futurice.project.MyClass { *; }
```

難読化を防ぐには`keepnames`を使う。
```
-keepnames class com.futurice.project.MyClass { *; }
```

**Tip** 
リリースするたびに`mapping.txt`を保存しておこう。ユーザがバグを踏み、難読化されたスタックトレースを送ってきた際にデバッグする事ができる。

**DexGuard**
さらに最適化され、さらに難読化されたものを考えるならDexGuardの採用を考えよう。DexGuardはProGuardを作った同じチームが開発している商用のものである。さらにDexGuardなら簡単にDexファイルを分割し65k制限を解決する。

### Thanks to

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton and other Futurice developers for sharing their knowledge on Android development.

### License

[Futurice Oy](www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)


Translation
===========

Translated to Japanese (`ja`) by **Shinnosuke Kugimiya, Aska Kadowaki**.

Original content by [Futurice Oy](http://www.futurice.com).

