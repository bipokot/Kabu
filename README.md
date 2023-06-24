
# Kabu

##  Overview
**Kabu** is an annotation processor for Kotlin which embraces a declarative way of DSLs creation. The processor allows splitting visual representation of DSL from its functionality.

```kotlin
// Example-001

@GlobalPattern("The.Declarative[!way] /= to * create { +{ a > DSL } } - message")
fun motto(message: String) = println(message)

fun main() {
    The.Declarative[!way] /= to * create { +{ a > DSL } } - "👍"
    // prints "👍"
}
```

Annotate a function with `@GlobalPattern` annotation and set a desired pattern for that function. The processor will generate code so that any expression matching the pattern will call the function with actual arguments used in that expression.

Think of a pattern as a *visual decoration* of arguments which will be passed to annotated function.
Patterns can be almost as complex as you can do it with Kotlin.

## Examples
Explore [a project with a set of examples](doc/exampleProject.md). Each documentation example with `Example-XXX` code can be found in that project.

Feel free to experiment with patterns and have fun!
           
### Some of the examples 
<details>
<summary>Hello, World!</summary>

```kotlin
// Example-000

@GlobalPattern("hello")
fun helloWorld() {
    println("Hello, World!")
}

fun main() {
    hello

    /* Prints:
    Hello, World!
     */
}
```

</details>

<details>
<summary>Book</summary>

```kotlin
// Example-014

@GlobalPattern("print book name[author / year] .. description")
fun printBook(name: String, description: String, year: Int, author: String) {
    println("'$name' by $author ($year)\n'$description'")
}

fun main() {
    print book "About Nothing"["Smart Person" / 2011].."The best book in the world"

    /* Prints:
    'About Nothing' by Smart Person (2011)
    'The best book in the world'
     */
}
```

</details>

<details>
<summary>Repeat string</summary>

```kotlin
// Example-002

@GlobalPattern("string * count")
fun repeatString(count: Int, string: String) = buildString {
    repeat(count) { append(string) }
}

fun main() {
    println("abc" * 3)

    /* Prints:
    abcabcabc
    */
}
```

</details>

<details>
<summary>Functional parameters</summary>

```kotlin
// Example-006

@GlobalPattern("block onlyIf condition")
fun onlyIf(block: () -> String, condition: Boolean) = if (condition) block() else null

fun main() {
    println({ println("evaluating 'abc'"); "abc" } onlyIf true)
    println({ println("evaluating 'def'"); "def" } onlyIf false)
    
    /* Prints:
    evaluating 'abc'
    abc
    null
    */
}
```

</details>

<details>
<summary>Transactions (actual used operator feature)</summary>

```kotlin
// Example-005

data class User(
    var balance: Int
)

@GlobalPattern("send[amount] { user1 > user2 }")
fun transaction(amount: Int, user1: User, rank: RankingComparisonInfo, user2: User) {

    fun moveMoney(amount: Int, from: User, to: User) {
        // dumb transaction implementation, don't try this at work
        from.balance -= amount
        to.balance += amount
    }

    when (rank) {
        GREATER -> moveMoney(amount, from = user1, to = user2)
        LESS -> moveMoney(amount, from = user2, to = user1)
    }
}


fun main() {
    val alice = User(1000)
    val bob = User(800)
    
    println("Alice: $alice")
    println("Bob: $bob")

    send[200] { alice > bob }
    send[100] { alice < bob }

    println("Alice: $alice")
    println("Bob: $bob")

    /* Prints:
    Alice: User(balance=1000)
    Bob: User(balance=800)
    Alice: User(balance=900)
    Bob: User(balance=900)
     */
}
```

</details>

<details>
<summary>Football team (pattern extension)</summary>

