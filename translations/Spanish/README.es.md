# Buenas prácticas en el desarrollo de Android

Éstas son algunas de las lecciones aprendidas por los desarrolladores de Android en [Futurice](http://www.futurice.com). Evita reinventar la rueda siguiendo estas directrices. Si también estás interesado en desarrollo de iOS o Windows Phone, también puedes echarle un ojo a nuestras guías de [**Buenas prácticas para el desarrollo en iOS**](https://github.com/futurice/ios-good-practices) y [**Buenas prácticas para el desarrollo de Windows Phone apps**](https://github.com/futurice/windows-app-development-best-practices).

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--best--practices-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1091)

## Resumen

#### [Usar Gradle y su estructura recomendada de proyecto](#build-system)
#### [Guardar contraseñas y datos sensibles en gradle.properties](#gradle-configuration)
#### [IDEs y editores de texto](#ides)
#### [Usar la librería Jackson para el parseo de datos JSON](#libraries)
#### [No crear tu propio cliente HTTP, usa las librerías Volley o OkHttp](#networklibs)
#### [Evitar Guava y usar sólo algunas librerías debido al *límite de 65k métodos*](#methodlimitation)
#### [Estructura de paquetes Java](#java-structure-package)
#### [Navegar con cuidado al elegir entre Activities y Fragments](#activities-and-fragments)
#### [Los layouts XML son también código, trata de organizarlos correctamente](#resources)
#### [Usar estilos para evitar atributos duplicados en XML layouts](#styles)
#### [Usar varios archivos de estilos para evitar crear un único archivo de estilos enorme](#splitstyles)
#### [Mantener el archivo colors.xml claro y conciso, definiendo la paleta de colores](#colorsxml)
#### [Mantener el archivo dimens.xml claro y conciso, definiendo constantes genéricas](#dimensxml)
#### [No hacer una jerarquía muy profunda de ViewGroups](#deephierarchy)
#### [Evitar procesos en el lado del cliente para WebViews, y tener cuidado con las fugas de memoria](#webviews)
#### [Usar Robolectric para tests de unidad, Robotium para UI tests](#test-frameworks)
#### [Usar Genymotion como tu emulador](#emulators)
#### [Siempre usar ProGuard o DexGuard](#proguard-configuration)
#### [Usar SharedPreferences para una persistencia simple y ContentProviders para el resto](#data-storage)
#### [Usar Stetho para depurar tu app](#use-stetho)
#### [Usar Leak Canary para encontrar fugas de memoria](#use-leakcanary)

----------

### Android SDK

Coloca tu [Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools) en algún lugar de tu directorio `home` o en algún sitio independiente de cualquier otra aplicación. Algunas distribuciones de IDEs incluyen el SDK en la instalación, y es posible que lo coloque en el mismo directorio donde se encuentra el IDE. Esto puede ser contraproducente cuando necesites actualizar (o reinstalar) el IDE, ya que podrías perder la instalación del SDK, y forzándote a un nuevo largo y tedioso proceso de descarga.

También evita poner el SDK en otro directorio de nivel de sistema que necesite permisos de sudo, si su IDE se ejecuta bajo tu usuario y no bajo root.

<a name="build-system"></a>
### Sistema de construcción (Build system)

