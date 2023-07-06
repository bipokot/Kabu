package io.kabu.frontend.ksp.processor.complex.nested.jsonBuilder

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test

/*

buildJson {
    "lastName" toValue 5
    "innerJson" toValue buildJson {
    }
}

fun main() {
    val a = jsonObject {
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
    println(a)
}

*/

class JsonObjectBuilderTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
        
            @DslMarker
            annotation class JsonDsl
            
            open class JsonNode
            
            data class JsonObject(
                val properties: Map<String, Any>
            ) : JsonNode() {
                override fun toString() = properties.toString()
            }
            
            data class JsonArray(
                val array: List<Any>
            ) : JsonNode() {
                override fun toString() = array.toString()
            }
            
            @JsonDsl
            class JsonObjectBuilder {
            
                val map = mutableMapOf<String, Any>()
            
                @LocalPattern("propertyName - value")
                fun addObjectProperty(propertyName: String, value: Any) {
                    map[propertyName] = value
                }
            }
            
            @JsonDsl
            class JsonArrayBuilder {
            
                val list = mutableListOf<Any>()
            
                @LocalPattern("+ value")
                fun addArrayElement(value: Any) {
                    list.add(value)
                }
            }
            
            @Pattern("jsonObject @Extend(context = objectBuilder(), parameter = objectBuilder) {}")
            fun createJsonObject(objectBuilder: JsonObjectBuilder) = JsonObject(objectBuilder.map)
            
            @Pattern("jsonArray @Extend(context = arrayBuilder(), parameter = arrayBuilder) {}")
            fun createJsonArray(arrayBuilder: JsonArrayBuilder) = JsonArray(arrayBuilder.list)
            
            @ContextCreator("objectBuilder")
            fun objectContextCreator() = JsonObjectBuilder()
            
            @ContextCreator("arrayBuilder")
            fun arrayContextCreator() = JsonArrayBuilder()

        """,
        sample("""
            val a = jsonObject {
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
            print(a)           
        """) - Termination("{first={inner=101}, second=[a, [1, {deepest=1, object=2}], c]}")
    )
}
