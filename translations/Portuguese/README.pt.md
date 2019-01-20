# As melhores práticas no desenvolvimento Android

Lições aprendidas por programadores Android na [Futurice](http://www.futurice.com). Evite reinventar a roda seguindo estas recomendações. Se está interessado em desenvolver aplicações para iOS ou Windows Phone, não se esqueça de verificar também nossas [**iOS Good Practices**](https://github.com/futurice/ios-good-practices) e [**Windows App Development Best Practices**](https://github.com/futurice/windows-app-development-best-practices).

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--best--practices-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1091)

## Resumo

#### Use o Gradle e a estrutura do projeto por ele recomendada
#### Coloque suas senhas e dados sensíveis em gradle.properties
#### Não escreva o seu próprio cliente de HTTP, utilize as bibliotecas Volley ou OkHttp
#### Use a biblioteca Jackson para fazer o parse de JSON
#### Evite o Guava e utilize poucas bibliotecas devido ao *limite máximo de 65 mil métodos*
#### Use Fragmentos para representar um ecrã UIƒ
#### Use Atividades apenas para gerir Fragmentos
#### As layouts de XML são código, organize-as bem
#### Use estilos para evitar atributos duplicados nas layouts de XML
#### Use múltiplos ficheiros de estilos para evitar apenas um muito grande
#### Mantenha o seu colors.xml pequeno e DRY (sem repetições), defina apenas a palete
#### Mantenha também o dimens.xml DRY, defina constantes genéricas
#### Não faça uma hierarquia muito profunda de ViewGroups
#### Evite processar WebViews no cliente, e cuidado com fugas
#### Use o Robolectric para testes unitários, Robotium para testes conetados (UI)
#### Use sempre o ProGuard ou DexGuard
#### Use SharedPreferences para persistência simples, caso contrário use ContentProviders


----------

### Android SDK

