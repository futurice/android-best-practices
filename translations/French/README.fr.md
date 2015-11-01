# Les bonnes pratiques du développement Android

Leçons apprises par les developpeurs Android de [Futurice](http://www.futurice.com). Ne réinventez plus la roue en suivant ces bonnes pratiques. Si vous êtes intéressé par le développement iOS ou Windows Phone, n'hésitez pas à regarder nos documents [**Bonnes pratiques iOS**](https://github.com/futurice/ios-good-practices) et [**Bonnes pratiques Windows Phone**](https://github.com/futurice/windows-app-development-best-practices).

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--best--practices-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1091)

## Sommaire

#### Utiliser Gradle et sa structure de projet recommandée
#### Mettre les mots de passe et les données sensibles dans le fichier gradle.properties
#### Ne pas écrire son propre client HTTP, utiliser les librairies Volley ou OkHttp
#### Utiliser la librairie Jackson pour parser les données au format JSON
#### Eviter d'utiliser Guava et ne pas trop utiliser de libraries à cause de la *limite des 65k méthodes*
#### Utiliser des fragments pour représenter une interface graphique
#### Utiliser les activités uniquement pour gérer les fragments
#### Les fichiers XMLs sont aussi du code, penser à bien les organiser
#### Utiliser des styles pour éviter des duplicatas d'attributs dans les fichiers XMLs
#### Utiliser plusieurs fichiers de style pour éviter d'en avoir un seul trop gros
#### Garder le fichier colors.xml court et propre, se contenter de définir la palette de couleurs
#### Garder aussi le fichier dimens.xml concis, définir des constantes génériques
#### Ne pas imbriquer trop de ViewGroups les uns dans les autres
#### Eviter le traitement côté client pour les WebViews, et faire attention aux fuites
#### Utiliser Robolectric pour les tests unitaires, Robotium pour les tests graphiques
#### Utiliser Genymotion comme émulateur
#### Toujours utiliser Proguard et DexGuard


----------

### Android SDK