```kotlin
// Example-007

data class Player(
    val name: String,
    val number: Int
)

data class Trophy(
    val name: String,
    val times: Int
)

data class FootballTeam(
    val name: String,
    val isChampion: Boolean,
    val players: List<Player>,
    val trophies: List<Trophy>
)

class PlayersBuilder @ContextCreator("playersBuilder") constructor() {
    val players = mutableListOf<Player>()

    @LocalPattern("name - number")
    fun addPlayer(name: String, number: Int) {
        players.add(Player(name, number))
    }
}

class FootballTeamBuilder @ContextCreator("footballTeamBuilder") constructor() {

    val trophies = mutableListOf<Trophy>()
    var isChampion = false
    val players = mutableListOf<Player>()

    @LocalPattern("champion")
    fun champion() {
        isChampion = true
    }

    @LocalPattern("!number - trophy")
    fun addTrophy(trophy: String, number: Int) {
        trophies.add(Trophy(trophy, number))
    }

    @LocalPattern("has outstanding players @Extend(context = playersBuilder(), parameter = builder) {}")
    fun addPlayers(builder: PlayersBuilder) {
        players.addAll(builder.players)
    }
}

@GlobalPattern("football team name @Extend(context = footballTeamBuilder(), parameter = builder) {}")
fun footballTeam(name: String, builder: FootballTeamBuilder): FootballTeam {
    return FootballTeam(name, builder.isChampion, builder.players, builder.trophies)
}

fun main() {
    val team = football team "Tigers" {
        champion
        
        !3 - "Super League Champions"
        !26 - "National League"

        has outstanding players {
            "John Smith" - 10
            "Enrique Hernandez" - 2
        }
    }
    println(team)

    /* Prints:
    FootballTeam(name=Tigers, isChampion=true, players=[Player(name=John Smith, number=10), Player(name=Enrique Hernandez, number=2)], trophies=[Trophy(name=Super League Champions, times=3), Trophy(name=National League, times=26)])
     */
}
```

</details>

<details>
<summary>Alternative if-else (pattern extension)</summary>

```kotlin
// Example-009

class Actions @ContextCreator("actions") constructor() {
    val trueActions = mutableListOf<() -> Unit>()
    val falseActions = mutableListOf<() -> Unit>()

    @LocalPattern("+action")
    fun addTrueAction(action: () -> Unit) {
        trueActions += action
    }

    @LocalPattern("-action")
    fun addFalseAction(action: () -> Unit) {
        falseActions += action
    }
}

@GlobalPattern("condition @Extend(context = actions(), parameter = actions) {}")
fun ifElse(condition: Boolean, actions: Actions) {
    val actionsToExecute = if (condition) actions.trueActions else actions.falseActions
    actionsToExecute.forEach { it() }
}

fun main() {
    val isOperationSuccessful = false
    isOperationSuccessful {
        + { println("show message 'Success'") }
        - { println("show message 'Failure'") }

        + { println("activate button 'Close'") }
        - { println("activate button 'Repeat'") }
    }

    /* Prints:
    show message 'Failure'
    activate button 'Repeat'
     */
}
```

</details>

<details>
<summary>Alphabet</summary>

```kotlin
// Example-019

@GlobalPattern("a{{ b ..< c } !in -d[e, +-{f}[g][{{{}..{h.i = j}}}], k(l){ m += n} + !{o * -p(q {r[s.t.u] = v w x})}]} / y + z")
fun alphabet(
    b: Int,
    inclusion: InclusionInfo,
    e: String,
    g: Int,
    j: String,
    m: Int,
    r: Int,
    s: Int,
    v: String,
    z: Int,
) {
    listOf(b, inclusion, e, g, j, m, r, s, v, z).forEach(::println)
}

fun main() {
    a{{ 2 ..< c } !in -d["3", +-{f}[5][{{{}..{h.i = "7"}}}], k(l){ 11 += n } + !{o * -p(q {13[17.t.u] = "19" w x})}]} / y + 23

    /* Prints:
    2
    NOT_IN
    3
    5
    7
    11
    13
    17
    19
    23
     */
}
```

</details>

<details>
<summary>Gibberish</summary>

```kotlin
// Example-018

@GlobalPattern("ᘤ [ᘎ, a, +ᒣ {!b{ᐳƧ}*c}] - ϾϿ(-d[Ⲷ]){ e Ⴖ Ϟ % ᘃ(ᗏ) ᗊ -ᓬ[f] }")
fun gibberish(a: String, b: String, c: String, d: String, e: String, f: String) {
    println(a + b + c + d + e + f)
}

fun main() {
    ᘤ [ᘎ, "K", +ᒣ {!"o"{ᐳƧ}*"t"}] - ϾϿ(-"l"[Ⲷ]){ "i" Ⴖ Ϟ % ᘃ(ᗏ) ᗊ -ᓬ["n"] }

    /* Prints:
    Kotlin
     */
}
```

