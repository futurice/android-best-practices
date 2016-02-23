# Передовой опыт разработки для Android

Уроки от лучших Android-разработчиков из команды Futurice. Не нужно изобретать велосипед, просто следуйте правилам в руководстве. Если вы интересуетесь разработкой для IOS или Windows Phone, обратите внимание на [**iOS Good Practices**](https://github.com/futurice/ios-good-practices) и [**Windows App Development Best Practices**](https://github.com/futurice/windows-app-development-best-practices).

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--best--practices-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1091)

## Cодержание

#### Используйте Gradle и рекомендованную им структуру проекта
#### Храните пароли и важные данные в файле gradle.properties
#### Не пишите свой HTTP клиент, используйте библиотеки Volley или OkHttp
#### Используйте библиотеку Jackson для парсинга JSON данных
#### Не нужно использовать Guava и большое количество библиотек чтобы не привысить лимит методов (65k)
#### Используйте фрагменты для отображения пользовательского интерфейса
#### Используйте activity только для управления фрагментами
#### XML-разметка — это код, который нужно писать аккуратно
#### Чтобы не дублировать атрибуты в XML-разметке, используйте стили
#### Используйте несколько файлов со стилями, не нужно создавать один огромный файл
#### Сделайте файл colors.xml компактным. Не дублируйте цвета, задайте основную палитру
#### То же касается и dimens.xml, задайте только основные константы
#### Избегайте слишком глубокой иерархии элементов ViewGroup
#### Не нужно обрабатывать WebView на клиентской стороне, будьте внимательны к возможным утечкам
#### Используйте Robolectric для unit-тестов, а Robotium для UI-тестов
#### Используйте Genymotion в качестве эмулятора
#### Всегда используйте ProGuard или DexGuard
#### Для простого хранения данных используйте SharedPreferences, для сложной структуры данных - ContentProvider


----------

### Android SDK

Разместите [Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools) в домашней дирректории или другом месте, не связанном с приложением. Некоторые IDE включают в себя SDK при установке и могут размещать его в своей дирректории. Это не совсем правильно, так как в дальнейшем вам может понадобиться обновить, переустановить или сменить IDE. Не размещайте SDK в системные папки, так как для доступа к ним может понадобиться ипользование команды sudo, если вы вошли в систему как обычный пользователь. 


### Система сборки

