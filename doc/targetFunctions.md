
# Target functions
Target functions are functions which have been annotated with `@Pattern`/`@LocalPattern`.

## Restrictions
These types of functions can not be used as target functions:
- anonymous or local functions
- generic functions
- functions with following modifiers:
    - inline
    - operator
    - suspend
    - tailrec

Some restrictions may be revised later.

### @Pattern target functions
Target functions annotated with `@Pattern` must be:
- top level functions
- with `public` or `internal` visibility

### @LocalPattern target functions
Target functions annotated with `@LocalPattern` must be:
- member functions of non-abstract top level classes
- with `public` or `internal` visibility

## Returned type
If a target function returns a value, then the type of whole expression corresponding to the target function's pattern will be the same as return type of the target function.

```kotlin
// Example-002

@Pattern("string * count")
fun repeatString(count: Int, string: String) = buildString {
    repeat(count) { append(string) }
}

fun main() {
    println("abc" * 3) // prints "abcabcabc"
}
```

## Parameters

### Extension receivers
You can annotate a target function with an extension receiver. Extension receiver must be provided via one of *receiver providing mechanisms*.

#### Receiver providing mechanisms
To pass the value for receiver during termination you can:
- use `this` identifier somewhere in a pattern (thus defining a certain place for a receiver object in the expression)
- use a *receiver catcher*
	- receiver catcher is unmatched identifier of a pattern that "brings" a receiver value to termination (this keyword will be declared as an extension property on target function's receiver type, so the expression will be available only there where a receiver of required type is available)
	- a first unmatched identifier of target function pattern will be used as *receiver catcher* if necessary. If there are no unmatched identifiers in a pattern - add one.

**Example of receiver catcher usage**
```kotlin
// Example-012

class Scope {
    val isDebug = true

    fun logMessage(message: String) {
        println(message)
    }
}

@Pattern("debug (messageBuilder)")
fun Scope.debugPostponed(messageBuilder: () -> String) {
    if (isDebug) logMessage(messageBuilder())
}

fun main() {
    with(Scope()) {
        debug { "message" }
    }
}
```

**Example of `this` identifier usage**
==examples==

#### Restrictions
- multiple receivers are not supported
- receivers for `@LocalPattern`-annotated functions are not supported

### Parameter types (including an extension receiver)

#### Nullable types
You can use nullable types in your function signature.

See `Example-011`.

#### Generic types
Generic parameter types are not fully supported yet. You can use parameters with generic types with type parameters in `invariant` position only.

#### Functional types
You can use functional types (with optional parameters/receivers) to receive an object of functional type in a target function and use it appropriately (e.g. call its `invoke` under some condition).

See `Example-011`.

There can be cases when usage of functional parameters in a pattern makes the pattern erroneous. Pattern "foo bar" in the following example can not be recognized as valid Kotlin statement:
      
```kotlin
@Pattern("foo bar")
fun function(bar: () -> Unit) {
    bar()
}
```

Current workaround for this is to surround functional parameter name in a pattern with braces:

```kotlin
@Pattern("foo (bar)")
fun function(bar: () -> Unit) {
    bar()
}
```

### Arbitrary order of target function parameters
Target function parameters can be placed in any order regardless of order used in a pattern. Exception is when some of `OperatorInfo` parameters are present in a function signature, then the order of function parameters must match those in the pattern.
            
See `Example-014`, `Example-002`.
