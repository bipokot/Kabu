## Example project

### Modules
Example project contains 2 modules:
- [simple](https://github.com/bipokot/KabuExamples/tree/master/simple) - demonstrates basic pattern syntax capabilities
- [complex](https://github.com/bipokot/KabuExamples/tree/master/complex) - shows how to build more complex DSLs via patterns with extension points

Each example has a unique permanent code (`Example-XXX`).

### Pattern samples from actual tests
                           
Take a look at [pattern samples](samples) used to test Kabu itself.
                                   
Each sample consists of three parts:
1. `raw` - pattern itself and encoded parameters of target function placed after `//` separator
2. `sample` - actual runtime Kotlin statement (or expression) which must match designated pattern
3. `termination` - arguments which will be passed to the target function during termination 
                                          
For example:
```json
{
  "raw" : "s..b + i // s b i",
  "sample" : "\"bbb\"..true + 13",
  "termination" : "bbb, true, 13"
}
```
may be interpreted as following:
```kotlin
@GlobalPattern("s..b + i")
fun targetFunction(s: String, b: Boolean, i: Int) {
    print(listOf(s, b, i).joinToString())
}

fun main() {
    "bbb"..true + 13 // prints: bbb, true, 13     
}
```
