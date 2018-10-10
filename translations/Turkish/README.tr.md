# Android Geliştirmede En İyiyi Yakalamak

[Futurice](http://www.futurice.com)'deki Android Geliştiriciler tarafından öğrenilmiş Android derslerini içerir. Bu kılavuzları takip edin ve tekerleği tekrar icat etmekle vakit harcamayın. Eğer IOS ve Windows Phone platformlarında da geliştirme yapmakla ilgileniyorsanız, [**iOS Good Practices**](https://github.com/futurice/ios-good-practices) ve [**Windows App Development Best Practices**](https://github.com/futurice/windows-app-development-best-practices) dökümanlarını da incelemeyi unutmayın.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--best--practices-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1091)

## Özet

#### [Gradle ve önerilen proje yapısı](#build-system)
#### [Şifreleri ve hassas verileri gradle.properties içerisine koyun](#gradle-configuration)
#### [JSON data parse etmek için Jackson kütüphanesini kullanın.](#libraries)
#### [Kendi HTTP client'ınızı yazmak yerine, Volley veya OkHttp kütüphanelerini kullanın.](#networklibs)
#### [*65k method limit* olayından dolayı Guava ve çok fazla kütüphane kullanmaktan kaçının.](#methodlimitation)
#### [UI ekranları için Fragment kullanmayı tercih edin.](#activities-and-fragments)
#### [Activity dosyalarını sadece Fragment'ları yönetirken kullanın.](#activities-and-fragments)
#### [Layout XML dosyaları da kodlardan oluşur, onları düzgün organize edin.](#resources)
#### [Layout XML dosyalarında nitelik çakışmasını engellemek için style dosyaları kullan.](#styles)
#### [Bir tane büyük style dosyası yerine bu dosyayı parçalara bölün.](#splitstyles)
#### [colors.xml dosyasını kısa ve sade tut, sadece renk paletini belirle](#colorsxml)
#### [dimens.xml dosyanıda sade tut, sadece genel sabitleri belirle.](#dimensxml)
#### [ViewGroup'lar için derin bir hiyerarşiden kaçın.](#deephierarchy)
#### [WebView üzerinde client-side işlemlerden kaçın, ve leakleri hesaba kat](#webviews)
#### [Unit Test yaparken Robolectric, UI testler içinse Robotium  kullan.](#test-frameworks)
#### [Emülatör olarak Genymotion kullan](#emulators)
#### [Her zaman ProGuard veya DexGuard kullan](#proguard-configuration)
#### [Basit devamlılıktaki uygulamalar için SharedPreferences, diğerleri içinse ContentProvider kullan.](#data-storage)
#### [Uygulamanı debug ederken Stetho kullan.](#use-stetho)


----------

### Android SDK

[Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools) dosyanızı uygulamalardan bağımsız ve erişebileceğiniz bir yere koyun. Bazı IDE'ler yüklenirken, kendisiyle birlikte SDK dosyalarınıda yükler ancak bunu yaparken kendi ana dosya dizinine yükleyebilir. Bu kullanışlı değildir, çünkü IDE değiştirmek istediğinizde veya güncelleme yaparken bu verilerde kayba uğrayabilirsiniz. Ayrıca eğer IDE'niz root üzerinde çalışmıyorsa, SDK dosyalarınızı sistem seviyesinde veya admin izni isteyen bir yere koymayın.  .

### Yapı Sistemi

[Gradle](http://tools.android.com/tech-docs/new-build-system) bu durumda ilk seçeneğiniz olmalı. Ant daha sınırlı ve daha çok komut kullanmanızı istemektedir. Gradle ile ise:

- Uygulamanın farklı yerlerini oluşturur
- Okunabilir görevlendirme yapmanızı sağlar
- Dependency'leri yönetir ve indirir,
- Keystore'ları düzenler
- ve daha fazlasını yapar

Android'in Gradle eklentisi, Google tarafından durmadan geliştirilmekte ve dependency kontrolünde en ön plana çıkan seçenek olmaktadır.

### Proje Yapısı

İki seçeneğimiz var: Eski ADT ve Eclipse proje yapısı, ve yeni Gradle & Android studio proje yapısı.Siz yeni olanı seçmelisiniz. Eğer eski yapıyı kullanıyorsunuz, artık onun tarih olduğunu varsayıp yeni yapuya geçirin.

Eski yapı:

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

Yeni yapı:

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

Bu iki yapı arasındaki ana fark, yeni fark kesin olarak 'kaynak dosyaları'  (main, androidTest), Gradle ile birbirinden ayırmaktadır. Örneğin, 'paid' ve 'free' adı altında iki kaynak dosyasını 'src' içerisine ekleyip uygulamanıza bu kaynakların özelliklerinden faydalanmasına olanak sağlayabilirsiniz.

Yüksek kalitede bir 'app' klasörü sahibi olmak  uygulamanızda kullanılan başka kütüphane projelerinden projelerinizin ayrılmasına olanak sağlar. `settings.gradle` ise `app/build.gradle` referanslarını tutacağı kütüphaneleri takip eder.

### Gradle Düzenlemeleri

**Genel Yapı** [Google'ın Android için Gradle Rehberi](http://tools.android.com/tech-docs/new-build-system/user-guide) takip edin.

**Küçük işlemler** shell, Python, Perl, gibi dillerde script ile işlem yapmak yerine, Bu işlemleri Gradle üzerinden yapabilirsiniz.Bunun için [Gradle Dökümantasyonu](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF) nu okuyun.

**Şifreler.** `build.gradle` içerisinde, uygulamanın yayın versiyonu için `signingConfigs` kısmını belirlemeniz lazım. Kaçınmanız gerekenler şunlar:

_Bundan Kaçının_. VCS üzerinde görülecektir.

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

Onun yerine, `gradle.properties` dosyası oluşturup VCS'ye eklemeyin:

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```

Bu dosya Gradle otomatik olarak tarafından tanınacaktır, `build.gradle` içinde direk kullanabilirsiniz.

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

**Jar dosyaları import etmek yerine Maven dependency çözümünü kullanın.** Jar dosyalarını projelerinize direk olarak eklerseniz, bunlar `2.1.1` gibi belirli versiyonlarda olacaktır. Bu da jarların güncellenmesi yada versiyon değiştirmesi ile ilgili sıkıntı çıkaracaktır, bu sorun Maven'ın çözdüğü bir sorundur, Android Gradle Build tarafından da önerilmektedir. Örneğin:

```groovy
dependencies {
    implementation 'com.squareup.okhttp:okhttp:2.2.0'
    implementation 'com.squareup.okhttp:okhttp-urlconnection:2.2.0'
}
```    

**Dinamik Maven Dependency çözümlerinden kaçının**
`2.1.+` şeklinde yazılmış dinamik versiyon numaralarından kaçının,farklı versiyon numaralarında dikkate alınmamış değişikliklerden dolayı, düzgün çalışmayan veya ince hatalar bulunan uygulamalar geliştirmiş olursunuz. `2.1.1` gibi statik versiyonlar kullanmak daha stabil, düzgün çalışan ve hataları kolayca belirlenebilen uygulamalar ortaya çıkarmanızı sağlar.

**Release olmamış projeler için farklı paket isimleri kullanın**
 `applicationIdSuffix` ile *debug* [build type](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Build-Types) seçeneklerini *debug* ve *release* modudnaki apk apkları aynı cihaz üzerine yükleyip test edebilmek için yazarız. Bu uygulamamız yayınlandıktan sonraki süreçte dahada büyük önem kazanacaktır.

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

Farklı versiyon tipleri için farklı icon'lar kullanmanız sizin yararınıza olacaktır. Gradle bu işlemleri kolaylaştırmıştır: verilen proje yapısında, *debug* iconunu `app/src/debug/res` içerisine ve *release* iconunu `app/src/release/res` koyun. Aynı zamanda [Uygulama adı değiştirip](http://stackoverflow.com/questions/24785270/how-to-change-app-name-per-gradle-build-type) `versionName` üzerinden bunu gerçekleştirebilirsiniz.

### IDEler ve Text Editörler

**Proje yapısını düzgün takip edebileceğiniz herhangi bir text editörünü kullanabilirsiniz** Editörler kişisel tercihler üzerine değişebilir ancak proje yapısını düzgün takip edebileceğiniz bir çevre yaratmalısınız.

Şu anda en çok tavsiye edilen IDE [Android Studio'dur](https://developer.android.com/sdk/installing/studio.html). Bunun, Google tarafından geliştiriliyor olması, Gradle kullanıyor olması, yeni proje yapısı hazır halde gelmesi, Android Development için özel üretilmiş olması ve artık stabil olması gibi pek çok sebebi vardır.

Android geliştiriciliğinde [Eclipse ADT](http://developer.android.com/tools/help/adt.html) kullanımı artık tavsiye edilmemektedir. [Google ADT desteiğini 2015 sonunda bitirdi](http://android-developers.blogspot.fi/2015/06/an-update-on-eclipse-android-developer.html) ve kullanıcılarını [Android Studio'ya geçiş](http://developer.android.com/sdk/installing/migrate.html) yapmalarını önerdi. Hala Eclipse kullanmaya devam edebilirsiniz ancak, ancak eski proje yapısını ve ANT kullanımını desteklediği için, Gradle çalışması için düzenlemeler yapmanız veya komut satırı üzerinden çalıştırmanız gerekmektedir.

Vim, Sublime Text veya Emacs gibi Text Editörleri de kullanabilirsiniz. Bu durumda Gradle ve adb'yi komut satırından çalıştırmanız beklenmektedir.

Hangi editörü kullanırsanız kullanın Gradle ve proje yapısının önerilen şekilde olduğundan emin olun, ve editörünüze özgü dosyaları VCS üzerine eklemediğinize emin olun. Örneğin, Ant üzerinde `build.xml` dosyasını eklemediğinize emin olun. Özellikle `build.gradle` dosyasını güncel tutmayı ve çalışır halde olduğundan emin olmayı unutmayın. Her zaman diğer developerları düşünün ve onların sizin için projelerini toparlamak zorunda olmadıklarını hatırlayın.

### Kütüphaneler

**[Jackson](http://wiki.fasterxml.com/JacksonHome)** Objeleri JSON'a çeviren ve JSON'ları da objeye çeviren bir Java kütüphanesidir. [Gson](https://code.google.com/p/google-gson/) kütüphaneside bu durumda en çok kullanılan kütüphanedir, ancak biz Jackson'ı JSON işlemede alternatif seçenekler sunduğu için tercih ediyoruz: streaming, in-memory tree model, ve klasik JSON-POJO data binding. Ama unutmamanız gereken şey Jackson GSON'dan daha büyük bir kütüphanedir ve 65k method limitine uymak için bazen duruma göre değişiklik yapmanız gerekebilir. Diğer alternatifler: [Json-smart](https://code.google.com/p/json-smart/) ve [Boon JSON](https://github.com/RichardHightower/boon/wiki/Boon-JSON-in-five-minutes)

<a name="networklibs"></a>
**Networking, caching, ve resimler.** Backend serverlara istek atmada kendini ispatlamış sizinde kendi projenizin client-side'ınde kullanabileceğiniz pek çok kütüphane vardır. [Volley](https://android.googlesource.com/platform/frameworks/volley) yada [Retrofit](http://square.github.io/retrofit/)'i kullanabilirsiniz'.Volley aynı zamanda resim yükleme ve cachede tutmaya yardımcı olmaktadır. Retrofiti seçerseniz, resim yüklemek için [Picasso](http://square.github.io/picasso/)yu, ve etkili HTTP requestler için [OkHttp](http://square.github.io/okhttp/) kullanabilirsiniz. Üç kütüphanede (Retrofit, Picasso and OkHttp) aynı şirketin ürünüdür ve birbirleriyle verimli çalışmaktadırlar. [OkHttp aynı zamanda Volley ilede kullanılabilmektedir.](http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835).

**RxJava** reaktif programlama için kullanılan bir kütüphanedir, başka bir deyişle ise, senkronize olmayan işlemleri gerçekleştirir. Bu çok güçlü ve pek çok şey vaad eden bir paradigmadır, Çok farklı olduğu için kafa karıştırıcı da olabilir.Tüm uygulamanızı bu kütüphaneye göre yapmadan önce herşeyi dikkatlice göz önüne almanızı öneriyoruz. RxJava ile yapılmış bazı projeler var, eğer yardıma ihtiyacıbız olursa bu konuyla ilgili konuşabileceğiniz insanlar: Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen. Bu konuyla ilgili bizimde yazdığımız blog gönderileri: [[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android), [[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754), [[4]](http://blog.futurice.com/android-development-has-its-own-swift).

Rx programlama ile ilgili herhangi bir tecrübeniz yoksa öncelikle ,API'den gelen cevaplara uygulayarak başlayın. Alternatif olaraksa, Basit UI işlemlerinde uygulayabilirsiniz , click yada bir text kısmına yapılan yazıları dinlerken gibi. Eğer Rx yeteneklerinize güveniyorsanız ve tüm mimariye bunu uygulamak istiyorsanız,O zaman her kısma Javadocs ekleyin. Unutmamanız gereken önemli noktaysa ileride projede çalışacak Rx programlama ile ilgili tecrübesiz olabilir, bu yüzden kodunuzu mümkün olduğunca anlaşılır tutun.

**[Retrolambda](https://github.com/evant/gradle-retrolambda)**, Android ve JDK-8 öncesi projelerde Lambda syntax'ını kullanmamızı sağlayan kütüphanedir. Kodunuzu temiz ve düzenli tutar, özellikle RxJava ile çalışırken işinizi çok kolaylaştırır. Kullanmak için, JDK8 yükleyin, SDK yerini nasıl belirlemenizi istediysek aynı şekilde jdk yerini belirleyin, `JAVA8_HOME` ve `JAVA7_HOME` sistem değişkenlerini ayarlayın, build.gradle üzerinde artık çağırabilirsiniz:

```groovy
dependencies {
    classpath 'me.tatarka:gradle-retrolambda:2.4.1'
}
```

Gradle'ın her bir modülüne belirtilen değeri yazın:

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

Android Studio Java8 lambdalar için destek sağlar. Lambda'lar hakkında bilginiz yoksa, Sıradakileri takip edin:

- Bir method barındıran interface'ler "lambda dostu" olarak anılır ve daha kısa syntax içerisine alınabilir.
- Parametreler ve benzeri hakkında ikileme düşerseniz, anonim bir inner class yazın ve Android Studio onu sizin için lambda formatına çevirsin.

<a name="methodlimitation"></a>
**Dex method sınırlamasına dikkat edin ve çok fazla kütüphane kullanmaktan kaçının.** Android uygulamalar, dex dosyası olarak sıkıştırıldıklarında, 65536 adet referans edilebilecek metod sınırlaması vardır.  [[1]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [[2]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [[3]](http://jakewharton.com/play-services-is-a-monolith/). Eğer sınır aşarsınız hata aldığınızı göreceksiniz. Bu sebeple, düşük sayıda kütüphaneler kullanın, [dex-method-counts](https://github.com/mihaip/dex-method-counts) aracını kullanarak hangi kütüphanelerle bu limitin altında kalacağınızı hesaplayabilirsiniz. Özellikle Guava kullanımından sakının.İçeriğinde 13k metod barındırmaktadır.

### Activity'ler ve Fragment'lar

Android developerlar ve Futurice developerları arasında dahi Android Uygulamada Activity ve Fragment kullanarak en iyi yazılım mimarisinin nasıl yapılacağı hakkında bir fikir birliği yok. Square bile [Çoğunlukla View'lar aracılığıyla Mimari oluşturma](https://github.com/square/mortar),Fragmentlara olan ihtiyacı ortadan kaldırmıştır, ama community arasında halen kabul edilebilir bir işlem olarak görülmemektedir.

Android API'nin tarihi itibariyle, Fragmentları UI kısmının başında olarak görebilirsiniz. Diğer bir deyişle, Fragmentlar çoğunlukla UI'a bağlıdırlar. Activityler ise yönetmek ve lifecycle'dan dolayı controller görevi görmektedir. Ancak, Bu roller arasında çeşitlilikte de gözlenebilir: Activityler UI görevi alabilir([Ekranlar arası değişiklik yapmak](https://developer.android.com/about/versions/lollipop.html)), ve [Fragmentları controller olarak kullanmak](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI). Her bir yaklaşımın(sadece-fragment mimarisi, veya sadece-activity,veya sadece-view) kendince sakıncaları olduğu için dikkatli karar vermenizi öneriyoruz.Dikkat etmeniz gereken birkaç şey aşağıda sıralanıyor, işinize yarayan kısımları kullanmaya dikkat edin.

- [İçiçe Fragment](https://developer.android.com/about/versions/android-4.2.html#NestedFragments)ları sıkça kullanmamaya çalışın, çünkü [matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/) oluşabilir. Sadece mantıklı yerlerde içiçe fragment kullanın (örneğin, Fragment içerisindeki Viewpager'ın içerisinde bulunan fragmentlar) veya gerçekten düşünülmüş bir senaryoysada kullanabilirsiniz.
- Activityler içerisine çokça kod koymaktan çekinin. Mümkün olduğunca hafif kodlar tutun, uygulamanızın lifecycle ve Android-interfacing API'leri ile çoğunlukla ön plana çıkarılmalıdır. Düz activityler yerine tek fragment içeren activityler tercih edilmeli ve UI kodları fragment içerisine yerleştirilmelidir. Bu bir modifikasyonda veya taba eklemek,çoklu fragment ekranına geçmek gibi genel değişiklikte bize kolaylık sağlar. Mümkün olduğunca her activitynize karşılık bir fragment bulundurmaya çalışın, aksi durumda dikkatlice herşeyi ayarladığınıza emin olun.
- Android işletim seviyesinde işlem yapmamaya dikkat edin.Bu seviyeyle ilgili yaşayabileceğiniz en büyük sıkıntıları Intent kullanımı sırasında yaşayabilirsiniz. Bu kullandığınız intentler, Android işletim sistemini veya diğer uygulamaları etkileyebilir ve buglara sebep olabilir. Örneğin, eğer uygulamanızda paketler arası haberleşmede intent kullanıyorsanız, cihazınız açıldıktan sonra bu haberleşmede bir kaç saniyelik gecikme yaşayabilirsiniz.

### Java Paket Mimarisi

Java Android mimarisi kabaca [Model-View-Controller](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) üzerinden açıklanabilir. Android'de, [Fragment ve Activity controller kısımlarıdır](http://www.informit.com/articles/article.aspx?p=2126865). Öte yandan, kullanıcı arayüzünün arayüzleridir,dolayısıyla UI kısımlarıdır.

Bu sebeple, fragment veya activityleri view yada controller sınıfına direk koymak yanlıştır. Bu yüzden onları `fragments` paketinde tutmak daha mantıklıdır. Önceki kısımdaki yönergeleri takip ettiğiniz takdirde activitynizi en tepede tutmakta sakınca yoktur. Eğer 2,3 veya daha fazla activity kullanmak istiyorsanız activitylerinizi `activities` paketi içine koyabilirsiniz.

Yoksa, mimari klasik bir MVC gibi görünebilir, API cevaplarından gelen JSON'lardan elde edilen POJO'ların tutulduğu `models`paketi, custom-viewlarınızı tuttugunuz `views` paketi, bildirimler, action bar viewları, widgetlar vs. Adapterlar ara birimlerdir, data ve viewlar arasında bulunur. Ancak, çoğunlukla `getView()` ile viewları çağırdıkları için `adapters` alt paketini `views` içerisine koyabilirsiniz.

Bazı controller sınıfları tüm uygulamayı kapsar ve Android İşletim Sistemi seviyesine yakındır. Bunlar `managers` packeti içerisinde bulunabilir. Çeşitli data process sınıfları, "DateUtils" gibi, `utils` paketi içerisinde yer almaktadır. Backend ile iletişim içerisindeki sınıfları ise `network` paketi içerisine koyabilirsiniz.

Backende en yakından Kullanıcıya en yakın olarak sıraladığımızda hepsi böyle bir görünüm alır:

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

### Resource Dosyaları

**İsimlendirme** İsimlendirme yaparken `type_foo_bar.xml` kalıbındaki gibi, tipini öncesine yazın. Örneğin: `fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`.

**Layout XML'leri Düzenlemek** XML dosyalarını doğru bir şekilde nasıl oluşturacağınızı bilmiyorsanız, Bunları takip edin:

- Her bir satıra bir özellik gelecek ve 4 boşluk karakteri kadar boşluk bırakılacak şekilde yazın.
- `android:id` her zaman ilk özelliğiniz olmalı
- `android:layout_****` özellikleri her zaman yukarıda olmalı
- `style` özelliği en aşağıda tutulmalı
- Tag kapatma simgesini `/>` ayrı bir satıra yazmanız yeni bir özellik eklerken veya düzenleme yaparken yararınıza olacaktır.
- `android:text` kullanmak yerine,[Designtime attributes](http://tools.android.com/tips/layout-designtime-attributes) seçeneğini kullanmayı deneyin.Android studio desteği bulunmaktadır.

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

Ana kural olarak `android:layout_****` ile paylaşan kavramlar XML içerisinde tanımlanmalıyken, `android:****` ile başlayan diğer özellikler style etiketi altında ayrı bir XML'de belirtirlmelidir. Bu kuralın istisna durumları vardır,ancak genelde böyle yapılması yararlıdır. Ana fikir her zaman (pozisyon,büyüklük,kenarlara olan uzaklıklar vs.) ve içerik özelliklerini xml içinde tutarken, Style özelliklerini (renk,dosya adı,text boyutu vs.) style XML'leri içerisinde tutmaktır.

İstisnalar ise:

- `android:id` kesinlikle layout XML dosyasında olmalıdır
- `LinearLayout` özelliği olan `android:orientation` ın layout XML içinde bulunması daha mantıklıdır
- `android:text` içerik belirlediği için layout dosyasında olmalıdır
- `android:layout_width` ve `android:layout_height` kullanarak bazen farklı bir style yapabilirsiniz ancak önceden dosyayı oluşturduğunuzda, layout XML içerisinde bu özellikler gelecektir.

<a name="styles"></a>
**Style kullanın.** Nerdeyse bütün projelerimizde style kullanmamız mantıklı olan seçenektir,çünkü bir view'ın pek çok kez aynı özelliklerle karşımıza gelme olasılığı yüksektir. Örneğin çok kullanılan bir Text için ortak bir style belirleyip kod yükünüzü aşağıdaki gibi hafifletebilirsiniz:

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

Bunu buttonlar içinde yapmanız gerekebilecektir. Ama bununla yetinmeyin,`android:****` özelliklerin hepsini style'a taşıyın.

<a name="splitstyles"></a>
**Büyük bir style dosyasını küçük dosyalara bölün.** Kendinizi `styles.xml` dosyası ile sınırlamak zorunda değilsiniz. Android SDK için `styles` kelimesinden ziyade, XML dosyasısın `<style>` etiketini içernesi daha önemlidir. Bu sebeple `styles.xml`, `styles_home.xml`, `styles_item_details.xml`, `styles_forms.xml` gibi pek çok dosya oluşturabilirsiniz. res dosyası içerisindeki işletim sistemine anlam ifade eden kelimeler varken, `res/values` içerisindeki dosyalar değişkenlik gösterebilir.

<a name="colorsxml"></a>
**`colors.xml` sizin renk paletiniz.** `colors.xml` dosyanızda bir rengin adını gösteren RGBA değerinden başka birşey olmamalıdır. Buttonların farklı özellikleri için farklı RGBA kullanmak gibi girişimler yapmayın.

*Bunu yapmayın:*

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

Burada aynı RGBA değerini farklı yerlerde kullanmanız mümkündür ve basit bir renk değişikliğinde pek çok dosyada değişiklik yapmanız gerekecektir. Aynı zamanda, bu açıklamalar style kısmına ait olmalıdır `colors.xml` içerisinde tutulmamalıdır.

Onun yerine, bunu yapın:

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

Bu renk paletini uygulamanızın tasarımcısından isteyin. Renklerin isimleri illede "beyaz","mavi" gibi renk isimleri olmak zorunda değildir. "anarenk", "ikincilrenk", "negatifdegeri" gibi isimlerde kabul edilebilir. Renkleri böyle belirlemek değişiklik yapmamızı kolaylaştırırken, kullandığımız renk sayısınada hakim olabiliriz. Estetik bir UI için renk varyasyonları düşürmek önemlidir.
<a name="dimensxml"></a>
**dimens.xml dosyasını da colors.xml gibi düzenleyin.** Renklerdeki aynı amaçlar doğrultusunda Text büyüklüğü ve kenar boşlukları içinde bir "palet" oluşturmak doğru olandır. Dimen dosyasına güzel bir örnek şekildeki gibidir:

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

`spacing_****`etiketi layoutlarda boşluk bırakma için kullanılmaktadır, margin veya paddinglerde, direk kod içerisinde veri yazmak yerine, normal string oluşturmuşuz gibi düşünülebilir. Bu bize daha düzenli, düzenlenmesi kolay ve anlaması kolay bir kod ortamı sunmaktadır.

**strings.xml**
Stringleri etiketlerken içerikle yakın olacak şekilde anahtar kelimeler seçmeyi tercih edin, aynı stringi birden çok etiket için kullanmaktan çekinmeyin. Diller karmaşık kavramlardır, bu etiketlemenin düzgün yapılması mantık kurulmasını kolaylaştırıp belirsizliği ortadan kaldıracaktır.

**Kötü**
```xml
<string name="network_error">Network error</string>
<string name="call_failed">Call failed</string>
<string name="map_failed">Map loading failed</string>
```

**İyi**
```xml
<string name="error.message.network">Network error</string>
<string name="error.message.call">Call failed</string>
<string name="error.message.map">Map loading failed</string>
```

String değerlerinin hepsini büyük harflerle yazmayın, normal kullanımdaki gibi devam edin(ör., İlk harf büyük harfle başlanabilir.). Tüm texti büyük harflerle göstermeniz gerekiyorsa bunu TextView özelliği olan [`textAllCaps`](http://developer.android.com/reference/android/widget/TextView.html#attr_android:textAllCaps) ile yapabilirsiniz.

**Kötü**
```xml
<string name="error.message.call">CALL FAILED</string>
```

**İyi**
```xml
<string name="error.message.call">Call failed</string>
```

<a name="deephierarchy"></a>
**Viewlar arasında içiçe çoklu hiyerarşiden kaçının.** Bazen UI'da düzenleme yapıp istediğinizi elde etmek için, bir tane daha LinearLayout eklemek size cazip gelebilir. Bu tarz şeyler şunlara sebep olabilir:

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

Bunu özel olarak layout dosyasında kullanmasanız bile, Java kodunuz üzerinden yazarken view oluşturduğunuzda buna benzer durumu oluşturabilirsiniz.

Bununla ilgili birkaç problem oluşabilir. performans problemleriyle karşılabilirsiniz, çünkü ele alınması gereken pek çok UI elemanı bulunmaktadır. Öne çıkacak başka hataysa bu [StackOverflowError](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views) dur.

Bu yüzden, view'lerinizi mümkün olduğunca basit tutun: [RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html) kullanımını öğrenin, [Layoutlarınızı ayarlamak](http://developer.android.com/training/improving-layouts/optimizing-layout.html) nasıl olur onu öğrenin ve [`<merge>` tag](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts) kullanımını araştırın.

<a name="webviews"></a>
**WebView'ler ile alakalı sorunları dikkate alın.** Bir makale ile ilgili sayfa göstermek ve benzeri durumlarda bir web sayfası göstermek istediğinizde, cilient-side tarafında mümkün olduğunca işlem yapmaktan çekinin, backend programcılarınızdan "*saf*" HTML dosyası almayı talep edin. ApplicationContext'e bağlı olmak yerine Activity üzerinden bir referans alındıysa [WebView'da Memory Leak](http://stackoverflow.com/questions/3130654/memory-leak-in-webview) oluşabilir. Basit butonlar ve form yapıları için websayfası kullanmayın, native çalışmayı tercih edin.


### Test frameworkleri

Android SDK test frameworkleri, özellikle UI testleri, henüz daha başlangıç aşamasındadır. Android Gradle [`connectedAndroidTest`](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing) adını verdikleri bir test materyali üzerinde çalışmaktadırlar.Bu materiyal [Android için JUnit eklentileri ve yardımcıları](http://developer.android.com/reference/android/test/package-summary.html) üzerinde çalışmaktadır. Bunun için bir cihaz veya emülatör üzerinden testlerinizi sürdürmelisiniz. Test için resmi kaynaklar şunlardır: [[1]](http://developer.android.com/tools/testing/testing_android.html) [[2]](http://developer.android.com/tools/testing/activity_test.html).

**Unit test için [Robolectric](http://robolectric.org/) kullanın** Robolectric "cihazdan bağımsız olarak" Unit ve UI testlerde kullanabileceğiniz bir test frameworküdür. Ancak, Robolectric üzerinde UI test yapmak doğru sonuçlar vermeyecektir. UI elemanları arasında animasyonlarda ve benzeri durumlarda gözlem yapamadığınız için verimli bir test alamayacaksınız.

**[Robotium](https://code.google.com/p/robotium/) UI testleri kolaylaştırır.** Robotium ile birbirinden bağımsız UI testler yazabilirsiniz. Ancak birbirine bağlı testler yazmanız uygulamanızın akışını kontrol etmeniz açısından size daha yararlıdır. Test kodları örnekteki kadar kolaydır.:

```java
solo.sendKey(Solo.MENU);
solo.clickOnText("More"); // searches for the first occurrence of "More" and clicks on it
solo.clickOnText("Preferences");
solo.clickOnText("Edit File Extensions");
Assert.assertTrue(solo.searchText("rtf"));
```

### Emulatorler

Android geliştiricilik mesleğiniz ise [Genymotion emulator](http://www.genymotion.com/) için lisans alın. Genymotion emulatorler AVD emülatörlerden daha hızlı frame/sec hizmeti sağlarlar. İçerisinde uygulamanızı test etmek için, network bağlantı kalitesini test etmek için, GPS pozisyonları ve buna benzer pekçok durum için araç bulundurmaktadır. Aynı zamanda uygulamayı toplu test etme durumlarında yararlıdır. Pek çok cihaz üzerinde farklı android versiyonları üzerinde test yapabilirsiniz, dolayısıyla Genymotion lisansı pek çok cihaz almaktan çok daha mantıklıdır.

Uyarılar: Genymotion Play Store ve Maps gibi Play Store Servislerini içermez. Ayrıca Samsunga özgü API'leri test etmek için Samsung cihaz kullanmanız önerilir.

### Proguard Ayarları

[ProGuard](http://proguard.sourceforge.net/) Android projelerini küçültmek ve kodları karartmak için kullanılır.

ProGuard kullanıp kullanmamanız proje ayarlarına bağlıdır. Genellikle uygulama yayınlarken Gradle dosyanızı ProGuard kullanmaya ayarlamanız mantıklı olandır.

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

Hangi kodun gözardı edileceğini, hangisinin korunacağını ve hangisinin karartılacağını ayarlamak için, kodunuz içerisinde birden çok giriş noktası ayarlamanız gerekmektedir. Bu giriş noktaları genelde main methodu olan sınıflar, appletler, midletler, activityler,ve benzeridir.
Android frameworkü proguard ayarlarını `SDK_HOME/tools/proguard/proguard-android.txt` adresindeki txt'den alır. Yukarıdaki ayarlarla,projeye özgü,belirli ProGuard kuralları, `my-project/app/proguard-rules.pro`, adresinde belirtildiği üzere varsayılan ayarlar olarak uygulanacaktır.

`assembleRelease` gibi Proguard komutları başarılı çalıştığı halde,ProGuard'la ilgili en belirgin hatalar `ClassNotFoundException` veya `NoSuchFieldException` ve benzerleri ortaya çıkabilir.
Bu iki anlama gelebilir:

1. ProGuard bulunmayan dosyayı gerekli bulmayarak silmiş olabilir.
2. ProGuard Java adını karartmış olabilir ve bu Java sınıfına başka bir yerde direk ismiyle ulaşılıyor olabilir.

`app/build/outputs/proguard/release/usage.txt` üzerinden objenin kaldırılıp kaldırılmadığını kontrol edin.
`app/build/outputs/proguard/release/mapping.txt` üzerinden objenin karartılıp karartılmadığını kontrol edin.

ProGuard'ın gerekli dosyalardan ve methodlardan *kurtulmasını* engellemek için, ProGuard ayarlarına `keep` ile özel durumu ekleyin:
```
-keep class com.futurice.project.MyClass { *; }
```

ProGuard'ın gerekli dosyaları ve methodları *karartmasını* engellemek için, ProGuard ayarlarına `keepnames` ile özel durumu ekleyin:

```
-keepnames class com.futurice.project.MyClass { *; }
```

Dahası için [Proguard](http://proguard.sourceforge.net/#manual/examples.html).

**Projelerinizin erken aşamalarında yayınlamak için APK oluşturun** Böylece ProGuard'ın size gerekli dosyaları tutup tutmadığını kontrol edebilirsiniz.Yeni bir kütüphane eklediğiniz zamanda aynı şekilde, bir tane yayına göndermek üzere APK hazırlayıp, cihazlarda deneyin. Uygulamanızın yayına tamamen hazır olduğunu düşündüğünüzde APK oluşturmak yerine bu şekilde durmadan APK oluşturmak daha yararlıdır. Böylece hatayı erkenden keşfedip önüne geçebilirsiniz.

**Öneri.** `mapping.txt` dosyanızı kullanıcıya yolladığınız her bir güncellemede tutun. Bu şekilde kullanıcıdan gelecek hatada veya size gelen hata raporunda uygulamanızdaki hatayı daha çabuk bulabilirsiniz.

**DexGuard**. Kodunuzu karartmak için gerçekten güçlü bir araca ihtiyacınız varsa,[DexGuard](http://www.saikoa.com/dexguard)'ı araştırın.DexGuard ticari amaçla ProGuard'ı yapan ekip tarafından yapılmış, ProGuard'a ek olarak 65k limit metodunu dex üzerinden çözmenizede yaran bir araçtır.

### Veri Depolama


#### SharedPreferences

Uygulamanız tek bir context üzerinde çalışıyor ve karmaşık veriler içermiyorsa varsayılan olarak SharedPreferences kullanmak en mantıklısıdır.

SharedPreferences kullanmamak için düşünebileceğiniz seçenekler ise şunlardır:

* *Performans*: Verilerinizin karmaşık veya çok fazlaysa
* *Farklı işlemlerin istekleri* :Farklı pek çok işlem veriye ulaşmaya çalışıyorsa


#### ContentProviders

SharedPreferences yetmediği durumlarda, platform standardı olan ContentProviders'ı kullanabiliriz.ContentProviders daha hızlıdır ve işlemleri yapmada daha güvenilirdir.

ContentProviders kullanıma hazırlamak için yazmamız gereken kod miktarının uzun olması, hele kötü tutoriallar ile daha uzamasıdır. Ancak ContentProviderları [Schematic](https://github.com/SimonVT/schematic) gibi kütüphanelerle kullanıp büyük bir efordan kar edebiliriz.

Halen SQLite üzerinden verileri okumak üzerine kod yazmanız gerekmektedir. Gson ve benzeri kütüphanelerle sadece ihtiyacınız olan verileri alabilirsiniz.Bu şekilde performans olarak düşüş olacaktır ancak veriler için teker teker sütün ayarlamak zorunda kalmayacaksınız.


#### ORM Kullanımı

Genellikle kesin bir ihtiyacınız yoksa veya kalabalık bir veri topluluğunuz yoksa ORM kullanımını tavsiye etmiyoruz. Biraz karmaşık ve öğrenmesi zaman almaktadır. Eğer uygulamanızda ORM kullanma kararı verdiyseniz _process safe_ olup olmamasını kontrol edin, çoğu ORM çözümü  _process safe_ değildir.


### Stetho Kullanın

[Stetho](http://facebook.github.io/stetho/) Facebook tarafından geliştirilmiş Chrome Developer Araçlarınıda kullanan, Android uygulumalarda kullanım için yapılmış bir debug bridge'dir. Stetho ile uygulamanızı kolayca inceleyebilirsiniz, özellikle network trafiğini takip etmede faydalıdır. Uygulamanızdaki SharedPreferences ve SQLite verilerini takip etmede size yardımcıdır. Ancak bunları yaparken Stethonun sadece debug modunda açık olduğuna emin olun.

### Teşekkür

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton ve diğer Futurice geliştiricilere bilgilerini paylaştıkları için teşekkürler.

### Lisans

[Futurice Oy](http://www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)