Mettre le [SDK Android](https://developer.android.com/sdk/installing/index.html?pkg=tools) dans votre répertoire utilisateur (home) ou à un autre endroit indépendant des applications. Certains IDEs inclus le SDK à leur installation, et peuvent le placer dans le même dossier que celui de l'IDE. Cela peut être problématique lorsqu'il faut mettre à jour (ou réinstaller) l'IDE, ou lorsque l'on change d'IDE. De plus éviter de mettre le SDK dans un dossier système qui pourrait nécessiter des permissions administrateurs si votre IDE n'est pas lancé en tant qu'administrateur.

### Système de build

Votre option par défaut devrait être [Gradle](http://tools.android.com/tech-docs/new-build-system). Ant est bien plus limité and aussi plus verbeux. Avec Gradle, il est simple de :

- Builder différentes _flavours_ ou variantes de votre application
- Ecrire des scripts pour réaliser des tâches.
- Gérer et télécharger les dépendences
- Customiser les clés de Store
- Et plus

De plus le plugin développé par Google, Android Gradle, a pour but de devenir le nouveau standard de système de build.

### Structure d'un projet

Il a deux options populaires : l'ancienne structure de projet Ant & Eclipse ADT et la nouvelle Gradle & Android Studio. Vous devriez choisir la nouvelle structure de projet. Si votre projet utilise la vieille structure, vous devriez migrer vers la nouvelle.

Ancienne structure:

```
ancienne-structure
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

Nouvelle structure:

```
nouvelle-structure
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

La principale différence est que la nouvelle structure sépare explicitement `source sets` (`main`, `androidTest`), résultant d'un des concepts de Gradle. Vous pourriez par exemple ajouter deux dossiers `payant` `gratuit` dans le dossier `src`qui aura donc le code source de la version payante et de la version gratuite de votre application.

Le fait d'avoir un dossier global `app` permet de distinguer facilement votre application d'autre libraries (ex.: `library-foobar`) qui seraient référencées dans votre application. Le fichier `settings.gradle` garde les références de ces librairies qui pourront ensuite être référencées dans `app/build.gradle`.

### Configuration de Gradle

**Structure générale.** Se référencer à la documentation [Google's guide on Gradle for Android](http://tools.android.com/tech-docs/new-build-system/user-guide)

**Petites tâches.** A la place d'écrire des scripts (shell, Python, Perl, etc), faites des tâches dans Gradle. Se référencer à la documation [Gradle's documentation](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF) pour plus de détails.

**Mots de passe.** Dans le fichier `build.gradle` de votre application, vous aurez besoin de définir `signingConfigs` pour votre Release. Voici ce que vous devez éviter de faire :

_Ne faites pas ça_. Ceci apparaitrait dans le système de contrôle de version (Git par exemple).

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

A la place, faites un fichier `gradle.properties` qui ne devrait _pas_ apparaître dans le système de contrôle de version.

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```

Ce fichier est automatiquement importer par gradle, donc vous pouvez l'utiliser dans `build.gradle` comme ceci:

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

**Préférez la résolution de dépendences Maven plutôt que l'importation de fichiers .jar.** Si vous importez explicitement des fichiers .jars dans votre projet, ceux-ci seront fixés dans une version, par exemple `2.1.1`. Télécharger et s'occuper des mises à jour est un tâche lourde et rébarbative à effectuer, Maven résoud ce problème d'une facçon élégante. Par exemple:

```groovy
dependencies {
    compile 'com.squareup.okhttp:okhttp:2.2.0'
    compile 'com.squareup.okhttp:okhttp-urlconnection:2.2.0'
}
```

**Eviter les résolutions dynamiques de dépendence Maven**
Evitez l'utilisation de versions dynamiques des librairies comme `2.1.+` car cela pourrait mener à des builds de votre application instables ou à des différénces subtiles du comportement de votre application entre vos différents builds. L'utilisation de versions statiques des librairies comme `2.1.1` permet de créer des environnements de développement plus stables et prédictibles.

### IDEs et éditeurs de texte/code

**Vous pouvez utiliser n'importe quel éditeur de code, il doit juste pouvoir respecter la structure du projet.** Utiliser tel ou tel éditeur de texte/code est un choix personnel, et il en va de votre responsabilité de choisir un étideur compatible avec la structure de projet et le système de build.

L'IDE le plus recommendé en ce moment est : [Android Studio](https://developer.android.com/sdk/installing/studio.html), car il est développé par Google, a bien intégré Gradle, et utilise la nouvelle structure de projet par défaut. De plus il est enfin en version stable et est conçu exprès pour le développement Android.

Vous pouvez utiliser [Eclipse ADT](https://developer.android.com/sdk/installing/index.html?pkg=adt) si vous le souhaitez, mais vous devrez le configurer car de base il utilise l'ancienne structure de projet et Ant pour builder votre application. Dans ce cas vous devrez utiliser Gradle et `adb` en ligne de commande. Si l'intégration de Gradle dans Eclipse ne marche pas chez vous, vos options sont d'utiliser les lignes de commande juste pour builder ou bien de migrer vers Android Studio. Cette dernière est la meilleure car le plugin ADT est devenu déprécié récemment.

Peu importe ce que vous utilisez, veuillez vous assurer que vous avez la nouvelle structure de projet, que vous utilisez Gradle et que vous n'introduisez pas de fichiers de configuation spécifique à votre éditeur de code dans le système de contrôle de version. Par exemple évitez d'ajouter un fichier de configuration Ant `build.xml`. Notamment n'oubliez pas de mettre à jour `build.gradle` si vous changez de configuration Ant. Enfin soyez sympas avec les autres développeurs et ne les forcez pas à changer leurs outils de développement ou leurs préférences.

### Librairies

**[Jackson](http://wiki.fasterxml.com/JacksonHome)** est une librairie capable de convertir les Objets en JSON et vice-versa. [Gson](https://code.google.com/p/google-gson/) est une librairie similaire et aussi populaire cependant nous trouvons que Jackson est plus performante car elle supporte différentes façons de traiter le JSON : en streaming, avec une structure d'arbre et le mapping traditionnel JSON-POJO. Garder en tête toutefois que Jackson est une librairie plus conséquente que GSON donc selon votre cas vous serez peut-être amené à choisir GSON pour éviter la limitation des 65k méthodes. Autres alternatives : [Json-smart](https://code.google.com/p/json-smart/) et [Boon JSON](https://github.com/RichardHightower/boon/wiki/Boon-JSON-in-five-minutes)

**Réseaux, cache et images.** Il existe plusieurs solutions pour faire des requètes sur un backend. Vous pouvez utiliser [Volley](https://android.googlesource.com/platform/frameworks/volley) ou [Retrofit](http://square.github.io/retrofit/). Volley apporte aussi des helpers permettant de charger et mettre en cache des images. Si vous choisissez Retrofit, nous vous conseillons d'utiliser [Picasso](http://square.github.io/picasso/) pour charger et mettre en cache les images et [OkHttp](http://square.github.io/okhttp/) pour faire des requètes HTTP performantes. Les trois librairies Retrofit, Picasso et OkHttp ont été créees par la même entreprise donc elles se complètent plutôt bien. [OkHttp can also be used in connection with Volley](http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835).

**RxJava** est une librairie pour faire de la programmation réactive, en d'autres termes, elle permet de gérer des évènements asynchrones. C'est un paradigme puissant et prometteur bien qu'il puisse être déroutant du fait de ses différences. Nous vous recommandons de faire très attention avant d'utiliser cette librairie pour réaliser l'architecture de votre application. Parmi nous projets, certains ont été réalisés avec RxJava. Si vous avez besoin d'aide adressez vous à l'une de ces personnes : Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen. Nous avons écris des articles dessus : [[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android), [[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754), [[4]](http://blog.futurice.com/android-development-has-its-own-swift).

Si vous n'avez aucune expérience antérieure avec Rx, commencez par mettre en oeuvre cette librairie uniquement sur les réponses des API. Une autre alternative serait de l'utiliser pour les évènements simples liés à l'interface graphique comme les clics ou l'ajout de caractères dans un champ de recherche. Si vous êtes confiants à propos de vos compétences en Rx et que vous souhaitez l'appliquer à l'architecture de votre projet, n'oubliez pas d'écrire des Javadocs au niveau de toutes les parties difficiles. Gardez en tête qu'un autre développeur qui ne connait pas RxJava aura probablement beaucoup de mal à maintenir votre projet. Faites de votre mieux pour les aider à comprendre votre code et Rx.

**[Retrolambda](https://github.com/evant/gradle-retrolambda)** est une librairie Java faite pour utiliser la syntaxe Lambda avec Android et d'autre plateformes antérieures au JDK8. Elle vous permet de garder votre code concis et lisible surtout si vous utilisez un style de programmation fonctionnel avec par exemple RxJava. Pour l'utiliser, installez le JDK8, le mettre en tant que SDK par défaut dans votre projet Android Studio et ajoutez les variables d'environnement `JAVA8_HOME` et `JAVA7_HOME`. Enfin ajout à votre build.gradle :

```groovy
dependencies {
    classpath 'me.tatarka:gradle-retrolambda:2.4.1'
}
```

et dans chacun de vos fichiers build.gradle ajoutez :

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

Android Studio propose un support pour l'utilisation de lambdas Java8. Si vous débutez avec les lambdas, suivez ces conseils pour démarrer :

- Toute interface qui comporte uniquement une méthode est compatible lambda et peut être réduite dans la syntaxe la plus compacte.
- Si vous avez des doutes concernant les paramètres ou autre, écrivez une classe interne normale et laissez Android Studio la compacter en un lambda pour vous.

**Faites attention à la limiation des méthodes dex et évitez d'utiliser beaucoup de librairies.** Lorsque les applications Android sont packagées en un fichier Dex, elles ont une limitation de 65536 méthodes référencées [[1]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [[2]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [[3]](http://jakewharton.com/play-services-is-a-monolith/). Vous verrez une erreur fatale si jamais vous dépassez cette limite. Pour cette raison utilisez un minimum de librairies et utilisez l'outil [dex-method-counts](https://github.com/mihaip/dex-method-counts) pour déterminer quelles librairies peuvent être utilisées pour rester en dessous de cette limite. Evitez d'utiliser la librairie Guava car elle contient plus de 13k méthodes.

### Les activités et les fragments

Il n'y a pas d'accord global sur la meilleure façon d'oganiser une architecture Android avec des activités et des fragments ni au sein de la communauté Android ni au sein des développeurs Futurice. Square a même [une librairie pour contruire une architecture à partir de Views principalement](https://github.com/square/mortar), ce qui supprime le besoin d'utiliser des fragments. Cela n'est cependant pas considéré comme une pratique largement recommandable par la communauté Android.

De part l'histoire de l'API Android, vous pouvez considérer les Fragments comme des petites parties graphiques de l'écran. En d'autres termes, les Fragments sont en temps normal liés à l'interface graphique. Les Activities peuvent être considérées comme des controlleurs, elles sont particulièrement importante grâce à leurs cycles de vie et pour gérer les changements d'états. Vous allez cependant pouvoir constater certaines variations dans ces rôles : les activités peuvent être liées à l'interface graphique ([delivering transitions between screens](https://developer.android.com/about/versions/lollipop.html)), et [les fragments peuvent prendre le rôle d'un controlleur](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI). Nous vous suggérons d'agir consciencieusement de vous informer avant de prendre des décisions car il y a des avantages et des inconvénients à chaque méthode. Voici quelques conseils à prendre avec des pincettes sur lesquels il faut rester vigilant :

- Evitez de [mettre trop de fragments les uns dans les autres](https://developer.android.com/about/versions/android-4.2.html#NestedFragments) à cause du [bug matryoshka](http://delyan.me/android-s-matryoshka-problem/). Utilisez donc des fragments à l'intérieur d'autres lorsque cela a du sens (par exemple des fragments dans un ViewPager horizontal) ou lorsque vous savez ce que vous faites.
- Evitez de mettre trop de code dans les activités. Lorsque cela est possible, laissez les aussi légers que possible en tant que conteneurs et utilisez les majoritairement pour gérer les cycles de vie ou d'autres évènement importants liés à l'API android. Préférez utiliser des fragments seuls plutôt que des activités seules - mettez le code lié à l'interface graphique dans l'activité du fragment. Cela vous permet de le rendre réutilisable dans le cas ou vous souhaiteriez le mettre dans une interface avec des onglets par exemple ou dans une interface contenant plusieurs fragments sur une tablette. Evitez d'avoir des activités sans un fragment qui leur correspond sauf si vous savez ce que vous faites.
- N'abusez pas sur l'utilisation des API android comme les Intent pour le fonctionement interne de votre application. Cela pourrait avoir de facheuses conséquences comme des bugs ou des lags. Par exemple, il a été prouvé que l'utilisation des Intent pour communiquer entre les packages de votre application peut créer des lags de plusieurs secondes si votre application a été ouverte juste après le démarrage du téléphone.

### L'architecture des packages java

L'architecture java des applications android peut être vue approximativement comme du [Model-Vue-Controlleur](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller). Avec Android, [les fragments et les activités sont des controlleurs](http://www.informit.com/articles/article.aspx?p=2126865). D'un autre côté, ils sont une partie intégrante de l'interface utilisateur, ceux sont donc aussi des vues.

C'est pour cette raison qu'il est dur de classifier les fragments (ou les activités) comme uniquement des controlleurs ou des vues. Il est préférable de les laisser dans leur package `fragments`. Les activités peuvent rester dans le package de plus au niveau tant que vous suivez les conseils de la section précédente. Si vous comptez utiliser plus de 2 ou 3 activités, faites un package `activities`.

Autrement, l'architecture ressemble à celle d'un MVC typique avec un package `models` contenant les objects POJOs à être remplis à partir des réponses des API et à l'aide d'un parser JSON. Mettre aussi un package `views` contenant toutes vos vues customisées, les notifications, les bar d'actions, les widgets, etc. Les Adapters sont une autre paire de manche, car ils se situent entre les data et les vues. Cependant ils ont besoin d'exporter une vue via la méthode `getView()` donc vous pouvez inclure les Adapters dans un sous package `adapters` dans le package `views`.

Certains controlleurs ont une porté au niveau de l'application et sont proche du système Android. Ceux-ci peuvent être placés dans le package `managers`. D'autres classes diverses manipulant des données comme "DateUtils" peuvent être placées dans le package `utils`. Les classes qui s'occupent d'interagir avec le backend restent dans le package `network`.

Pour résumer, ordonés du plus proche du back au plus proche de l'utilisateur :

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

### Ressources

**Règles de nommage.** Suivez les conventions en préfixant vos noms du type comme `type_foo_bar.xml`. Exemples: `fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`.

**Organisation des fichiers XMLs.** Si vous n'êtes pas sûr(e) de l'organisation de vos fichiers XMLs, les conventions suivantes pourraient vous aider :

- Un attribut par ligne, indenté d'espaces
- `android:id` toujours en tant que premier attribut
- les `android:layout_****` toujours en haut après `android:id`
- l'attribut `style` tout en bas
- le tag `/>` de fermeture doit être sur sa propre ligne pour faciliter l'ajout d'attributs et l'ordonnancement.
- plutôt que de hardcoder l'attribut `android:text`, pensez à utiliser les [attributs Designtime](http://tools.android.com/tips/layout-designtime-attributes) disponibles dans Android Studio.

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

En règle générale, les attributs `android:layout_****` doivent être placés dans les fichiers layouts XML et les autres attributs `android:****` dans les fichiers de style XML. Cette règle a des exceptions mais s'applique bien la plupart du temps. L'idée est de garder uniquement les attributs de contenu et de layout (positionnement, marges, tailles) dans les fichiers de layout XML et de mettre tous les autres liés aux détails de l'apparence (couleurs, padding, police) dans des fichiers de style.

Les exceptions sont :

- `android:id` doit évidement se trouver dans les fichiers layout XML
- l'attribut `android:orientation` pour un `LinearLayout` a normalement plus de sens dans les fichiers layout XML
- l'attribut `android:text` devrait être dans les fichiers layout XML car il définit du contenu
- De temps en temps il est judicieux de mettre un style générique pour définir `android:layout_width` et `android:layout_height` mais par défaut ils devraient apparaitre dans les fichiers de layout XML.

**Utilisez des styles.** Quasiment tous les projets ont besoin d'utiliser correctement des styles, car il est très commun d'avoir des vues dont l'apparence est similaire. Vous devriez au moins avoir un style commun pour la plupart des contenu textuels dans votre application, par exemple :

```xml
<style name="ContentText">
    <item name="android:textSize">@dimen/font_normal</item>
    <item name="android:textColor">@color/basic_black</item>
</style>
```

Appliqué aux TextViews:

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/price"
    style="@style/ContentText"
    />
```

Vous aurez probablement besoin de faire la même chose pour les boutons, mais ne vous arretez pas là. Allez plus loin et mettez les groupes d'attributs qui se répètent dans un style commun.

**Séparez les fichiers de style très gros en plusieurs petits fichiers de style.** Vous n'êtes pas contraints d'avoir qu'un seul fichier `styles.xml`. Le SDK Android supporte plusieurs fichiers de style, il n'y a rien de magique avec le nom `styles`, ce qui importe ce sont les tags XML `<style>` à l'intérieur du fichier. Donc vous pouvez très bien avoir les fichiers `styles.xml`, `styles_home.xml`, `styles_item_details.xml`, `styles_forms.xml`. A l'opposé des fichiers dans le dossier de ressources qui doivent être nommés précisément pour que le système de build Android puisse les comprendre, les noms de fichier dans `res/values` sont arbitraires.

**`colors.xml` est une palette de couleurs.** Il ne devrait avoir rien d'autre à part des correspondances entre un nom de couleur et une valeur RGBA dans le fichier `colors.xml`. N'utilisez pas ce fichier pour définir différents types de boutons.

*Ne faites pas ça:*

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

Avec ce format il est facile de devoir se répéter et il est difficile de changer une couleur de base si besoin. De plus ces définitions sont liés à un contexte, comme "button" ou "comment" et devrait être placés dans un style de bouton et non dans le fichier `colors.xml`.

A la place, faites ceci:

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

Demandez cette palette de couleur à votre designer. Les noms n'ont pas besoin d'être des noms de couleur comme "vert", "bleu", etc. Les noms comme "brand_primary", "brand_secondary", "brand_negative" sont tout à fait acceptables. Le fait d'arranger les couleurs comme ceci vous permettra de les changer facilement et permettra de voir explicitement le nombre de couleurs différentes utilisées dans votre application. Normalement pour une interface graphique agréable à regarder, il faut minimiser le nombre de couleurs différentes utilisées.

**Traitez dimens.xml comme colors.xml.** Vous devriez aussi définir une "palette" de dimensions, d'espacements et de tailles de police typiques à votre application. Un bon exemple de fichier dimens.xml est :

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

Vous devriez utiliser les dimensions `spacing_****` pour le layout, les marges et les padding au lieu d'harcoder les valeurs. Il s'agit de traiter les dimensions comme les strings. Cela donnera à votre application de la consistence au niveau de l'apparence tout en facilitant l'organisation des styles et des layouts.

**strings.xml**

Nommez vos strings avec des clés qui ressemblent aux espaces de noms et n'ayez pas peur de répéter les valeurs pour deux ou plusieurs clés car les espaces de noms sont nécessaire pour apporter un contexte et enlever toute ambiguité.

**Pas bien**
```xml
<string name="network_error">Erreur de réseau</string>
<string name="call_failed">L'appel a échoué</string>
<string name="map_failed">Le chargement de la carte a échoué</string>
```

**Bien**
```xml
<string name="error.message.network">Erreur de réseau</string>
<string name="error.message.call">L'appel a échoué</string>
<string name="error.message.map">Le chargement de la carte a échoué</string>
```

N'écrivez pas les strings en majuscules. Basez vous sur les conventions des textes (ex.: en mettant une majuscule à la première lettre). Si vous avez besoin d'afficher le texte tout en majuscules, faites le en utilisant l'attribut [`textAllCaps`](http://developer.android.com/reference/android/widget/TextView.html#attr_android:textAllCaps) dans une TextView.

**Pas bien**
```xml
<string name="error.message.call">L'APPEL A ECHOUE</string>
```

**Bien**
```xml
<string name="error.message.call">L'appel a échoué</string>
```

**Evitez d'avoir une hiérarchie trop profonde de vues.** De temps à autres vous serez tenté d'ajouter simplement un autre LinearLayout pour accomplir l'arrangement des vues que vous souahité. Ce genre de situation peut arriver :

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

Même si vous ne voyez pas explicitement ceci dans un fichier de layout, cela arrivera peut être lorsque vous ajouterez des vues (en Java) dans d'autres vues.

Plusieurs problèmes pourront alors survenir. Vous pourrez par exemple rencontrer des problèmes de performances parce que le processeur doit gérer une arborescence de vues complexe. Une autre erreur aux conséquences grave serait : [StackOverflowError](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views).

Essayez donc de garder vos vues aussi plates que possible: apprenez à utiliser le [RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html), comment [optimize your layouts](http://developer.android.com/training/improving-layouts/optimizing-layout.html) et comment utiliser [`<merge>` tag](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts).

**Faites attention aux problèmes liés aux WebViews.** Lorsque vous devez afficher une page web, par exemple pour un article de presse, évitez de faire des manipulations sur la page du coté client, demandez plutôt aux développeurs backend de vous fournir une version "*pure*" de leur page HTML. [Les WebViews peuvent aussi provoquer des fuites de mémoire](http://stackoverflow.com/questions/3130654/memory-leak-in-webview) lorsqu'elles gardent en référence leur activité à la place d'être référencées à l'ApplicationContext. Evitez d'utiliser les WebViews pour afficher des simple textes ou des boutons, préferez des TextViews ou des Buttons.

### Emulateurs

Si votre métier est de développer des applications Android, achetez une license de [l'émulateur Genymotion](http://www.genymotion.com/). Les émulateurs Genymotion ont un taux de rafraichissement plus rapide que les machines virtuelles AVD. Ils ont aussi des outils qui permettent de faire des démos de votre application, de simuler tel ou tel type de connection internet, de simuler des positions GPS, etc. Ils sont aussi idéals pour faire des tests connectés. Vous aurez accès à beaucoup de différents périphériques (pas tous mais beaucoup) donc le coup d'achat d'une license de Genymotion revient moins cher qu'acheter le nombre équivalent de périphériques.

Les inconvénients sont : les émulateurs Genymotion ne contiennent pas tous les services Google comme Google Play Store ou Maps. Vous aurez aussi probablement besoin de tester des APIs spécifiques aux téléphones samsung donc vous aurez besoin d'acheter un vrai téléphone pour ça.

### Configuration Proguard

[ProGuard](http://proguard.sourceforge.net/) est utiliser en temps normal pour diminuer la taille des applications Android et pour obfuscer le code packagé.

<!-- Le fait d'utiliser ou nom Proguard dépend de la configuration de votre projet. Normalement on configure gradle pour utiliser Proguard lorsqu'on build la version release de l'apk. -->

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

Pour déterminer quels bouts de code doivent être gardés tel quel et lesquels doivent ête supprimés ou obfusqués, vous devez spécifier un ou plusieurs points d'entrée dans votre code.

La configuration par défaut du framework Android se trouve ici : `SDK_HOME/tools/proguard/proguard-android.txt`. Les règles spécifiques à chaque projet seront ajoutées automatiquement à la configuration par défaut à partir du fichier `my-project/app/proguard-rules.pro`.

Un problème récurrent lié à l'utilisation de Proguard est le crash de l'application à son démarrage avec `ClassNotFoundException` ou `NoSuchFieldException` ou autre erreur similaire même si la commande de build a réussi (i.e.: `assembleRelease`) sans avertissements.
Cela peut dire deux choses :

1. ProGuard a supprimé la class, l'enum, la méthode, le champ ou l'annotation en pensant qu'il/qu'elle n'était pas nécessaire.
2. ProGuard a obfusqué (renommé) la classe, l'enum ou le champ alors qu'il/qu'elle est utilisé indirectement avec son nom d'origine, i.e. à travers la réflexion Java.

Vérifiez `app/build/outputs/proguard/release/usage.txt` pour voir si l'objet en question a été supprimé.
Vérifiez `app/build/outputs/proguard/release/mapping.txt` pour voir si l'objet en question a été obfusqué (renommé).

Afin d'empêcher ProGuard de *supprimer* les classes qui sont nécessaires ou les champs qui sont nécessaires au bon fonctionement de l'application, ajoutez l'option `keep` à votre configuration ProGuard :

```
-keep class com.futurice.project.MyClass { *; }
```

Pour empêcher ProGuard *d'obfusquer* les classes ou les champs des calsses, ajoutez l'option `keepnames`:
```
-keepnames class com.futurice.project.MyClass { *; }
```

Regardez [cette configuration ProGuard de référence](https://github.com/futurice/android-best-practices/blob/master/templates/rx-architecture/app/proguard-rules.pro) pour voir quelques exemples.
Lisez [Proguard](http://proguard.sourceforge.net/#manual/examples.html) pour obtenir plus d'exemples.

**Faites un build de la release tôt dans le développement de votre application** pour vérifier si ProGuard garde correctement ce qui est important. De plus dès que vous ajoutez une nouvelle librairie dans votre projet, faites une build de la release et testez l'apk sur votre périphérique. N'attendez pas que votre application soit finalisée en version "1.0" pour faire ce build de la release car vous risquez de tomber sur de mauvaises surprises et d'avoir peu de temps pour les corriger.

**Conseil.** Sauvegardez le fichier `mapping.txt` pour chaque release que vous publiez aux utilisateurs. En gardant une copie de ce fichier pour chaque build de release, vous vous assurez de pouvoir débugguer le problème si un utilisateur rencontre un bug et vous envoie le rapport avec le code obfusqué.

**DexGuard**. Si vous avez besoin d'outils surpuissants pour optimiser votre application et surtout pour obfusquer votre code, utilisez [DexGuard](http://www.saikoa.com/dexguard), un logiciel commercial fait par la même équipe que ProGuard. Ce logiciel est aussi capable de couper les fichiers Dex en plusieurs fichiers afin de contourner la limite des 65k méthodes.

### Remerciements

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton et les autres développeurs à Futurice pour le partage de leur connaissances sur le développement Android.

### License

[Futurice Oy](http://www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)