</details>

## Documentation

### Terminology
- `pattern` - a string which defines how an expression must look like. [Pattern syntax](doc/patternSyntax.md) generally corresponds to a Kotlin *statement* syntax.
- `target function` - function annotated with one of the pattern annotations (`@GlobalPattern`/`@LocalPattern`), which is to be called when expression matching to the pattern is evaluated
	- `global target function` - a target function annotated with `@GlobalPattern` annotation
	- `local target function` - a target function annotated with `@LocalPattern` annotation
- `termination` - gathering all required arguments and calling a target function
- `inferrable lambda` - a lambda which exact type can be inferred by a compiler. Usually this means that lambda must not be a first evaluated argument of an operation.

### Details
- [Pattern syntax](doc/patternSyntax.md) covers features of patterns of `@GlobalPattern` and `@LocalPattern` annotations
- [Target functions](doc/targetFunctions.md) covers supported features of target functions (scope, modifiers, parameters, etc.)
- [Pattern extension](doc/patternExtension.md) describes how to make extension points in your patterns
- [Unsafe features](doc/unsafe.md) explains why they called "unsafe"

## Features

### Pattern extensibility
> See [documentation on pattern extending](doc/patternExtension.md)

Plain simple pattern acts as a comprehensive template, which defines more or less *fixed* structure for future runtime expressions. [Pattern syntax](doc/patternSyntax.md) supports *extension points* in order to express more complex DSLs.

An *extension point* defines a lambda based scope in which multiple operations from some limited set of allowed operations can be included. These allowed operations are defined by annotating functions of *context class* with `@LocalPattern` annotation.

```kotlin
// Example-009

// context class
class Actions @ContextCreator("actions") constructor() {
    val trueActions = mutableListOf<() -> Unit>()
    val falseActions = mutableListOf<() -> Unit>()

    @LocalPattern("+action")
    fun addTrueAction(action: () -> Unit) {
        trueActions += action
    }

    @LocalPattern("-action")
    fun addFalseAction(action: () -> Unit) {
        falseActions += action
    }
}

@GlobalPattern("condition @Extend(context = actions(), parameter = actions) {}")
fun ifElse(condition: Boolean, actions: Actions) {
    val actionsToExecute = if (condition) {
        actions.trueActions
    } else {
        actions.falseActions
    }
    actionsToExecute.forEach { it() }
}

fun main() {
    val isOperationSuccessful = false
    isOperationSuccessful {
        + { println("show message 'Success'") }
        - { println("show message 'Failure'") }

        + { println("activate button 'Close'") }
        - { println("activate button 'Repeat'") }
    }
}
```

### Retrieving actual used operator (comparison/inclusion)
>This feature is *unsafe*. See [unsafe features](doc/unsafe.md).

Required conditions:
- check (comparison/inclusion) must be in an *inferrable lambda* (at least transitively) of a pattern
- order of [target function](doc/targetFunctions.md) parameters must match the order in which these parameters appear in a pattern
- parameter of type `OperatorInfo` must be included *between* corresponding parameters

You can use comparison/inclusion operators in any combination and get as many `OperatorInfo` parameters as you wish.

#### Comparison check
For example `transaction { bob > alice }` can be distinguished from `transaction { bob < alice }` (note the change in comparison operator).

The information of comparison operator used can be obtained in one of two possible ways:
- You can check whether `<`/`<=` was used or `>`/`>=`. To do that, put the parameter of type `RankingComparisonInfo` between corresponding parameters in a function signature. It will receive the information about actually used operator.
- You can check whether `<`/`>` was used or `<=`/`>=`. To do that, put the parameter of type `StrictnessComparisonInfo` between corresponding parameters in a function signature. It will receive the information about actually used operator.

Restrictions:
- if there is
	- more than one comparison operations for the same types in a pattern
	- these comparison operations are in the same *inferred lambda*
- then
	- you can't mix `RankingComparisonInfo` and `StrictnessComparisonInfo` parameters for these operations in order to get actual used operator info