Используйте [Gradle](http://tools.android.com/tech-docs/new-build-system) по умолчанию. У Ant меньше возможностей и его код менее компактный. Используя Gradle, вы сможете:

- Создавать различные сборки и варианты приложения
- Создавать простые задачи в виде скрипта
- Управлять и загружать зависимости
- Настраивать хранилище ключей
- И многое другое

Плагин Gradle находится в активной разработке командой Google и уже стал основной системой сборки для Android


### Project structure

Существует два варианта структуры проекта: старый Ant + Eclipse ADT и новый Gradle + Android Studio. Лучше использовать второй вариант. Если ваш проект все еще использует старую структуру, рекомендуем портировать проект.

Старая структура:

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

Новая структура:

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

Главным отличием является то, что новая структура явно разделяет 'наборы ресурсов' (`main`, `androidTest`), используя одну из концепций Gradle. Например, вы можете добавить папки 'paid' и 'free' в вашу папку `src`, в которых будет отдельный исходный код для платной и бесплатной версий приложения.

Наличие папки `app` в верхнем уровне иерархии помогает отделить ваше приложение от библиотек (например, `library-foobar`), которые в нем используются. В таком случае файл `settings.gradle` хранит проекты, на которые может ссылаться `app/build.gradle`.


### Конфигурация Gradle

**Общая структура.** Следуйте [Рекомендациям Google для Gradle for Android](http://tools.android.com/tech-docs/new-build-system/user-guide)

**Маленькие задачи.** Вместо скриптов на shell, Python, Perl, вы можете создавать задачи в Gradle. Подробне в документации [Документация Gradle](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF).

**Пароли.** В файле `build.gradle` вашего приложения необходимо задать `signingConfigs` для релизной сборки. Here is what you should avoid:

_Не делайте так_. Эта информаци появится в системе контроля версий.

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

Вместо этого, создайте файл `gradle.properties`, который _не_ будет добавлен в систему контроля версий:

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```

Gradle автоматически импортирует этот файл, по этому вы можете использовать его в `build.gradle`:

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

**Старайтесь использовать зависимости Maven вместо импортирования jar-файлов.** Если вы явно включаете jar файл в ваш проект, его версия будет неизменна, например `2.1.1`. Скачивать и обновлять зависимости это непростая задача, которую Maven с легкостью решает, что также приветствуется в сборках Android Gradle. Например:

```groovy
dependencies {
    compile 'com.squareup.okhttp:okhttp:2.2.0'
    compile 'com.squareup.okhttp:okhttp-urlconnection:2.2.0'
}
```    

**Избегайте использования динамических зависимостей Maven**
Не нужно использовать динамические версии, такие как `2.1.+` так как это может привести к нарушению стабильности сборки, а также непредсказуемым различиям между разными сборками. Использование статических версий как  `2.1.1` помогает создать более стабильную и предсказуемую среду разработки.


### Среда разработки (IDE) и текстовые редакторы

**Искользуйте любой редактор, который удобно отображает структуру проекта.** Текстовый редактор это дело вкуса, так или иначе вам нужно будет настроить его в соответствии со структурой проекта и системой сборки.

На данный момент рекомендуемая IDE - [Android Studio](https://developer.android.com/sdk/installing/studio.html), разработанная командой Google, она лучше других сочитается с Gradle, использует новую структуру проекта по умолчанию, уже в стабильной сборке и создана непосредственно для Android разработки.

Вы можете использовать [Eclipse ADT](https://developer.android.com/sdk/installing/index.html?pkg=adt), но вам придется настроить его, так как там используется старая структура проекта и система сборки Ant. Вы даже можеде использовать простые редакторы как Vim, Sublime Text, или Emacs. В этом случае вам понадобится Gradle и команда `adb`. Если у вас не получается интегрировать Gradle в Eclipse, для сборки можно использовать командную строку, или начать использовать Android Studio. Это лучший вариант, учитывая что плагин ADT устарел.

Что бы вы не использовали, главное убедиться что Gradle и структура проекта следуют официальным инструкциям, и не добавлять в систему контроля версий специфические для редактора файлы. Например, не добавляйте файл `build.xml`. Особенно внимательно следите за обновлениями файла `build.gradle` если вы меняете настройки сборки в Ant. Будьте доброжелательными по отношению к другим разработчикам и не заставляйте их использовать непривычные инструменты.


### Библиотеки

**[Jackson](http://wiki.fasterxml.com/JacksonHome)** это Java-библиотека для конвертации объектов в JSON обратно. [Gson](https://code.google.com/p/google-gson/) так же хорошее решение этой задачи, но мы обнаружили что у Jackson выше производительность, так как он поддерживает альтернативные способы обработки JSON: стриминг, модель дерева памяти, и традиционную связку JSON-POJO. Но имейте в виду что Jackson больше чем GSON, по этому, в зависимости от ситуации, вам нужно будет использовать GSON чтобы не привысить лимит в 65k методов. Альтернативные библиотеки: [Json-smart](https://code.google.com/p/json-smart/) и [Boon JSON](https://github.com/RichardHightower/boon/wiki/Boon-JSON-in-five-minutes)

**Сеть, кеширование и изображения.** Существует несколько проверенных временем библиотек для создания запросов к backend-серверам, которые следует использовать вместо создания собственного клиента. Используйте [Volley](https://android.googlesource.com/platform/frameworks/volley) или [Retrofit](http://square.github.io/retrofit/). Volley также поддерживает кеширование и загрузку изображений. Если вы предпочитаете Retrofit, используйте [Picasso](http://square.github.io/picasso/) для загрузки и кеширования картинок, и [OkHttp](http://square.github.io/okhttp/) для эффективных HTTP запросов. Все три библиотеки (Retrofit, Picasso и OkHttp) созданы одной компанией, поэтому они неплохо дополняют друг друга. [OkHttp так же может быть использован в сочитании с Volley](http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835).

**RxJava** - библиотека для Reactive Programming, другими словами, для обработки асинхронных событий. Это очень мощная и многообещающая концепция, которая сначала может смутить своей необычностью. Мы рекомендуем подумать, перед тем как использовать эту библиотеку как фундамент архитектуры вашего приложения. Существуют проекты, созданные с использованием RxJava, и вы можете обратиться за помощью в использовании RxJava к однуму из этих людей: Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen. Мы написали несколько статей на эту тему: 
[[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android), [[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754), [[4]](http://blog.futurice.com/android-development-has-its-own-swift).

Если у вас нет опыта работы с Rx, то начните с использования API. Или вы можете начать с обработки простых событий пользовательского интерфейса, таких как обработка кликов или ввод текста. Если вы уверены в ваших навыках использования Rx и хотите использовать его во всей архитектуре приложения, напишите Javadocs о самых сложных моментах. Помните, что у программиста, не имеющего опыта  виспользовании RxJava, могут быть большие проблемы при работе с проектом. Помогите понять ваш код и Rx.

**[Retrolambda](https://github.com/evant/gradle-retrolambda)** - Java библиотека fдля использования Lambda-выражений в Android и других платформах с JDK ниже версии 8 . Это поможет сохранить компактность и читабельность кода особенно при использовании функционального стиля, например с RxJava. Для ее использования установите JDK8, выберите его как  SDK в настройках структуры проекта в Android Studio, и задайте переменные `JAVA8_HOME` и `JAVA7_HOME`, затем в корневом файле build.gradle:

```groovy
dependencies {
    classpath 'me.tatarka:gradle-retrolambda:2.4.1'
}
```

и в файлах build.gradle для каждого модуля:

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

Android Studio предлагает поддержку лямбда-синтаксиса Java8. Если у вас нет опыта работы с лямбдами, начните с этого:

- Любой интерфейс с одним методом вполне совместим с лямбда-выражениями и может быть упрощён в их помошью
- Если вам непонятно какие параметры использовать, напишите обычный внутренний класс и позвольте Android Studio преобразовать его в лямбда-выражение.

**Помните о лимите dex-файла на количество методов и избегайте его привышения.** Android приложения, запакованный в  dex файл, не могут привысить лимит в 65536 ссылочных методов [[1]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [[2]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [[3]](http://jakewharton.com/play-services-is-a-monolith/). При компиляции вы увидите соответствующую ошибку. Поэтому используйте ограниченное количество библиотек и утилиту [dex-method-counts](https://github.com/mihaip/dex-method-counts) для определения оптимального набора библиотек, не выходящего за лимит. Особенно избегайте библиотеки Guava, так как она содержит 13k методов.


### Фрагменты и Activities

В сообществе Android-разработчиков (как и в команде Futurice) нет единого мнения по поводу того, как лучше всего построить архитектуру Android-приложения используя фрагменты и activity. Square даже выпустила [библиотеку для построения архитектуры в основном с помощью View](https://github.com/square/mortar), минимизировав тем самым необходимость фрагментов, но этот способ до сих пор не стал общепринятым.

Исходя из истории Android API, фрагменты можно рассматривать как часть пользовательского интерфейса экрана. Другими словами, фрагменты обычно являются частью UI. Activities обычно рассматриваются как контроллеры, которые особенно важны для управления состоянием и жизненным циклом. Однако, может быть иначе: activity могут исполнять функции, связанные с UI ([переходы между экранами](https://developer.android.com/about/versions/lollipop.html)), а  [фрагменты могут быть использованы только как контроллеры](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI). Мы советуем принимать решение, имея в виду, что архитектура, которая строится только на фрагментах, только на activity или только на view, может иметь много недостатков. Вот пара советов по поводу того, на что нужно обратить внимание, но относитесь к этим советам критично:

- Избегайте чрезмерного использования [вложенных фрагментов](https://developer.android.com/about/versions/android-4.2.html#NestedFragments), так как может появится побочный эффект [матрёшки](http://delyan.me/android-s-matryoshka-problem/). Используйте вложенные  фрагменты только тогда, когда в этом есть смысл (например, фрагменты в горизонтальной разметке ViewPager внутри фрагмента во весь экран) или если вы действительно знаете что делаете.
- Избегайте чрезмерного кода в activity. По возможности, используйте их как контейнеры которые отвечают за жизненный цикл и другие важные элементы интерфейса Android API. Вместо того чтобы использовать activity, используйте activity с фрагментом и вынесите код, отвечающий за UI внутрь фрагмента. Это сделает возможным повторное использование фрагмента если вам потребуется поместить его в разметку с во вкладки (tabbed layout), или на экран планшета с несколькими фрагментами. Избегайте создания activity без фрагментов, кроме случаев, когда вы делаете это с определенной целью. 
- Не стоит злоупотреблять Android API, например, полагаясь только на механизм Intent для внутренней работы приложения. Вы можете повлиять на работу операционной системы Android и других приложений, вызвав ошибки или зависания. Например, известно, что если ваше приложение использует механизм Intent для внутренней связи между пакетами приложения, оно может вызвать зависание в несколько секунд, если было открыто сразу после загрузки ОС.


### Архитектура пакетов Java

Архитектура Java для приложения Android похожа на [Model-View-Controller](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller). В Android, [Фрагмент и Activity являются контроллерами](http://www.informit.com/articles/article.aspx?p=2126865). С другой стороны, они также являются частью пользовательского интерфейса, следовательно они представляют собой еще и Views.

По этой причине сложно классифицировать фрагменты (или activities) как только Controller или  View. Лучше поместить их в отдельный пакет `fragments`. Activities могут оставаться в верхнем уровне пакета до тех пор пока вы следуете совету из предыдущей секции. Если вы планируете создать 2 или 3 activities, создайте отдельный пакет `activities`.

Во всем остальном, архитектура выглядит как обычный MVC, с пакетом `models` содержащим объекты POJO, которые будут наполнены информацией при помощи JSON-парсинга ответов, полученных от API, и пакетом `views`,  содержащим кастомные Views, уведомления, action bar, виджеты, и т.д. Адапторы находятся где-то между data и views, связывая их между собой. Однако им нужно экспортировать View с помощью метода `getView()`, по этому можно поместить пакет `adapters` внутрь пакета `views`.

Некоторые Controller-классы используются во всем приложении работая напрямую с системой Android.
Эти классы могут находиться в пакете `managers`. Различные классы для обработки данных, такие как "DateUtils", должны быть в пакете `utils`, а классы, отвечающие за взаимодействие с сетью - в пакете `network`.

Пакеты сортируются в порядке от closest-to-backend до closest-to-the-user:

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


### Ресурсы

**Имена.** Следуйте конвенции и указывайте тип в начале названия, как `тип_файла_имя_файла.xml`. Примеры: `fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`.

**Структура разметки XML.** Если вы не уверены как форматировать XML, вам могут помочь следующие правила:

- Один атрибут на строку с отступом 4 пробела
- `android:id` всегда указан как первый атрибут
- `android:layout_****` указываются следом
- `style` атрибуты указываются внизу
- Закрывающий тег `/>`на отдельной линии, чтобы облегчить добавление и сортировку атрибутов.
- Вместо ввода строк `android:text` вручную, используйте [Designtime атрибуты](http://tools.android.com/tips/layout-designtime-attributes), доступные в Android Studio.

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

Как правило, атрибут `android:layout_****` должен быть указан в XML-файлах разметки,в то время ак атрибуты `android:****` должны находиться в XML-файлах стилей. У этого правила есть исключения, но в целом оно работает. Идея в том чтобы держать разметку (позиционирование, отступы, размеры) и другие атрибуты контента в файах разметки, а все внешние детали элементов (цвета, оформление, шрифты) в файлах стилей.

Исключения:

- `android:id` должен находиться в файлах разметки
- `android:orientation` для `LinearLayout` также более уместен в файлах разметки
- `android:text` тоже размещать в файлах разметки так как он задает сам контент
- Иногда имеет смысл определить `android:layout_width` и `android:layout_height` как стили, но по умолчанию они должны находиться в файлах разметки

**Используйте стили.** Практически каждый проект нуждается в использовании стилей, так как очень часть приходится использовать повторяющиеся элементы. По крайней мере у вас долджен быть отдельный файл стилей для основного текстового контента в приложении, например:

```xml
<style name="ContentText">
    <item name="android:textSize">@dimen/font_normal</item>
    <item name="android:textColor">@color/basic_black</item>
</style>
```

Это применимо и к TextViews:

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/price"
    style="@style/ContentText"
    />
```

Скорее всего вы захотете сделать то же самое и для кнопок, но не нужно на этом останавливаться. Используйте этот принцип для группировки схожих или повторяющихся атрибутов `android:****` в отдельный файл стилей.

**Разделите большой файл стилей на несколько маленьких.** Не нужно держать все стили в одном файле `styles.xml`. Android SDK поддержывает и другие файлы, нет ничего магического в названии файла `styles`, единственное что важно, это тег `<style>` внутри него. Следовательно, вы можете создать файлы `styles.xml`, `styles_home.xml`, `styles_item_details.xml`, `styles_forms.xml` и т.д. В отличие от имен папок с ресурсами, которые несут определенное значение для системы сборки, имена файлов в `res/values` могут быть произвольными.

**Файл `colors.xml` - это палитра цветов.** В файле `colors.xml` не должно быть ничего другого кроме присвоения названиям цветов определенных значений RGBA. Не используйте его чтобы задавать параметры RGBA для разных типов кнопок.

*Неправильный вариант файла colors.xml:*

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

При таком подходе очень легко создать повторные значения RGBA и на много сложнее менять цвета. Кроме того, эти цвета относятся к определённому контенту, как «button» или «comment», поэтому должны быть описаны в стиле кнопки, а не в файле `colors.xml`.

Правильно файл colors.xml:

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

Цветовую палитру определяет дизайнер приложения. Не обязательно называть цвета «green», «blue», и т.д. Названия вроде «brand_primary», «brand_secondary», «brand_negative» вполне приемлемы. Такие названия цветов облегчают их рефакторинг, а также позволяют понять, сколько цветов используется. Для создания красивого UI, важно уменьшить количество используемых цветов, если это возможно.

**Оформляйте dimens.xml как colors.xml.** Вам также потребуется создать что-то вроде "палитры" отступов и размеров, аналогично цветам в файле colors.xml. Пример хорошо оформленного файла dimens.xml:

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

Рекомендуется не писать числовые значения в атрибутах разметки (полях и отступах), а использовать константы вида `spacing_****` (по тому же принципу что и файлы для локализиции строковых значений).
Это делает разметку понятнее и облегчает ее рефакторинг.

**strings.xml**

Используйте ключи в именах строк, как и в именах пакетов — это поможет вам решить проблему с одинаковыми именами и лучше понимать контекст использования строк.

**Плохой пример**
```xml
<string name="network_error">Network error</string>
<string name="call_failed">Call failed</string>
<string name="map_failed">Map loading failed</string>
```

**Хороший пример**
```xml
<string name="error.message.network">Network error</string>
<string name="error.message.call">Call failed</string>
<string name="error.message.map">Map loading failed</string>
```

Не пишите значения строк в верхнем регистре. Придерживайтесь стартных конвенций (например, первая буква - заглавная). Если вам нужно отобразить строку в верхнем регистре, сделайте это используя атрибут [`textAllCaps`](http://developer.android.com/reference/android/widget/TextView.html#attr_android:textAllCaps) внутри элемента TextView.

**Плохой пример**
```xml
<string name="error.message.call">CALL FAILED</string>
```

**Хороший пример**
```xml
<string name="error.message.call">Call failed</string>
```

**Избегайте глубокой иерархии Views.** Иногда возникает соблазн добавить еще одну LinearLayout в разметку, однако чаще всего это приводит к подобным ситуациям: 

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

Иногда можно не увидеть чрезмерную вложенность в файле разметки, так как это может происходить из-да добавления элементов в разметку непосредственно в Java файле.

Из-за этого может возникнуть несколько проблем. В первую очередь проблемы с производительностью приложения, так как процессору необходимо управлять более сложной структурой элементов. Другая еще более серьёзная проблема это [StackOverflowError](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views).

В общем, постарайтесь сделать иерархию разметки наиболее более плоской: научитесь использовать [RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html), как [оптипизировать вашу разметку](http://developer.android.com/training/improving-layouts/optimizing-layout.html) и использовать [`тег <merge>`](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts).

**Остерегайтесь проблем, связанных с WebViews.** Когда вам нужно показать web-страницу, например новостную статью, не исполняйте код для очистки HTML на клиентской стороне, лучше всего попросить backend-программистов дать вам «чистый» HTML. [WebViews могут вызвать утечку памяти](http://stackoverflow.com/questions/3130654/memory-leak-in-webview) когда они ссылаются на Activity, вместо того, чтобы ссылаться на ApplicationContext. Избегайте использования WebView для создания текста или кнопок, для этого есть TextView и Button.


### Фреймворки для тестов

Тестовый фреймвокр Android SDK's только развивается, особенно когда ресь идет о UI тестах. Android Gradle содержит задачу [`connectedAndroidTest`](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing) которая запускает созданные вами тесты JUnit, используя [расширение JUnit с утилитами Android](http://developer.android.com/reference/android/test/package-summary.html). Это означает, что вы можете запускать тесты на присоединенном устройстве или эмуляторе. Следуйте официальным инструкциям по тестированию [[1]](http://developer.android.com/tools/testing/testing_android.html) [[2]](http://developer.android.com/tools/testing/activity_test.html).

**Используйте [Robolectric](http://robolectric.org/) только для Unit-тестов, не для UI-тестов.** Этот фреймворк позволяет запускать тесты без устройства, для увеличения скорости разработки и идеально подходит для unit-тестов моделей данных и view. Однако, UI-тесты Robolectric'а не полные и не точные. У вас возникнут проблемы с тестированем элементов UI, таких как анимации, диалоги, и т.д. Процесс тестирования происходит «с закрытыми глазами» (без возможности видеть тестируемый экран).

**[Robotium](https://code.google.com/p/robotium/) значительно облегчает написание UI-тестов.** Вам не понадобится Robotium connected-тестов, но он будет очено полезен в получении и анализе views за счет большого количества утилит и контроля экрана во время тестов. Тесты выглядят достаточно просто:

```java
solo.sendKey(Solo.MENU);
solo.clickOnText("More"); // searches for the first occurence of "More" and clicks on it
solo.clickOnText("Preferences");
solo.clickOnText("Edit File Extensions");
Assert.assertTrue(solo.searchText("rtf"));
```


### Эмуляторы

Если вы - профессиональный Android разработчик, купите лицензию [эмулятора Genymotion](http://www.genymotion.com/). Genymotion работает быстрее обычных AVD-эмуляторов и с более высоким FPS. Он позволяет записывать демо-видео вашего приложения, эмулирует различное качество соединения, GPS и многое другое. Также он идеален для запуска тестов. У вас появится доступ к многим образам устройств на Android, так что на много дешевле купить Genymotion, чем покупать все эти устройства.

Подводные камни: На эмуляторах Genymotion по умолчанию нет Google Play Store и Google Maps. Вам также могут понадобиться API, специфические для Samsung, по этому необходимо иметь свое устройство Samsung.

### Конфигурация Proguard

[ProGuard](http://proguard.sourceforge.net/) обычно используется в проектах Android для сжатия и шифровки кода.

Использование ProGuard зависит от конфигурации проекта. Обычно ProGuard используют для защиты release-версии проекта.

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

Чтобы выяснить, какой участок кода нуждается в защите, а какой - нет, нужно отметить в коде одну или несколько точек входа. Обычно это классы, содержащие основне методы, апплеты, мидлеты, activities, и т.д. Фреймворк Android по умолчанию использует конфигурацию, которая находится в `SDK_HOME/tools/proguard/proguard-android.txt`. Вы можете задать собственные правила для найтройки ProGuard, просто поместив их в файл `my-project/app/proguard-rules.pro`, которые дополняют конфигурацию по умолчанию.

Основная проблема, связанная с ProGuard — остановка приложения при запуске с ошибками `ClassNotFoundException` или `NoSuchFieldException`, даже если задача для сборки проекта (т.е. `assembleRelease`) отработала без ошибок. Это означает одно из двух:

1. ProGuard удалил класс, enum, метод, поле or аннотацию, посчитав что она не нужна.
2. ProGuard зашифровал (переименовал) класс, enum или имя поля, старое имя которого по прежнему используется (т.е. Java-отражение).

Проверьте `app/build/outputs/proguard/release/usage.txt` чтобы убедиться что удаленный объект нигде не упоминается.
Проверьте `app/build/outputs/proguard/release/mapping.txt` чтобы убедиться что объект не был зашифрован.

Чтобы не допустить *выбрасывание* нужных классов или их членов ProGuard'ом, добавьте опцию `keep` в файл конфигурации ProGuard:
```
-keep class com.futurice.project.MyClass { *; }
```

Чтобы предотвратить *шифрование* классов или их членов, добавьте опцию `keepnames`:
```
-keepnames class com.futurice.project.MyClass { *; }
```

Пример можно увидеть в [этом шаблоне конфигурации ProGuard](https://github.com/futurice/android-best-practices/blob/master/templates/rx-architecture/app/proguard-rules.pro).
Больше примеров [Proguard](http://proguard.sourceforge.net/#manual/examples.html).

**Создайте релиз-сборку на ранней стадии** чтобы проверить правила ProGuard на предмет сохранности кода. Также рекомендуется делать релиз-версию каждый раз когда вы добавляете новую библиотеку и тестировать приложение на устройстве. Не ждите версии "1.0" для выпуска release-версии, так как тогда могут быть неприятности при запуске приложения.

**Совет.** Сохраняйте файл `mapping.txt` для каждой выпущенной версии приложения. Сохраняя копию `mapping.txt` для каждой сборки, вы можете быть уверены в том что сможете отладить код если пользователь столкнется с ошибкой и отправит лог ошибок из зашифрованного кода.

**DexGuard**. Если вам нужно оптимизировать или зашифровать код намного сильнее, используйте [DexGuard](http://www.saikoa.com/dexguard), коммерческий аналог ProGuard. Он также может разделить dex-файл для обхода лимита в 65k методов.

### Хранение данных
Если вам нужно сохранить несколько простых "флагов" и ваше приложение использует один процесс - SharedPreferences будет достаточно. Это хороший выбор по умолчанию.

Есть две причины, по которым вы можете не захотеть использовать SharedPreferences:

* *Производительность*: Вам нужно сохранить сложные данные или их очень много
* *К данным должен быть доступ из нескольких процессов*: Вы используете виджеты или удаленные сервисы, которые работают в отдельных процессах и требуют синхронизировать данные


#### ContentProviders

In the case SharedPreferences is not enough for you, you should use the platform standard ContentProviders, which are fast and process safe.
Используйте стандартные ContentProviders, если вам не достаточно возможностей SharedPreferences. ContentProvider - быстрый и безопасный способ хранения данных.

Единственный недостаток ContentProviders - это объем кода, который нужно написать для их работы, как и отсутствие качественных туториалов. Тем не менее, возможно создать ContentProvider с помошью библиотеки, например [Schematic](https://github.com/SimonVT/schematic), что значительно облегчает задачу.

Вам все еще понадобится написать код для парсинга, чтобы читать и сохранять данные в столбцы базы Sqlite.
Также возможно сериализировать данные, например используя Gson, а хранить только строку результата. В этом случае уменьшится производительность, но с другой стороны больше нет необходимости создавать отдельный столбец для каждого поля с данными.


#### Использование ORM

Мы не советуем использовать библиотеки для Object-Relation-Mapping. Используйте их только если у вас не безумно сложная структура данных и вам это действительно необходимо.
Как правило, ORM библиотеки очень сложные и требуют много времени чтобы научиться их использовать. Если вы решите использовать ORM библиотеку, обратите внимание на то, является ли она _process safe_ (если это необходимо в приложении), так как многие из существующих ORM библиотек почему-то не безопасны.


### Благодарности

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton и другим разработчики из команды Futurice за то, что они поделились своими знаниями в области Android разработки.

### Лицензия

[Futurice Oy](http://www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)
