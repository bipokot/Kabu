package io.kabu.backend.integration.detector.user.legacy

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.integration.detector.user.legacy.types.StandardMethod
import io.kabu.backend.integration.detector.user.legacy.types.StandardProperty
import io.kabu.backend.provider.provider.AbstractProvider
import io.kabu.backend.util.fromJSON
import io.kabu.backend.util.poet.RegularSerializedType
import io.kabu.backend.util.poet.SerializedType
import io.kabu.backend.util.save
import io.kabu.backend.util.spacedValue
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

open class TypeConflictChecker(private val klass: KClass<*>) : ConflictChecker() {

    protected val typeName: String = klass.simpleName!!
    protected val functions = getMemberFunctions()
    protected val properties = getMemberProperties()

    //todo implementation should not be a part of release code (as we can't write into resources) - move to tests
    override fun cache() {
        val typeDirectory = File("src/main/resources${typeResourcePath(klass)}")
            .also { it.mkdirs() }

        val declaredMethods = klass.obtainMemberFunctions()
        save(declaredMethods, typeDirectory.resolve(MEMBER_FUNCTIONS_CACHE_FILENAME).absolutePath)

        val declaredProperties = klass.obtainMemberProperties()
        save(declaredProperties, typeDirectory.resolve(MEMBER_PROPERTIES_CACHE_FILENAME).absolutePath)
    }

    override fun isApplicableTo(request: OperatorDeclarationRequest): Boolean {
        return request.rawProviders.left.type.toString() == klass.qualifiedName
    }

    override fun check(request: OperatorDeclarationRequest) {
        val clashingStandardFunction = findFirstClashingFunction(request)

        if (clashingStandardFunction != null) ownTypeConflict(request, clashingStandardFunction)
    }

    @Suppress("UNCHECKED_CAST")
    private fun findFirstClashingFunction(request: OperatorDeclarationRequest): StandardMethod? {
        val operatorFunctionName = request.operator.overriding.function
        val parametersWithoutReceiver = request.rawProviders.providersList.drop(1)

        return functions.find { standardMethod ->
            operatorFunctionName == standardMethod.name &&
                    parametersAreCompatible(
                        standardMethod.parameterTypes,
                        parametersWithoutReceiver as List<AbstractProvider>
                    )
        }
    }

    private fun parametersAreCompatible(
        standardFunctionParameters: List<SerializedType>,
        requestedOperatorParameters: List<AbstractProvider>,
    ): Boolean {
        return standardFunctionParameters.size == requestedOperatorParameters.size
            && standardFunctionParameters.zip(requestedOperatorParameters)
            .all { (standardParameter, requestedParameter) ->
                standardParameter.toTypeName().toString() == requestedParameter.type.toString()
            }
    }

    protected fun ownTypeConflict(request: OperatorDeclarationRequest, standardMethod: StandardMethod) {
        diagnosticError(
            "Operator '${request.operator.symbol}' conflicts " +
                "with own ${typeName.spacedValue()}type declaration: $standardMethod",
            request.operator
        )
    }

    protected fun ownTypeConflict(request: OperatorDeclarationRequest, functionName: String) {
        diagnosticError(
            "Operator '${request.operator.symbol}' conflicts " +
                "with own ${typeName.spacedValue()}type declaration: '$functionName'",
            request.operator
        )
    }

    private fun KClass<*>.obtainMemberProperties() = memberProperties.map { property ->
        val serializedContainingType = klass.asClassName().toRegularSerializedType()
        StandardProperty(serializedContainingType, property.name)
    }

    private fun KClass<*>.obtainMemberFunctions() = memberFunctions.map { callable ->
        val receiver: TypeName? = callable.parameters
            .find { it.kind == KParameter.Kind.EXTENSION_RECEIVER }
            ?.type?.asTypeName()

        val params = callable.parameters
            .filterNot { it.kind == KParameter.Kind.INSTANCE }
            .map {
                (it.type.asTypeName() as ClassName).toRegularSerializedType()
            }

        val serializedContainingType = klass.asClassName().toRegularSerializedType()
        val serializedReceiver = (receiver as ClassName?)?.toRegularSerializedType()

        StandardMethod(serializedContainingType, callable.name, serializedReceiver, params)
    }

    private fun ClassName.toRegularSerializedType() = RegularSerializedType(
        packageName = packageName,
        className = simpleName,
        typeParameters = emptyList(), //todo
        nullable = isNullable
    )


    private fun getMemberFunctions(): List<StandardMethod> {
        return javaClass.getResource("${typeResourcePath(klass)}/$MEMBER_FUNCTIONS_CACHE_FILENAME")?.readText()
            ?.fromJSON<List<StandardMethod>>()
            ?: emptyList()
    }

    private fun getMemberProperties() =
        javaClass.getResource("${typeResourcePath(klass)}/$MEMBER_PROPERTIES_CACHE_FILENAME")?.readText()
            ?.fromJSON<List<StandardProperty>>()
            ?: emptyList()

    internal companion object {

        private const val TYPES_MEMBERS_CACHE_PATH = "/io/kabu/backend/typeConflictDetectorCache"
        private const val MEMBER_FUNCTIONS_CACHE_FILENAME = "functions.json"
        private const val MEMBER_PROPERTIES_CACHE_FILENAME = "properties.json"

        fun typeResourcePath(klass: KClass<*>) = "$TYPES_MEMBERS_CACHE_PATH/${klass.simpleName}"
    }
}