#### Inclusion check
For example `groups { user in "admins" }` can be distinguished from `groups { user !in "admins" }` (note the negation of `in` operator).
Put the parameter of type `InclusionInfo` between corresponding parameters in a function signature to receive the information about actually used inclusion operator (`in` or `!in`).

### Synthetic properties
All identifiers defined in a pattern which have no match with one of [target function](doc/targetFunctions.md) parameters will be created as synthetic properties to support the pattern.

See `Example-000`, `Example-001`, `Example-003`, `Example-004` etc.

### Minimum pollution principle
All generated declarations are placed in the most narrow scope (package, class, nested classes) to reduce conflicts probability and to maintain your namespaces as clean as possible.

See `Example-016`.

### Conflict detection
The processor is capable of detecting conflicts between parts of generated code (and resolving them in simple cases) as well as between generated code and Kotlin stdlib (in simple cases). Conflict detection between generated code and user code is not implemented yet.
                    
```kotlin
// Example-016

// declarations inside inferred lambda go into its scope (scope narrowing)
@GlobalPattern("name * age - { occupation * income }")
fun printPersonInfo(occupation: String, income: Int, name: String, age: Int) {
    println("Person '$name'($age) is '$occupation'($income X)")
}

// declarations go into one shared scope
@GlobalPattern("name % age - occupation % income")
fun printPersonInfo2(occupation: String, income: Int, name: String, age: Int) {
    println("Person '$name'($age) is '$occupation'($income X)")
}

fun main() {
    // below are two equal expressions (with '*' syntax):
    "Adam" * 20 - { "Physicist" * 1000 }
    "Adam".times(age = 20) - { "Physicist".times(income = 1000) } // parameter names are conserved as conflict was avoided

    // below are two equal expressions (with '%' syntax):
    "Adam" % 20 - "Physicist" % 1000
    "Adam".rem(int = 20) - "Physicist".rem(int = 1000) // parameter names aren't conserved as conflict was resolved
}
```

### Propagation of user given names
[Target function's](doc/targetFunctions.md) parameter names are propagated to corresponding generated declarations. In case of possible conflict resolution those user given names may not be preserved.

See `Example-016`.

### Implementation details hiding
Most implementation details (fields of holder classes, internal structure of context delegate class, etc.) are hidden from inadvertent usage.

## Installation

### Install KSP plugin
Make sure you have KSP plugin (`com.google.devtools.ksp`) available in your project. The KSP plugin version (`kspVersion`) must match Kotlin version used in your project.

**settings.gradle**

```gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
```

**build.gradle**
```gradle
plugins {
    id "com.google.devtools.ksp" version "$kspVersion"
}

sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
    java.srcDirs("build/generated/ksp/main/java")
}
```

### Install Kabu
Add following maven repository:

```gradle
maven {
    url = "http://80.240.29.205:45011/artifactory/libs-release-local"
    allowInsecureProtocol = true
}
```

Add following dependencies to your project:

```gradle
def kabuVersion = '0.20.0'

dependencies {
    ksp "io.kabu:processor:$kabuVersion"
    compileOnly "io.kabu:annotations:$kabuVersion"

    implementation "io.kabu:runtime:$kabuVersion
}
```

#### Processor options
Optional preferences can be set in a `build.gradle` file:

```gradle
ksp {
    arg("ksp.io.kabu.allowUnsafe", "true")
}
```

Available options:
- `ksp.io.kabu.allowUnsafe`
	- "true" - *unsafe* features of patterns are enabled
	- "false" - *unsafe* features of patterns are disabled

## Diagnostics
There are cases when some patterns can not be implemented for you by the processor. Generally this could happen due to several reasons:
- syntactically incorrect pattern. If pattern does not follow [pattern syntax rules](doc/patternSyntax.md), the processor can not do anything with it - the runtime expression won't work anyway
- [target functions](doc/targetFunctions.md) with unsupported properties (e.g. `@LocalPattern` on a top level function)
- there are some conflicts with already existing declarations. For example one can not override an already defined member function/property of some type (Kotlin stdlib type or user type)

Basically you should get a diagnostic message containing the location(s) of code which caused the error. If not - please file an issue for that.