Coloque o [Android SDK](https://developer.android.com/sdk/installing/index.html?pkg=tools) algures no seu diretório inicial ou algum outro local independente da aplicação . Alguns IDEs incluem o SDK aquando da instalação, e pode colocá-lo sob o mesmo diretório que o IDE. Isso pode ser ruim quando precisa de atualizar (ou reinstalar) o IDE, ou quando mudar IDEs . Além disso, evite colocar o SDK noutro diretório em nível de sistema que pode precisar de permissões sudo, se o IDE é executado sob o utilizador e não na raiz.

### Sistema de compilação

A sua opção padrão deve ser o [Gradle](http://tools.android.com/tech-docs/new-build-system). O Ant é muito mais limitado e também mais verboso. Com o Gradle, é fácil:

- Construir sabores ou variantes diferentes do seu aplicativo
- Fazer tarefas simples tipo script
- Gerir e fazer o download de dependências
- Personalizar keystores
- E mais

O plugin Gradle do Android também está a ser activamente desenvolvido pela Google como o novo sistema de compilação padrão.

### Estrutura do Projeto

Existem duas opções populares: a estrutura do projeto antiga Ant juntamente com o Eclipse ADT, e a nova estrutura do projeto Gradle juntamente com o Android Studio. Você deve escolher a nova estrutura do projeto. Se o seu projeto usa a estrutura antiga, considere-a desatualizada e comece a transferi-la para a nova estrutura.

Estrutura antiga:

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

Estrutura nova:

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

A principal diferença é que a nova estrutura separa explicitamente 'source sets' (`main`, `androidTest`), um conceito do Gradle. Você poderá, por exemplo, adicionar diretórios 'paid' e 'free' a `src` que terá o código fonte para os sabores pagos e gratuitos da sua aplicação.

Ter um diretório de topo `app` é útil para distinguir a sua aplicação de outras bibliotecas (por exemplo, `library-foobar`) que terá uma referência na sua aplicação. O `settings.gradle` posteriormente mantém referências para estas bibliotecas, e o `app/build.gradle` pode fazer referência a elas.

### Configuração do Gradle

**Estrutura geral.** Siga [o guia do Google sobre Gradle para Android](http://tools.android.com/tech-docs/new-build-system/user-guide)

**Pequenas tarefas.** Ao invés de scripts (shell, Python, Perl, etc.), pode fazer tarefas com o Gradle. Siga [a documentação do Gradle](http://www.gradle.org/docs/current/userguide/userguide_single.html#N10CBF) para mais detalhes.

**Senhas.** No `build.gradle` da sua aplicação, precisa de definir `signingConfigs` para a _compilação de lançamento_. O que deverá evitar:

_Não faça isto_. Irá aparecerá no sistema de controlo de versão.

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

Ao invés, crie o ficheiro `gradle.properties` o qual _não_ deverá ser adicionado ao sistema de controle de versão:

```
KEYSTORE_PASSWORD=password123
KEY_PASSWORD=password789
```

Aquele ficheiro será importado automaticamente pelo gradle, podendo utilizá-lo no `build.gradle` da seguinte forma:

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

**Prefira a resolução de dependências Maven em vez de importar ficheiros jar.** Se você incluir explicitamente os ficheiros jar no seu projeto, eles terão uma versão fixa específica, como `2.1.1`. Baixar jars e gerir atualizações é uma confusão, este é o problema que o Maven resolve de forma adequada, e é também incentivada nas compilações de Android Gradle. Por exemplo:

```groovy
dependencies {
    implementation 'com.squareup.okhttp:okhttp:2.2.0'
    implementation 'com.squareup.okhttp:okhttp-urlconnection:2.2.0'
}
```    

**Evite a resolução de dependências dinâmicas do Mavem**

Evite usar versões dinâmicas, como por exemplo `2.1.+`, isto porque podem resultar em compilações instáveis ou subtis diferenças de comportamento não rastreadas entre as compilações. O uso de versões estáticas como `2.1.1` ajuda a criar um ambiente de desenvolvimento mais estável, previsível e repetível.

### IDEs e editores de texto

**Use qualquer editor, mas este deverá integrar bem com a estrutura do projeto.** Os editores são uma escolha pessoal, contudo é sua responsabilidade colocar o editor a funcionar de acordo com a estrutura do projeto e o sistema de compilação.

O IDE mais recomendado neste momento é o [Android Studio](https://developer.android.com/sdk/installing/studio.html), porque foi e está a ser desenvolvido pela Google, é o mais próximo do Gradle, usa a nova estrutura de projeto por definição, encontra-se em fase estável, e foi adaptado especificamente para desenvolvimento Android.

Pode usar o [Eclipse ADT](https://developer.android.com/sdk/installing/index.html?pkg=adt) se assim o desejar, mas terá de o configurar, visto que ele espera a antiga estrutura do projeto e compilações Ant. Poderá até mesmo usar editores de texto simples como o Vim, Sublime Text, ou Emacs. Nesse caso, irá ter de usar Gradle e `adb` na linha de comando. Se a integração do Eclipse com o Gradle não está a funcionar corretamente, as suas opções são usar a linha de comando apenas para compilação, ou migrar para o Android Studio. A última é a melhor opção visto que o plugin ADT foi depreciado recentemente.

O que quer que use, certifique-se apenas que o Gradle e a nova estrutura do projeto se mantêm como a forma oficial de compilar a sua aplicação, e evite adicionar os ficheiros com as configurações específicas do seu editor ao sistema de controlo de versão. Por exemplo, evite adicionar o ficheiro Ant `build.xml`. Especialmente não se esqueça de manter o `build.gradle` atualizado e em funcionamento se está a modificar as configurações de compilação do Ant. Além disso, seja gentil com outros programadores, não os force a alterar as suas ferramentas preferidas.

### Bibliotecas

**[Jackson](http://wiki.fasterxml.com/JacksonHome)** é uma biblioteca em Java para converter Objetos em JSON e vice-versa. [Gson](https://code.google.com/p/google-gson/) é uma escolha popular para resolver este problema, contudo nós achamos que o Jackson é mais eficiente visto que suporta maneiras alternativas de processar JSON: _streaming_, modelo de árvore em memória, e a vinculação tradicional JSON-POJO. Mantenha em mente, contudo, que o Jackson é uma biblioteca maior do que Gson, assim, dependendo do seu caso, pode preferir Gson para evitar a limitação dos 65 mil métodos. Outras alternativas: [Json-smart](https://code.google.com/p/json-smart/) e [Boon JSON](https://github.com/RichardHightower/boon/wiki/Boon-JSON-in-five-minutes)

**Trabalhos em rede, armazenamento em cache e imagens.** Há um par de soluções testadas exaustivamente no dia-a-dia para a realização de requisições para servidores, que você deverá utilizar antes de considerar implementar o seu próprio cliente. Use o [Volley](https://android.googlesource.com/platform/frameworks/volley) ou o [Retrofit](http://square.github.io/retrofit/). O Volley também fornece ajudantes para carregar e fazer o _cache_ de imagens. Se escolher o Retrofit, considere o [Picasso](http://square.github.io/picasso/) para carregar e fazer o _caching_ de imagens, e [OkHttp](http://square.github.io/okhttp/) para requisições eficientes de HTTP. Todos os três, Retrofit, Picasso e OkHttp foram criados pela mesma empresa, portanto eles complementam-se muito bem. [OkHttp pode também ser utilizado em conexão com o Volley](http://stackoverflow.com/questions/24375043/how-to-implement-android-volley-with-okhttp-2-0/24951835#24951835).

**RxJava** é uma biblioteca para Reactive Programming, por outras palavras, manipulação de eventos assíncronos. É um paradigma poderoso e promissor, o que pode tornar-se confuso devido ao fato de ser tão diferente. Recomendamos que tenha cuidado antes de usar esta biblioteca para arquitetar toda a sua aplicação. Há alguns projetos feitos por nós usando RxJava, se precisar de ajuda fale com uma destas pessoas:  Timo Tuominen, Olli Salonen, Andre Medeiros, Mark Voit, Antti Lammi, Vera Izrailit, Juha Ristolainen. Escrevemos também algumas postagens no blogue sobre ele: [[1]](http://blog.futurice.com/tech-pick-of-the-week-rx-for-net-and-rxjava-for-android), [[2]](http://blog.futurice.com/top-7-tips-for-rxjava-on-android), [[3]](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754), [[4]](http://blog.futurice.com/android-development-has-its-own-swift).

Se você não tiver qualquer experiência com Rx, comece por aplicá-lo apenas nas respostas da API. Alternativamente, comece aplicando-o para a simples manipulação de eventos UI, como eventos de clique ou eventos de digitação num campo de pesquisa. Se você está confiante nas capacidades de Rx e quer aplicá-lo em toda a arquitetura, então, escreva os Javadocs em todas as partes mais difíceis. Mantenha em mente que outro programador que não esteja familiarizado com o RxJava pode ter dificuldades em manter o projeto. Faça o seu melhor para ajudá-los a compreender o seu código e também Rx.

**[Retrolambda](https://github.com/evant/gradle-retrolambda)** é uma biblioteca em Java que permite o uso de expressões lambda em Android ou outras plataformas antes da JDK8. Ajuda a manter o código limpo e legível, especialmente se usa um estilo de programação funcional como pode exemplo o RxJava. Para o usar, instale a JDK8, defina-a como a sua localização SDK na janela da estrutura do projeto no Android Studio, defina as variáveis de ambiente `JAVA8_HOME` e `JAVA7_HOME`, e no ficheiro build.gradle que se encontra na raiz do projeto:

```groovy
dependencies {
    classpath 'me.tatarka:gradle-retrolambda:2.4.1'
}
```

e no ficheiro build.gradle de cada módulo, coloque

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

Após fazer isso, o Android Studio irá proporcionar suporte ao código que escreve para lambdas Java8. Se é novo no mundo dos lambdas, use o seguinte para começar:

- Qualquer interface com apenas um método é "amiga dos lambdas" e pode ser compressa na sintaxe mais compacta proporcionada pelos lambdas;
- Se está na dúvida sobre parâmetros e isso, escreva uma classe interna anónima normal e deixe o Android Studio a comprimir num lambda automaticamente.

**Esteja atento à limitação do número de métodos dex, e evite usar demasiadas bibliotecas.** As aplicações de Android, quando organizadas como um arquivo dex, têm um limite de 65536 métodos aos quais se podem fazer referências [[1]](https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71) [[2]](http://blog.persistent.info/2014/05/per-package-method-counts-for-androids.html) [[3]](http://jakewharton.com/play-services-is-a-monolith/). Durante a compilação irá encontrar um erro fatal se passar esse limite. Por essa razão, use o número mínimo de bibliotecas, e use a ferramenta [dex-method-counts](https://github.com/mihaip/dex-method-counts) para encontrar que bibliotecas usar para ficar abaixo desse limite. Especialmente evite usar a biblioteca Guava, visto que contém mais de 13 mil métodos.

### Atividades e Fragmentos

Não há um consenso dentro da comunidade nem entre os programadores da Futurice como melhor organizar a arquitetura do Android com Fragmentos e Atividades. O Square até tem
[uma biblioteca para construir uma arquitetura maioritariamente com Views](https://github.com/square/mortar), descartando a necessidade de Fragmentos, contudo isso ainda não é considerado uma boa prática dentro da comunidade.

Devido à história das APIs Android, pode de forma ligeira considerar Fragmentos como peças de UI no ecrã. Por outras palavras, Fragmentos estão normalmente relacionados com UI. Atividades podem ser ligeiramente consideradas controladores, eles são especialmente importantes devido ao seu ciclo de vida e para gerir estados. Contudo, não se surpreenda se vir variações nestes papéis: atividades podem tomar papéis na UI ([fazendo transições entre ecrãs](https://developer.android.com/about/versions/lollipop.html)), e [fragmentos podem ser usados apenas como controladores](http://developer.android.com/guide/components/fragments.html#AddingWithoutUI). Sugerimos que navegue com cuidado, tomando decisões informadas visto que há contrapartidas ao uso de arquiteturas apenas com fragmentos, ou apenas com atividades, ou apenas com views. Seguem alguns conselhos do que ter mais cuidado, mas leve-os com uma pitada de sal:

- Evite usar [fragmentos dentro de fragmentos](https://developer.android.com/about/versions/android-4.2.html#NestedFragments) em excesso, porque [matryoshka bugs](http://delyan.me/android-s-matryoshka-problem/) podem ocorrer. Use fragmentos dentro de fragmentos apenas quando isso faz sentido (por exemplo, fragmentos num ViewPager que desliza horizontalmente dentro de um fragmento tipo ecrã) ou se é uma decisão bem informada.

- Evite colocar demasiado código dentro das atividades. Sempre que possível, mantenha-as como contentores de peso leve, existindo primariamente na sua aplicação pelo ciclo de vida e outras interfaces importantes da API do Android. Dê preferência a atividades com apenas um fragmento ao invés de apenas atividades - coloque o seu código UI no fragmento da atividade. Isto irá fazer com que o código seja reutilizável no caso de por exemplo o querer modificar e colocar dentro de uma tabbed layout, ou num ecrã de tablete com múltiplos fragmentos. Evite ter uma atividade sem um fragmento correspondente, a não se que saiba o que está a fazer.

- Não abuse das APIs do Android, como pode exemplo colocar demasiado peso nos Intent para o funcionamento interno da sua aplicação. Isso pode afetar o sistema operativo do Android e outras aplicações, criando bugs ou lag. Por exemplo, é um fato que se utilizar Intents para a comunicação interna na sua aplicação entre os packages, poderá fazer com que exista um lag de alguns segundos na experiência do utilizador se a app foi aberta depois do sistema operativo tiver sido iniciado.

### Arquitetura dos pacotes de Java

A arquitetura do Java para aplicações Android pode ser mais ou menos aproximada ao modelo [Model-View-Controller](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller). No Android, [Fragmentos e Atividades são na verdade classes de controlo](http://www.informit.com/articles/article.aspx?p=2126865). Por outro lado, elas são parte explícita da interface do utilizador, portanto são também views.

Por este motivo, é difícil classificar fragmentos (ou atividades) como restritamente controladores ou views. É melhor as deixa ficar no seu próprio pacote `fragments`. Atividades podem ficar no pacote mais algo desde que sigam os conselhos da secção anterior. Se está a planear ter mais do que 2 ou 3 atividades, então crie também um pacote `activities`.

Caso contrário, a arquitetura pode ser semelhante ao típico MVS, com um pacote `models` contendo POJOs que serão populados através de JSON parser com respostas à API, e um pacote `views` contendo as Views criadas por si, notificações, views da action bar, widgets, etc. Adapters são uma matéria cinzenta, estando entre os dados e as views. Contudo, eles tipicamente precisam de exportar alguma View através do `getView()`, portanto poderá incluir o sub-pacote `adapters` dentro de `views`.

Algumas classes de controladores são para toda a aplicação e perto do sistema Android. Estas podem estar no pacote `managers`. Classes de processamento de dados várias, como "DateUtils", ficam dentro do pacote `utils`. Classes têm a responsabilidade de interagir com o servidor ficam no pacote `network`.

No final de contas, organizadas do mais perto do servidor para o mais perto do utilizador:

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

### Recursos

**Dar o nome.** Siga a convenção de colocar o prefixo como tipo, por exemplo `type_foo_bar.xml`. Exemplos:  `fragment_contact_details.xml`, `view_primary_button.xml`, `activity_main.xml`.

**Organizar os layout XMLs.** Se não tem a certeza de como formatar o layout XML, as seguintes convenções podem ajudar:

- Um atributo por linha, indentação de 4 espaços
- `android:id` sempre como o primeiro atributo
- `android:layout_****` atributo no topo
- `style` atributo no fundo
- A Tag de fechar `/>` na sua própria linha, para facilitar a ordenação e adicionar novos atributos.
- Ao invés de colocar o texto diretamente em `android:text`, considere usar os [atributos designtime](http://tools.android.com/tips/layout-designtime-attributes) disponíveis para Android Studio.

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

Regra geral, atributos `android:layout_****` devem ser definidos no layout XML, ao passo que outros atributos `android:****` devem ficar num XML de estilo. Esta regra tem excepções, mas de forma geral funciona bem. A ideia é manter apenas o layout (posicionamento, margem, tamanho) e atributos de conteúdo nos ficheiros de layout, mantendo todos os detalhes de aparência (cores, espaçamento, tipo de letra) em ficheiros de estilo.

As excepções são:

- `android:id` deverá ficar obviamente no ficheiro de layout
- `android:orientation` para um `LinearLayout` normalmente faz mais sentido num ficheiro de layout
- `android:text` deverá ficar no ficheiro de layout porque ele define conteúdo
- Por vezes irá fazer sentido definir um estilo genérico `android:layout_width` e `android:layout_height` mas por definição estes deverão aparecer no ficheiro de layout

**Use estilos.** Quase todos os projeto precisam de usar estilos de forma adequada, isto porque é muito comum ter de repetir a aparência de uma view. No mínimo deverá ter um estilo comum para a maioria do conteúdo de texto na sua aplicação, por exemplo:

```xml
<style name="ContentText">
    <item name="android:textSize>@dimen/font_normal</item>
    <item name="android:textColor">@color/basic_black</item>
</style>
```

Aplicado às TextViews:

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/price"
    style="@style/ContentText"
    />
```

Provavelmente irá ter de fazer o mesmo para botões, mas não pare já por aqui. Vá mais além e mova um grupo de atributos `android:****` que estejam relacionados e sejam repetidos para um estilo comum.

**Divida um ficheiro de estilo grande em outros ficheiros.** Não precisa de ter apenas um ficheiro `styles.xml`. A Android SDK suporta outros ficheiros, e não há nada de mágico acerca do nome `styles`, o que importa são as tags de XML `<style>` dentro do ficheiro. Assim poderá ter os ficheiros `styles.xml`, `styles_home.xml`, `styles_item_details.xml`, `styles_forms.xml`. Ao contrário dos nomes dos diretório de recursos que têm algum significado para o sistema de compilação, nomes de ficheiros dentro de `res/values` podem ser arbitrários. 

**`colors.xml` é uma palete de cores.** Não deverá existir mais nada no seu `colors.xml` do que apenas um mapeamento do nome da cor a um valor RGBA. Não o use para definir valores RGBA para diferentes tipos de botões.

*Não faça isto:*

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

Pode facilmente começar a repetir valores RGBA desta forma, e isso faz com que seja mais difícil alterar uma cor básica se isso for necessário. Além disso, essas definições estão relacionadas a algum contexto, como pode exemplo "button" ou "comment", e devem ficar no estilo do botão, não em `colors.xml`.

Ao invés, faça isto:

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

Peça esta palete ao designer da aplicação. Os nomes não precisam de ser nomes de cores como "green", "blue", etc. Nomes como "brand_primary", "brand_secondary", "brand_negative" são também totalmente aceites. Formatar as cores desta forma irá fazer com que as alterar ou fazer a refatorização de cores seja mais fácil, e também irá mostrar de forma explícita as diferentes cores que estão a ser utilizadas. Normalmente para um UI com bom aspeto, é importante reduzir a variedade das cores que estão a ser utilizadas.

**Trate o dimens.xml como o colors.xml.** Deverá também definir uma "palete" de espaçamentos e tamanhos de letra típicos, devido basicamente às mesmas razões que para as cores. Um bom exemplo de um ficheiro dimens:

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

Nas margens e espaçamentos deverá usar as dimensões `spacing_****` para o layout, ao invés de colocar valores fixos, da mesma forma que o strings são tratados. Isto irá dar um look-and-feel consistente, ao mesmo tempo que irá fazer com que seja mais fácil organizar e modificar estilos e layouts.

**strings.xml**

Dê o nome aos strings com keys que fazem lembrar namespaces, e não tenha receio de repetir um valor para duas ou mais keys. Linguagens são complexas, portanto namespaces são necessários para trazer contexto e quebrar ambiguidade.

**Ruim*
```xml
<string name="network_error">Erro de rede</string>
<string name="call_failed">Pedido falhou</string>
<string name="map_failed">Carregamento do mapa falhou</string>
```

**Bom**
```xml
<string name="error.message.network">Erro de rede</string>
<string name="error.message.call">Pedido falhou</string>
<string name="error.message.map">Carregamento do mapa falhou</string>
```

Não escreva os valores do strings em letras maiúsculas. Mantenha-se dentro das convenções normais de texto (exemplo: coloque o primeiro caráter em letras maiúscula). Se precisa de mostrar o string em letras maiúsculas, então use por exemplo o atributo [`textAllCaps`](http://developer.android.com/reference/android/widget/TextView.html#attr_android:textAllCaps) na própria TextView.

**Ruim**
```xml
<string name="error.message.call">PEDIDO FALHOU</string>
```

**Bom**
```xml
<string name="error.message.call">Pedido falhou</string>
```

**Evite uma hierarquia muito profunda de views.** Por vezes poderá ser tentado a adicionar apenas mais uma LinearLayout, para conseguir organizar as suas views. Este tipo de situação pode ocorrer:

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

Mesmo que não veja isso explicitamente num ficheiro de layout, poderá na mesma acontecer se está a inflando (em Java) views noutras views.

Um par de problemas pode ocorrer. Poderá experimentar problemas de performance, porque há uma árvore complexa de UI que o processador precisa de gerir. Outro problema mais sério é a possibilidade de [StackOverflowError](http://stackoverflow.com/questions/2762924/java-lang-stackoverflow-error-suspected-too-many-views).

Por isso, tente manter a hierarquia das views tão lisa quanto possível: aprenda como usar [RelativeLayout](https://developer.android.com/guide/topics/ui/layout/relative.html), como [optimizar os seus layouts](http://developer.android.com/training/improving-layouts/optimizing-layout.html) e como usar a [tag `<merge>`](http://stackoverflow.com/questions/8834898/what-is-the-purpose-of-androids-merge-tag-in-xml-layouts).

**Esteja atento a problemas relacionados com WebViews.** Quando tem de mostrar uma página web, por exemplo para uma notícia, evite usar o processamento no cliente para limpar o HTML, ao invés, peça um HTML "*puro*" aos programadores do servidor. [WebViews também podem vazar memória](http://stackoverflow.com/questions/3130654/memory-leak-in-webview) quando mantêm uma referência à sua Activity, ao invés de conetada ao ApplicationContext. Evite usar uma WebView para textos ou botões simples, dê preferência às TextViews ou Buttons.

### Frameworks para testes

As frameworks de teste do Android SDK ainda estão na sua infância, especialmente no que toca aos testes UI. O Android Gradle currentemente implementa uma tarefa de teste chamada [`connectedAndroidTest`](http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Testing), a qual corre testes JUnit criados por si, usando uma extensão de JUnit com ajudantes para Android](http://developer.android.com/reference/android/test/package-summary.html). Isto significa que irá ter de correr os testes conectado a um aparelho, ou um simulador. Siga o guia oficial [[1]](http://developer.android.com/tools/testing/testing_android.html) [[2]](http://developer.android.com/tools/testing/activity_test.html) para testar

**Use [Robolectric](http://robolectric.org/) apenas para os testes unitários, não para as views.**
É uma framework de teste que procura testar de forma "desconectada de um aparelho" para melhorar a velocidade, desenhado especialmente para os testes unitários aos modelos e _view models_. Contudo, testar sobe o Robolectric é pouco fiável e incompleto no que toca a testes UI. Irá ter problemas a testar elementos UI relacionados com animações, janelas de diálogo, etc., e isto irá ainda ficar mais complicado porque está a "caminhar no escuro" (testando sem ver o ecrã que está a ser controlado).

**[Robotium](https://code.google.com/p/robotium/) faz com que escrever testes UI seja fácil.** Não precisa do Robotium para correr testes conectados em casos de UI, mas irá provavelmente ser benéfico para si porque ele tem muitos ajudantes para obter e analisar as views, e controlar o ecrã. Casos de teste irão ser tão simples como:

```java
solo.sendKey(Solo.MENU);
solo.clickOnText("More"); // searches for the first occurence of "More" and clicks on it
solo.clickOnText("Preferences");
solo.clickOnText("Edit File Extensions");
Assert.assertTrue(solo.searchText("rtf"));
```

### Emuladores

A performance do emulador do Android SDK, particularmente a de arquitetura x86, tem melhorado significantemente nos últimos anos e hoje é a mais adequada para a maioria das tarefas de desenvolvimento do dia-a-dia. Contudo, você não deve desconsiderar que sua aplicação vai funcionar realmente em dispositivos reais. Claro, testar todos os possíveis dispositivos não é nada prático ou produtivo, ou seja, foque os seus esforços nos dispositivos que tenham uma grande fatia de mercado e os que sejam mais relevantes para a aplicação que está sendo desenvolvida no momento.

### Configurações Proguard

[ProGuard](http://proguard.sourceforge.net/) é normalmente usado em projetos Android para diminuir ou ofuscar o código empacotado.

Quer esteja a usar o ProGuard que não depende da configuração do seu projeto. Frequentemente irá configurar o gradle para usar ProGuard quando compilando a apk de lançamento.

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
De maneira a determinar que partes do código têm de ser preservadas e quais podem ser descartadas ou ofuscadas, terá de especificar um ou mais pontos de entrada para o seu código. Estes pontes de entrada são normalmente classes com main métodos, applets, midlets, atividades, etc.
A framework Android usa uma configuração padrão que pode ser encontrada em `SDK_HOME/tools/proguard/proguard-android.txt`. Usando a configuração acima, regras ProGuard específicas para o projeto, definidas em `my-project/app/proguard-rules.pro`, serão anexadas à configuração padrão.

Um problema comum relacionado com o ProGuard é verificar se a aplicação tem um crash ao iniciar com `ClassNotFoundException` ou `NoSuchFieldException` ou similar, mesmo que o comando de compilação (i.e. `assembleRelease`) seja bem sucedido sem qualquer aviso.

Isto significa uma de duas coisas:

1. ProGuard removou a classe, enum, método, nome do campo ou anotação, considerando que esta não era necessária.
2. ProGuard ofuscou (deu outro nome) à classe, enum ou nome do campo, mas continua a ser usada indiretamente pelo seu nome original, i.e. através da reflexão do Java.

Verifique `app/build/outputs/proguard/release/usage.txt` para ver se o objeto em questão foi removido.
Verifique `app/build/outputs/proguard/release/mapping.txt` para ver se o objeto em questão foi ofuscado.

De maneira a prevenir o ProGuard de *remover* classes ou membros da classe que sejam necessários, adicione a opção `keep` à sua configuração do ProGuard:

```
-keep class com.futurice.project.MyClass { *; }
```

De maneira a prevenir o ProGuard de *ofuscar* classes ou membros da classe, adicione `keepnames` 

```
-keepnames class com.futurice.project.MyClass { *; }
```

Leia mais em [Proguard](http://proguard.sourceforge.net/#manual/examples.html) para exemplos.

**Cedo no projeto, faça uma compilação de lançamento** para verificar se as regras do ProGuard estão a manter corretamente o que é importante. Além disso, sempre que inclua bibliotecas novas, faça uma compilação de lançamento e teste a apk num aparelho. Não espera até a sua aplicação estar finalmente na versão "1.0" para fazer a compilação de lançamento, poderá encontrar algumas surpresas desagradáveis e pouco tempo para as resolver.

**Dica.** Guarde o ficheiro `mapping.txt` por cada lançamento que publique para os seus utilizadores. Ao guardar uma cópia do ficheiro `mapping.txt` por cada compilação de lançamento, assegura-se de que poderá fazer o debug de um problema se o utilizador encontrar um bug e submeter um stack trace ofuscado.

**DexGuard**. Se necessita de ferramentas hard-core para optimizar, e especialmente ofuscar código de lançamento, considere o [DexGuard](http://www.saikoa.com/dexguard), um software comercial feito pela mesma equipa do ProGuard. Também poderá dividir ficheiros Dex para resolver o problema da limitação dos 65 mil métodos.

### Armazenamento de dados

#### SharedPreferences

Se precisa de persistir apenas flags simples e a sua aplicação corre em apenas um processo, SharedPreferences é muito provavelmente suficiente para si. É uma boa opção padrão.

Há duas razões que o podem levar a não querer usar SharedPreferences:

* *Performance*: Os seus dados são complexos ou simplesmente há muitos dados a serem guardados
* *Vários processos querendo aceder aos dados*: Você tem widgets ou serviços remotos que são executados em seus próprios processos e exigem dados sincronizados

#### ContentProviders

No caso de que as SharedPreferences não são suficientes para si, deverá usar 
o padrão da plataforma ContentProviders, que são rápidos e seguros no uso de processos.

O único problema com ContentProviders é a quantidade de código boilerplate que é necessário para configurá-los, bem como tutoriais de baixa qualidade. É possível, contudo, gerar ContentProvider utilizando bibliotecas como [Schematic](https://github.com/SimonVT/schematic), que reduz significativamente o esforço.


Você ainda precisa escrever algum código de parsing você mesmo para ler os objetos de dados das colunas do SQLite e vice-versa. É possível serializar os objetos de dados, por exemplo, com Gson, e fazer persistir apenas a string resultante. Desta forma, você perde em performance, mas por outro lado não precisa de declarar uma coluna para cada um dos campos da classe de dados.

#### Usando um ORM

Nós geralmente não recomendamos o uso de uma biblioteca de Object-Relation Mapping, a não ser que tenha dados invulgarmente complexos e uma necessidade extrema. Eles tendem a ser complexos e necessitam de tempo para aprender. Se decidir avançar com um ORM deverá prestar atenção para saber se é ou não é _seguro em processos_ se a sua aplicação o exige, visto que muitas das soluções ORM existentes neste momento surpreendentemente não o são.

### Obrigado a

Antti Lammi, Joni Karppinen, Peter Tackage, Timo Tuominen, Vera Izrailit, Vihtori Mäntylä, Mark Voit, Andre Medeiros, Paul Houghton e outros programadores da Futurice por partilharem o seu conhecimento em desenvolvimento Android.

### License

[Futurice Oy](http://www.futurice.com)
Creative Commons Attribution 4.0 International (CC BY 4.0)