Tu opción por defecto debe ser [Gradle](http://tools.android.com/tech-docs/new-build-system). Con Gradle es más facil hacer:

- Crear diferentes `sabores` o variantes de tu app
- Hacer tareas sencillas con scripts
- Administrar y descargar dependencias
- Personalizar keystores
- Y mucho más...

Ant, el anterior sistema de compilación, está obsoleto desde 2015 y ahora el plugin de Gradle para Android está siendo desarrollado y apoyado por Google.

Es importante que el proceso de construcción de tu aplicación esté definido por tus archivos Gradle, en lugar de depender de configuraciones específicas del IDE. Esto permite una construcción consistente entre herramientas y un mejor soporte para sistemas de integración continua.

### Estructura de proyecto

Aunque Gradle ofrece mucha flexibilidad a la hora de estructurar tu proyecto, a menos que tenga una razón convincente para hacerlo, deberías adoptar su [estructura por defecto](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Project-Structure). Ésto simplificará tus scripts de construcción. 

<a name="gradle-configuration"></a>
### Configuración de Gradle

**Estructura general.** Seguir la [guía de Gradle de Google para Android](http://tools.android.com/tech-docs/new-build-system/user-guide)

**Pequeñas tareas.** En lugar de (shell, Python, Perl, etc) scripts, puedes crear tareas en Gradle. Tan sólo sigue la [documentación de Gradle](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF) para más detalles.

**Contraseñas.** En el archivo `build.gradle` de tu aplicación necesaritarás definir los `signingConfigs` para tu `release`. Ésto es lo que deberías de evitar:

_No hagas esto_. Esto apararecerá en el sistema de control de versiones.

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

En su lugar, crea un archivo `gradle.properties` que _no_ sea añadido al sistema de control de versiones:

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```

Ese archivo será automáticamente importado por Gradle, por lo tanto, podrás usarlo en el `build.gradle` de la siguiente forma:

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
            throw new InvalidUserDataException("Deberías definir tu KEYSTORE_PASSWORD y KEY_PASSWORD en gradle.properties.")
        }
    }
}
```

**Mejor usar una resolución de dependencia Maven que importar ficheros jar** Si explicitamente incluyes fichero jar en tu proyecto, éstos estarán en una versión especifica congelada. Descargar jars y gestionar sus actualizaciones es muy tedioso, éste es un problema que Maven soluciona de una manera adecuada, y es también la forma que Gradle recomienda usar. Por ejemplo:

```groovy
dependencies {
    implementation 'com.squareup.okhttp:okhttp:2.2.0'
    implementation 'com.squareup.okhttp:okhttp-urlconnection:2.2.0'
}
```    

**Evitar usar resoluciones de dependencia dinámicas en Maven**
Evitar el uso de resoluciones de dependencia dinámicas, como por ejemplo `2.1.+`, ya que puede dar lugar a compilaciones inestables o a diferencias de comportamiento entre compilaciones. El uso de versiones estáticas como `2.1.1` ayuda a crear un entorno de desarrollo más estable, predecible y reproducible.

**Usar diferentes nombres para los paquetes de las compilaciones que no sean para release**
Usar `applicationIdSuffix` para *debug* [build type](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Build-Types) para ser capaz de instalar el apk de ambas versiones, *debug* y *release*, en un mismo dispositivo (también puedes hacer esto para compilaciones personalizadas, si fuera necesario). Esto será especialmente valioso más adelante en el ciclo de vida de la aplicación, después de haber sido publicado en la tienda.

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

Utiliza iconos diferentes para distinguir las versiones instaladas en el dispositivo, por ejemplo, con diferentes colores o con un texto `debug` superpuesto. Gradle hace esto muy sencillo: con la estructura de proyecto por defecto, simplemente pon el icono de *debug* en `app/src/debug/res` y el de *release* en `app/src/release/res`. También podrías [cambiar el nombre de la app](http://stackoverflow.com/questions/24785270/how-to-change-app-name-per-gradle-build-type) por tipo de compilación, así como el `versionName` (como en el ejemplo anterior).

<a name="ides"></a>
### IDEs y editores de texto

**Usa el editor que quieras, pero debe de adaptarse a la estructura de proyecto.** Los editores son una elección personal, y es tu responsabilidad que su editor funcione de acuerdo con la estructura del proyecto y el sistema de construcción.

El IDE recomendado es [Android Studio](https://developer.android.com/sdk/installing/studio.html) porque es desarrollado y actualizado frecuentemente por Google, tiene un buen soporte para Gradle, contiene una amplia gama de herramientas muy útiles para la supervisión y análisis y generalmente está adaptada para el desarrollo de Android.

Alternativamente puedes utilizar un editor de texto sin formato como Vim, Sublime Text o Emacs. Pero en ese caso, necesitará usar Gradle y `adb` en la línea de comandos.

Usar [Eclipse ADT](http://developer.android.com/tools/help/adt.html) para el desarrollo de Android ya no es una buena práctica.
[Google dejó de dar soporte a ADT en 2015](http://android-developers.blogspot.fi/2015/06/an-update-on-eclipse-android-developer.html) y sugirió a los usuarios a [migrar a Android Studio](http://developer.android.com/sdk/installing/migrate.html) lo antes posible.

Cualquiera que sea tu elección, evita añadir ficheros de configuración específicos del editor en cuestión, como los ficheros de Android Studio `.iml`, al sistema de control de versiones, ya que normalmente contienen configuraciones específicas de tu máquina local que no funcionarán para los equipos de tus compañeros de trabajo.

En última instancia, se amable con otros desarrolladores; No los obligue a cambiar su herramienta de preferencia si así es como son más productivos.

<a name="libraries"></a>
### Librerías

**[Jackson](http://wiki.fasterxml.com/JacksonHome)** es una librería de Java para convertir Objects en JSON y viceversa. [Gson](https://code.google.com/p/google-gson/) es también una elección muy popular para este cometido, sin embargo creemos que Jackson es más completo, ya que suporta diferentes maneras de procesamiento de JSON: streaming, el modelo de árbol en memoria y el tradicional enlace de datos JSON-POJO. Ten en cuenta también, que Jackson es una librería más grande que GSON, así que dependiendo de tu caso, podrías optar por GSON para evitar el límite de 65k métodos. Otras alternativas: [Json-smart](https://code.google.com/p/json-smart/) y [Boon JSON](https://github.com/RichardHightower/boon/wiki/Boon-JSON-in-five-minutes)

<a name="networklibs"></a>
**Networking, caching, e imágenes.** Hay un par de soluciones probadas para realizar las peticiones a los servidores back-end, los cuales debes utilizar teniendo en cuenta la implementación de tu app. Usa [Volley](https://android.googlesource.com/platform/frameworks/volley) o [Retrofit](http://square.github.io/retrofit/). Volley también proporciona ayuda para cargar y almacenar imágenes en caché. Si eliges Retrofit, considera usar [Picasso](http://square.github.io/picasso/) para cargar y guardar en caché imágenes, y [OkHttp](http://square.github.io/okhttp/) para unas peticiones HTTP más eficientes. Todos ellos, Retrofit, Picasso y OkHttp han sido creados por la misma compañía, por lo que se complementan muy bien entre ellas. [OkHttp puede también ser usado en conjunción con Volley](http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835).
[Glide](https://github.com/bumptech/glide) es otra opción para cargar y guardar imágenes en caché. Tiene mejor rendimiento que Picasso, GIF y soporte para imágenes circulares, pero también tiene un número mayor de métodos.

**[RxJava](https://github.com/ReactiveX/RxJava)** es una libreía para Programación Reactiva (Reactive Programming), en otras palabras, gestionar eventos asíncronos. Es un paradigma muy potente y prometedor, el cuál puede ser también muy confuso al ser tan distinto de cualquier otro. Recomendamos tomar precauciones antes de usar esta librería para diseñar toda la aplicación. Hay algunos proyectos hechos usando RxJava, si necesitaras ayuda ponte en contacto con alguna de estas personas: Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen. Hemos escrito algunas entradas de blog sobre este tema: [[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android), [[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754), [[4]](http://blog.futurice.com/android-development-has-its-own-swift).

Si no tienes experiencia previa con Rx, empieza a usarlo únicamente para las respuestas devueltas de una API. Poco a poco, podrías empezar a aplicarlo para gestión de eventos en un componente de la UI, como un evento de click o escribir en un campo de búsqueda. Si confías en tus habilidades en Rx y quieres aplicarlo a toda la arquitectura del proyecto, entonces será mejor que escribas Javadocs en todas las partes complicadas. Ten en cuenta que otros programadores pueden no estar familiarizados con RxJava y podría ser muy dificil mantener el proyecto. Haz lo posible para ayudarles a entender tu código y también Rx.

**[Retrolambda](https://github.com/evant/gradle-retrolambda)** es una librería de Java para usar expresiones Lambda en Android y otras plataformas pre-JDK8. Retrolambda ayuda a mantener el código compactado y legible, especialmente si utilizas un estilo funcional como, por ejemplo, con RxJava.

Android Studio ofrece soporte de ayuda de código lambda para Java 8. Si eres nuevo en lambdas, simplemente usa lo siguiente para comenzar:

- Cualquier interfaz con sólo un método es "lambda friendly" y puede ser englobado en una sintaxis más compacta y reducida.
- En caso de duda sobre los parámetros y demás, crea una clase anónima interna normal y luego deja que Android Studio lo pliegue en un lambda.

<a name="methodlimitation"></a>
**Ten cuidado con la limitación de 65k métodos y evita usar muchas librerías.** Las aplicaciones Android, cuando están empaquetadas en un fichero dex, tienen una limitación de 65536 métodos referenciados [[1]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [[2]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [[3]](http://jakewharton.com/play-services-is-a-monolith/). Verás un error fatal de compilación si sobrepasas este límite. Por esta razón, es recomendable usar el menor número de librerías posibles y usar el contador de métodos dex: [dex-method-counts](https://github.com/mihaip/dex-method-counts) una herramienta para determinar el conjunto de librerías que pueden ser usadas para mantenerte por debajo de ese límite. Especialmente evita usar la librería Guava, ya que contiene cerca de 13k métodos.

<a name="activities-and-fragments"></a>
### Activities y Fragments

No hay un consenso en la comunidad ni entre los desarrolladores en Futurice sobre cual es la mejor organización para una arquitectura Android con Fragments y Activities. Square incluso tiene [una librería para construir arquitecturas principalmente con Views](https://github.com/square/mortar), superando la necesidad de usar Fragments, pero ésto no está considerado aún como una práctica recomendable en la comunidad Android.

Debido a la historia de la API de Android, puedes considerar que los Fragments son como partes de la interfaz de usuario de una pantalla. En otras palabras, los Fragments normalmente están relacionados con la interfaz de usuario. Las Activities pueden considerarse vagamente como las controladoras, son especialmente importantes para su ciclo de vida y para la gestión del estado. Sin embargo, es probable que veas variación en estas funciones: las Activities pueden tomar funciones de UI ([envío de transiciones entre pantallas](https://developer.android.com/about/versions/lollipop.html)), y [Fragments pueden ser usados sólo como controladores](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI). Sugerimos que lo pruebes con cuidado, tomando decisiones con información suficiente, ya que hay varios inconvenientes a tener en cuenta al elegir una arquitectura sólo con Fragments, o sólo con Activities o sólo con Views. Aquí hay algunos consejos sobre con lo que se debe tener cuidado:

- Evita usar [Fragments anidados](https://developer.android.com/about/versions/android-4.2.html#NestedFragments) siempre que se pueda, ya que pueden causar [matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/). Usa Fragments anidados únicamente cuando tenga sentido usarlos (como por ejemplo, Fragments en un ViewPager horizontal dentro de un Fragment similar a una pantalla) o si de verdad tienes razones suficientes para tomar esa decisión.

- Evita poner demasiado código en las Activities. Siempre que sea posible, manténlas como unos contenedores ligeros de código, que existen en tu app principalmente para el ciclo de vida de la aplicación y otras tareas importantes de la API de Android. 
Es preferible Activities con un sólo Fragment en lugar de sólo Activities - poner el código UI en el Fragment de la Activity. De esta forma puedes reutilizarlo siempre que se necesite cambiar algo como, por ejemplo, reajustar el tamaño en una pantalla de tablet con varios Fragments dentro. Evita tener una Activity sin su correspondiente Fragment, a no ser que tengas verdaderas razones para ello.

- No abuses de las API level de Android, ya que puedes depender en gran medida de Intents para el funcionamiento interno de su aplicación. Podría afectar al sistema operativo Android o a otras aplicaciones, creando errores o retrasos. Por ejemplo, se sabe que si su aplicación utiliza Intents para la comunicación interna entre sus paquetes, podría incurrir en retrasos de varios segundos en la experiencia del usuario si la aplicación se abriera justo después del inicio del sistema operativo.

<a name="java-structure-package"></a>
### Estructura de paquetes Java

La arquitectura Java para aplicaciones Android puede ser brevemente explicada en [Modelo-Vista-Controlador (Model-View-Controller)](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller). En Android, [Fragments y Activities son realmente clases controladoras](http://www.informit.com/articles/article.aspx?p=2126865). Por otro lado, también son explícitamente parte de la UI, por lo tanto también son Vistas.

Por esta razón, es difícil clasificar a los Fragments (o Activities) estrictamente como controladores o vistas. Por eso, es mejor dejarlas estar en su propio paquete `fragments`. Las Activities pueden estar en el nivel superior  can stay on the top-level package as long as you follow the advice of the previous section. If you are planning to have more than 2 or 3 activities, then make also an `activities` package.

De lo contrario, la arquitectura puede parecerse a un MVC típico, con un paquete `models` que contiene POJOs para ser usados mediante el parseador JSON y las respuestas de una API, y un paquete `views` con todas las Views, notificaciones, Action Bars Views, widgets, etc. Los adaptadores (Adapters) son una estructura intermediaria entre los datos y las vistas. Sin embargo, normalmente necesitan exportar algunas View a través de `getView()`, por lo que se pueden incluir el paquete `adapters` dentro del paquete `views`.

Algunas clases de controladores se aplican a toda la aplicación y están cercanos al sistema Android. Éstos puede encontrarse en el paquete `managers`. Para las clases de procesamiento de datos, como "DateUtils", podría crearse el paquete `utils`. Y las clases responsables de interactuar con el backend en el paquete `network`.

En general, ordenado desde el más cercano al backend al más cercano al usuario:

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

<a name="resources"></a>
### Recursos (Resources)

**Cómo nombrarlos.** Sigue el siguiente patrón para nombrar los los ficheros dependiendo de su tipo, `type_foo_bar.xml`. Ejemplos: `fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`.

**Organizando los layouts XML.** Si no estás seguro de qué formato seguir en un layout XML, puede que te ayude seguir estas reglas.

- Un atributo por línea, y 4 espacios de sangrado.
- Empezar siempre con el atributo `android:id`.
- Los atributos del tipo `android:layout_****` siempre arriba.
- El atributo `style` al final.
- La etiqueta de cierre `/>` en su propia línea, para facilitar el ordenado o la adición de nuevos atributos.
- En lugar de usar textos estáticos (`hard coded`) en `android:text`, considera usar [atributos Designtime](http://tools.android.com/tips/layout-designtime-attributes) disponibles en Android Studio.

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

Como regla de oro, los atributos `android:layout_****` deberían estar definidos en el layout XML, mientras que otros atributos como `android:****` deberían mantenerse en un fichero XML de estilos. Esta regla tiene excepciones, pero en general funciona bien. La idea es mantener los atributos de del layout (posicionamiento, márgenes, tamaño, etc.) y el contenido en el fichero layout, mientras que todos los detalles de la apariencia (colores, padding, fuentes, etc.) en el fichero de estilos.

Las excepciones son:

- `android:id` obviamente debería estar en el archivo del layout.
- `android:orientation` para un `LinearLayout` normalmente tiene más sentido estar en el fichero del layout.
- `android:text` debería estar en el fichero del layout, ya que define el contenido.
- Algunas veces tendrá sentido definir un valor genérico para `android:layout_width` y `android:layout_height` en un fichero de estilos, pero por defecto éstos deberían estar en los ficheros del layout.

<a name="styles"></a>
**Uso de estilos (styles).** La gran mayoría de proyectos necesita usar apropiadamente estilos, ya que es muy común tener que reusar detalles de apariencia en algunas Views. Al menos deberías tener un estilo común para la mayoría de textos en la aplicación, por ejemplo:

```xml
<style name="ContentText">
    <item name="android:textSize">@dimen/font_normal</item>
    <item name="android:textColor">@color/basic_black</item>
</style>
```

Y aplicándolo a un TextView:

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/price"
    style="@style/ContentText"
    />
```

Seguramente necesitarás hacer lo mismo para botones, pero no te pares aquí. Ve más lejos y mueve un grupo de atributos del tipo `android:****` repetidos y relacionados a un estilo común.v

<a name="splitstyles"></a>
**Divide los ficheros grandes de estilos en varios ficheros.** No necesitas tener un único fichero `styles.xml`. Android SDK soporta otros archivos también, no hay nada mágico sobre el nombre `styles`, lo que importa son las etiquetas XML` <style> ` de dentro del archivo. Por lo tanto, puedes tener `styles.xml`,` styles_home.xml`, `styles_item_details.xml`,` styles_forms.xml`. A diferencia de los nombres de directorio de recursos que sí tienen algún significado para el sistema de compilación, los nombres de archivo en `res/values` pueden ser arbitrarios.

<a name="colorsxml"></a>
**`colors.xml` es una paleta de colores.** No debería haber nada más en tu `colors.xml` que una transformación del nombre de un color a su valor RGBA. No lo uses para definir valores RGBA para distintos tipos de botones.

*No hagas esto:*

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

De esta forma, es muy fácil que repitas valores RGBA y que resulte complicado cambiar algún color básico si lo necesitas. De hecho, estas definiciones están relacionadas con un contexto, como por ejemplo "button" o "comment", y debería estar en un fichero de estilo para botones y no en `colors.xml`.

En su lugar, mejor hacer lo siguiente:

```xml
<resources>

    <!-- Escala de grises -->
    <color name="white"     >#FFFFFF</color>
    <color name="gray_light">#DBDBDB</color>
    <color name="gray"      >#939393</color>
    <color name="gray_dark" >#5F5F5F</color>
    <color name="black"     >#323232</color>

    <!-- Colores básicos -->
    <color name="green">#27D34D</color>
    <color name="blue">#2A91BD</color>
    <color name="orange">#FF9D2F</color>
    <color name="red">#FF432F</color>

</resources>
```

Pídele ayuda al diseñador de la aplicación para crear la paleta de colores. Los nombres no tienen por qué ser el nombre del color "verde", "azul", etc. También nombres como "color_primario" o "color_segundario" están bien. Dar el correcto formato a los colores facilitará su cambio o refactorización, y también hará que se sepa cuántos colores diferentes se están utilizando en la aplicación. Normalmente para una IU aceptable, es importante reducir la variedad de colores que se utilizan.

<a name="dimensxml"></a>
**Tratar el fichero dimens.xml como al colors.xml.** Se debería definir una "paleta" para los tamaños de letra y espaciado, con el mismo cometido que para los colores. Un buen ejemplo de fichero de dimensiones sería:

```xml
<resources>

    <!-- Tamaño de letra -->
    <dimen name="font_larger">22sp</dimen>
    <dimen name="font_large">18sp</dimen>
    <dimen name="font_normal">15sp</dimen>
    <dimen name="font_small">12sp</dimen>

    <!-- Espacio entre dos Views -->
    <dimen name="spacing_huge">40dp</dimen>
    <dimen name="spacing_large">24dp</dimen>
    <dimen name="spacing_normal">14dp</dimen>
    <dimen name="spacing_small">10dp</dimen>
    <dimen name="spacing_tiny">4dp</dimen>

    <!-- Tamaño de una View -->
    <dimen name="button_height_tall">60dp</dimen>
    <dimen name="button_height_normal">40dp</dimen>
    <dimen name="button_height_short">32dp</dimen>

</resources>
```

Deberías usar `espacio_****` para definir los márgenes y paddings en los layouts, de la misma forma que los textos son normalmente tratados. Esto hará que el look-and-feel de la aplicación sea consistente, a la vez que hace más fácil organizar y cambiar los estilos y layouts.

**strings.xml**

Asigne nombres a las cadenas de texto con claves que se asemejen al contexto al que se refieren y romper la ambigüedad, así que no tengas miedo de repetir un valor para dos o más claves, si realmente lo necesitas.

**Mal**
```xml
<string name="network_error">Network error</string>
<string name="call_failed">Call failed</string>
<string name="map_failed">Map loading failed</string>
```

**Bien**
```xml
<string name="error_message_network">Network error</string>
<string name="error_message_call">Call failed</string>
<string name="error_message_map">Map loading failed</string>
```

No escribas cadenas de texto en mayúsculas. Sigue las convenciones, como por ejemplo, poner en mayúscula sólo el primer carácter del texto. Si necesitas mostrar todo el texto en mayúsculas, hazlo usando, el atributo [
`textAllCaps`](http://developer.android.com/reference/android/widget/TextView.html#attr_android:textAllCaps) en un TextView.

**Mal**
```xml
<string name="error_message_call">CALL FAILED</string>
```

**Bien**
```xml
<string name="error_message_call">Call failed</string>
```

<a name="deephierarchy"></a>
**Evite una jerarquía de vistas de gran profundidad.** A veces, se puede estar tentado a añadir un LinearLayout, para realizar la composición de vistas. Puede ocurrir que se dé esta situación:

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

Incluso si esto no ocurre de explícitamente en un único layout, podría terminar ocurriendo si están inflando vistas (en Java) dentro de otras vistas.

Pueden ocurrir un par de problemas a raíz de esto. Es posible que se experimenten problemas de rendimiento, ya que hay un árbol muy complejo en la UI que el procesador necesita controlar. Otro problema más grave es la posibilidad de un [Error de StackOverflow](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views) al tener demasiadas vistas.

Por lo tanto, intenta mantener la jerarquía de las vistas lo más plana posible: aprende a utilizar [RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html), y cómo [optimizar tus diseños](Http://developer.android.com/training/improving-layouts/optimizing-layout.html) y utilizar la etiqueta [`<merge>`](http://stackoverflow.com/questions/8834898/whatwhat-is-the-purpose-of-androids-merge-tag-in-xml-layouts).

<a name="webviews"></a>
**Ten cuidado con los problemas relacionados con WebViews.** Cuando tengas que mostrar una página web, por ejemplo para un artículo de noticias, evita hacer el procesamiento del lado del cliente para limpiar el código HTML, solicita un HTML "*puro*" a los programadores del backend. [Los WebViews también pueden crear memory leaks](http://stackoverflow.com/questions/3130654/memory-leak-in-webview) cuando mantienen una referencia a su actividad, en lugar de estar vinculados al ApplicationContext. Evita usar un WebView para textos simples o botones, usa en su lugar TextViews o Buttons.

<a name="test-frameworks"></a>
### Herramientas de Testing

Las herramientas de testing en el SDK de Android siguen estando "en pañales", especialmente lo relativo al testing de UI. Actualmente, Android Gradle implementa una tarea llamada [`connectedAndroidTest`](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing) la cual, lanza los test de unidad que creas, usando una [extensión de JUnit para Android](http://developer.android.com/reference/android/test/package-summary.html). Esto significa que necesitarás conectar un dispositivo o un emulador para ejecutar los test. Sigue la guía oficial [[1]](http://developer.android.com/tools/testing/testing_android.html) [[2]](http://developer.android.com/tools/testing/activity_test.html) para el testing.

**Usar [Robolectric](http://robolectric.org/) únicamente para los test de unidad, no para vistas.** Se trata de una herramienta de testing que busca realizar los test "sin necesidad de conectar un dispositivo" buscando una mayor velocidad de desarrollo, especialmente adecuadas para test de unidad en Models y View Models. Sin embargo, los test con Robolectric son inexactos e incompletos con respecto a los test de UI. Tendrás problemas para probar elementos de la UI relacionados con animaciones, diálogos, etc., y esto se complicará más aún por el hecho de que estás "a ciegas" (ya que las pruebas se realizan sin ver la pantalla sobre las que se ejecutan los test).

**[Robotium](https://code.google.com/p/robotium/) Hace que la escritura de test para UI sea más sencillo.** No necesitas Robotium para ejecutar los test para UI, pero probablemente será beneficioso ya que posee ayudantes para obtener y analizar vistas y controlar la pantalla. Los casos de prueba serán tan simples como:

```java
solo.sendKey(Solo.MENU);
solo.clickOnText("More"); // Busca la primera ocurrencia de "More" y hace click en él
solo.clickOnText("Preferences");
solo.clickOnText("Edit File Extensions");
Assert.assertTrue(solo.searchText("rtf"));
```

<a name="emulators"></a>
### Emuladores

Si estás desarrollando aplicaciones de Android como profesión, compra una licencia para el [emulador de Genymotion](http://www.genymotion.com/). Los emuladores de Genymotion se ejecutan a una velocidad de frames/seg. más rápida que los emuladores AVD típicos. Tienen herramientas para enseñar tu aplicación, emulando la calidad de conexión de red, posiciones GPS, etc. También son ideales para testing. Tiene acceso a muchos (no a todos) dispositivos diferentes, por lo que el coste de una licencia de Genymotion es en realidad mucho más barato que comprar múltiples dispositivos reales.

Las advertencias son: Los emuladores de Genymotion no poseen todos los servicios de Google, como Google Play Store y Google Maps. Es posible que también necesites probar APIs específicas de Samsung, por lo que es necesario tener un dispositivo Samsung real.

<a name="proguard-configuration"></a>
### Configuración de Proguard

[ProGuard](http://proguard.sourceforge.net/) Se utiliza normalmente en proyectos de Android para reducir y ofuscar el código compilado.

Ya uses ProGuard o no, dependerá de la configuración de tu proyecto. El uso habitual es configurar Gradle para usar ProGuard al crear el apk para la release.

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

Para determinar qué código tiene que ser conservado y qué código se puede descartar u ocultar, se debe especificar uno o más puntos de entrada a tu código. Estos puntos de entrada suelen ser clases con métodos principales, applets, midlets, Activities, etc.

Android usa una configuración por defecto que se encuentra en `SDK_HOME/tools/proguard/proguard-android.txt`.
Usando ésta configuración, con reglas específicas para el proyecto en cuestión en ProGuard, definidas en `my-project/app/proguard-rules.pro`, se anexarán a la configuración predeterminada.

Uno de los problemas comunes relacionados con ProGuard es ver fallar la aplicación al inicio con `ClassNotFoundException`, `NoSuchFieldException` o similar, incluso aunque el comando de compilación (como por ejemplo: `assembleRelease`) terminó satisfactoriamente sin advertencias.

Esto puede deberse a:

1. ProGuard ha eliminado esa clase, enum, método, variable o anotación, considerando que no era necesario.
2. ProGuard ha ofuscado (renombrado) la clase, enum o nombre de campo, pero está siendo utilizado indirectamente por su nombre original, es decir, a través de la reflexión de Java.

Comprueba `app/build/outputs/proguard/release/usage.txt` para ver si el objeto en cuestión ha sido eliminado.
Y comprueba `app/build/outputs/proguard/release/mapping.txt` para ver si el objecto ha sido ofuscado.

Para prevenir que ProGuard *se deshaga* de las clases o elementos necesarios, añádelos a `keep` en la configuración de ProGuard:

```
-keep class com.futurice.project.MyClass { *; }
```

Para prevenir que ProGuard *ofusque* clases u otros elementos, añádelos a `keepnames`:
```
-keepnames class com.futurice.project.MyClass { *; }
```

También podrás encontrar más ejemplos en la documentación oficial de [Proguard](http://proguard.sourceforge.net/#manual/examples.html).

**En las primeras etapas de tu proyecto, haz una versión para release** para comprobar si las reglas de ProGuard funcionan correctamente. También, cuando incluyas nuevas librerías al proyecto, haz otra versión para release y prueba el apk en un dispositivo real. No esperes hasta la versión "1.0" de tu app para hacer las pruebas, ya que podrías encontrarte con sorpresas inesperadas y un tiempo muy ajustado para poder solucionar cualquier error.

**Consejo.** Guarda el fichero `mapping.txt` de cada release que publiques a tus usuarios. Haciendo ésto, podrás depurar un problema cuando un usuario encuentre un error y lo envíe como una traza ofuscada.

**DexGuard**. Si lo que necesitas es una herramienta más potente para optimizar, y especialmente para ofuscar el código, considera la opción de [DexGuard](http://www.saikoa.com/dexguard), es un software comercial hecho por el mismo equipo que hizo ProGuard. También puede dividir fácilmente archivos Dex para resolver la limitación de 65k métodos.

<a name="data-storage"></a>
### Almacenamiento de datos

#### SharedPreferences

Si lo único que necesitas guardar son datos simples y tu app se ejecuta en un único proceso SharedPreferences entonces puede que sea suficiente como opción por defecto.

Hay dos razones por las que no podrías no querer usar SharedPreferences:

* *Rendimiento*: La información a guardar es muy compleja o es mucha.
* *Múltiples procesos accediendo la información*: Puede que tengas widgets o servicios remotos que se ejecutan en sus propios procesos y requieren de datos sincronizados.


#### ContentProviders

En el caso de SharedPreferences no sea suficiente para tu app, deberías utilizar la plataforma estándar ContentProviders, que son más rápidos y su procesamiento es más seguros.

El único problema con ContentProviders es la cantidad de código que se necesita para configurarlos, además de tutoriales de baja calidad. Sin embargo, es posible generar el ContentProvider utilizando librerías como [Schematic](https://github.com/SimonVT/schematic), que reduce significativamente el esfuerzo.

Aún así se necesita escribir algo de código para leer los objetos de las columnas SQLite y viceversa. Es posible serializar los objetos, por ejemplo con Gson, y sólo guardar la cadena de texto resultante. De esta manera se pierde en rendimiento pero por otro lado no necesitas declarar una columna para todos los campos de la clase.


#### Usar un ORM

Por lo general, no recomendamos el uso de librerías de mapeo Objeto-Relacional (Object-Relational Mapping), a menos que tengas datos extremadamente complejos y tengas una necesidad grave. Tienden a ser complejas y requieren tiempo para aprender. Si decidieras usar un ORM debes prestar atención a si es o no un _proceso seguro_ y si su aplicación realmente lo requiere, ya que muchas de las soluciones existentes de ORM sorprendentemente no lo son.


<a name="use-stetho"></a>
### Usar Stetho 

[Stetho](http://facebook.github.io/stetho/) es una herramienta de depuración para aplicaciones Android de Facebook que se integra con las herramientas de desarrollo (Developer Tools) del navegador Chrome. Con Stetho puedes inspeccionar fácilmente tu aplicación, especialmente el tráfico de red. También te permite inspeccionar y editar fácilmente bases de datos SQLite y las SharedPreferences en su aplicación. Sin embargo, debes asegurarse de que Stetho sólo está habilitada en la versión de debug y no en la de release. 

<a name="use-leakcanary"></a>
### Usar LeakCanary

[LeakCanary](https://github.com/square/leakcanary) es una librería que hace que la detección en tiempo de ejecución y la identificación de memory leaks sean una parte rutinaria más del proceso de desarrollo de apps. Échale un ojo a la librería [wiki](https://github.com/square/leakcanary/wiki) para tener detalles sobre la configuración y su uso. Únicamente recuerda configurar sólo la dependencia "no-op" en tu versión de release!

### Gracias a

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton y otros desarrolladores de Futurice por compartir su conocimiento en el desarrollo de Android.

### Licencia

[Futurice Oy](http://www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)
