
# Extending a pattern
All patterns (local and global) can be extended. To extend a pattern you need to define one or more *extension points* in the pattern.

## Extension point
*Extension point* is a `@Extend`-annotated empty lambda placed somewhere in a pattern. It serves 2 purposes:
- it defines a place inside the pattern where the extension will be made
	- *"Where we want to extend the pattern"*
- it binds itself with required components via parameters of its `@Extend` annotation

Those required components are:
- *Context class*
	- *"Which operations will extend the pattern?"*
- *Context creator(s)*
	- *"How we want to create the context class?"*

###  Context name
The binding between an extension point and context class is done by a *context name*. *Context name* is an identifier-like string used to search for context creators and context classes for particular extension point.

### Restrictions
The lambda of extension point must be *inferrable*.
In other words the lambda must be placed in such a position that will allow compiler to infer its exact type (generally speaking, the lambda must not appear as a first evaluated argument of expression).

### `@Extend` annotation parameters

#### `context`
`context=<CONTEXT_NAME>(<PARAMETERS>)` - where `<CONTEXT_NAME>` is the chosen name for context. `<PARAMETERS>` defines a comma separated list of parameters which will be passed to a chosen context creator.
Each parameter must be one of the [target function](targetFunctions.md) parameter names (including `this` if receiver is used). If detected that one or more of chosen parameters will be unknown by the moment of context creator invocation, you will get a diagnostic message with a list of parameters available as context creator parameters for the extension point.
In that way we can parameterize a context class instantiation with one or more runtime values. `Example-015` demonstrates context parametrization.

**Context creator dispatching**

The processor will search for accessible context creators with designated *context name* and will choose the only appropriate context creator with matching parameter types list.
An error occurs if there are no applicable context creators or there are more than one of them.

#### `parameter`
`parameter=<PARAMETER_NAME>` - where `<PARAMETER_NAME>` defines a parameter of the [target function](targetFunctions.md), which will receive an instance of context class associated with the extension point. In other words, we can access context class data that was created/modified during the lambda invocation. This functionality may be used in builder-style DSLs.

#### `result` (not yet supported)
`result=<PARAMETER_NAME>` - where `<PARAMETER_NAME>` defines a parameter of the [target function](targetFunctions.md), which will receive a returned value of the lambda invocation.

## Context class
Context class defines a scope for local [target functions](targetFunctions.md) (`@LocalPattern` functions). Context class must be a non-abstract top level class. A context class may be used for different extension points.

Member functions of a context class which are annotated with `@LocalPattern` annotation constitute a set of allowed operations inside a lambda. Statements corresponding to patterns of those local [target functions](targetFunctions.md) will be the only accessible way to interact with an instance of the context class.

## Context creator(s)
Context creator is a function (or constructor) that creates an instance of context class. A context creator may be used for different extension points. One context class may have several context creators with different parameters lists.
A context creator and an extension point associated with it must share the same *context name*.

### Restrictions
Context creator:
- must be one of the following:
	- a top level function returning an instance of corresponding context class
	- a constructor of corresponding context class
- must have `public` or `internal` visibility
- must be annotated with `@ContextCreator` annotation
- may have parameters

## Unbounded and recursive extensions nesting
Local patterns can have their own extension points, so building complex DSLs with arbitrary depth of extensions nesting is possible.
Recursive (and transitively recursive) extensions nesting is possible too because local pattens can have extension points bound to the same context class.

```kotlin
// Example-008

jsonObject {
    "first" - jsonObject {
        "inner" - 101
    }
    "second" - jsonArray {
        +"a"
        +jsonArray {
            +"1"
            +jsonObject {
                "deepest" - 1
                "object" - 2
            }
        }
        +"c"
    }
}
```

## Steps to define an extension point
1. Choose a *context name* (following the rules for identifier name). It may relate to a context class name.
2. Create a *context class* containing functions marked with `@LocalPattern`, which constitute a set of allowed operations inside a lambda.
3. Define at least one way to create the *context class*. Mark it with a `@ContextCreator` annotation and specify chosen *context name*.
4. Mark an empty lambda with `@Extend` annotation inside a pattern. Specify required parameters of the annotation.

### Example
```kotlin
class Builder @ContextCreator("ctx") constructor() {

    val actions = mutableListOf<() -> Unit>()

    @LocalPattern("+action")
    fun addAction(action: () -> Unit) {
        actions += action
    }
}

@Pattern("foo @Extend(context = ctx(), parameter = builder) {}")
fun func(builder: Builder) {
    builder.actions.forEach { it() }
}

fun main() {
    foo {
        + { println("action 1") }
        + { println("action 2") }
        + { println("action 3") }
    }
}
```

Here, the pattern `foo @Extend(context = ctx(), parameter = builder) {}` defines an extension point `@Extend(context = ctx(), parameter = builder) {}`:
- `context = ctx()` - means that no-argument function annotated as creator of "ctx"-named context will be searched in order to create an instance of context class (primary constructor of `Builder` class will be used)
- all `@LocalPattern` expressions will be accessible in that lambda, and their termination will be delegated to the newly created instance of `Builder` class
- `parameter = builder` - means that during termination that created instance of `Builder` class will be passed as `builder` parameter of target function `func` (e.g. to handle collected data)

## Context mediator class
The processor generates a special *context mediator class* with declarations providing support for local patterns originally declared in a context class.
It is an instance of *context mediator class* that acts as a receiver of lambda of an extension point. *Context mediator class* passes all terminations to the instance of context class.

Among other reasons this helps to hide context implementation details (intermediate variables, mutable lists, etc.) from a DSL user, providing a clean and well-defined interface inside an extension point lambda.

## More examples

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

@Pattern("football team name @Extend(context = footballTeamBuilder(), parameter = builder) {}")
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
}
```

