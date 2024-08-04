
# Pattern syntax

Pattern syntax generally corresponds to a Kotlin *statement* syntax (with some exceptions). No control flow structures are allowed (`if`, `while`, `for`, `try-catch`, etc.).

## Supported operations
Following operations are supported:
- call (invoke): `foo(a, b, c)`
- indexed access (get/set): `foo[a, b, c]`
- access: `foo.bar`
- unary prefix operators
	- `-`: `-foo`
	- `+`: `+foo`
	- `!`: `!foo`
- infix functions: `foo func bar`
- binary operations
	- multiplicative
		- `*`: `foo * bar`
        - `/`: `foo / bar`
        - `%`: `foo % bar`
    - additive
		- `+`: `foo + bar`
        - `-`: `foo - bar`
    - range
	    - `..`: `foo .. bar`
	    - `..<`: `foo ..< bar`
    - inclusion checks
    	- `in`: `foo in bar`
    	- `!in`: `foo !in bar`
    - comparison checks
    	- `>`: `foo > bar`
    	- `<`: `foo < bar`
    	- `>=`: `foo >= bar`
    	- `<=`: `foo <= bar`
    - augmented assignments:
	    - `+=`: `foo += bar`
        - `-=`: `foo -= bar`
        - `*=`: `foo *= bar`
        - `/=`: `foo /= bar`
        - `%=`: `foo %= bar`
    - assignments
	    - regular: `foo = bar` (see `Example-017`)
	    - indexed: `foo[a, b, c] = bar`

Not supported operations:
- postfix/prefix increment/decrement operators: `foo++`, `foo--`, `++foo`, `--foo`
- equality checks: `foo == bar`, `foo != bar`
- conjunctions: `foo && bar`
- disjunctions: `foo || bar`
- spread operator: `foo(*bar)`

## Functional literals
You can use lambdas as building blocks of patterns in all possible ways. Only first statement in a lambda inside a pattern is taken into account (other statements are ignored).
- `foo { a * b }`
- `a + {{{ b / c }}}`
- `{ foo } / !{ bar }`

## Extension points
> See [documentation on pattern extending](patternExtension.md)

A `@Extend`-annotated empty lambda serves as an *extension point* of a pattern. A pattern can have multiple extension points.

## Pattern samples
Explore [a project with a set of examples](https://github.com/bipokot/KabuExamples) and take a look at samples used to test Kabu itself.

