
## Unsafe features
"Unsafe" doesn't mean "poorly implemented", it means that unsafe features can be misused by a DSL user inadvertently.

Runtime correctness for unsafe features can be guaranteed only in the case when **runtime expression structure is exactly the same as in the pattern**. Otherwise, some required arguments of target function will be undefined by the moment of termination and an exception will be raised.

### Example of incorrect usage
Given the pattern below
```kotlin
@Pattern("foo { i > s }")
fun bar(i: Int, s: String) {}
```
the following expression `foo { true }` will be syntactically correct (`i > s` has `Boolean` type) but actual values for `i` and `s` will be unknown.

### Processor option
All unsafe features are disabled by default, but can be enabled by setting `ksp.io.kabu.allowUnsafe` option to `true`.

### List of unsafe features
- actual used operator retrieval (comparison/inclusion)
