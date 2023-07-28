package io.kabu.backend.integration

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.node.TypeNode

@JvmName("areParametersPlatformClashing_NameAndType")
fun areParametersPlatformClashing(list1: List<NameAndType>, list2: List<NameAndType>): Boolean {
    val mappedList1 = list1.map { it.typeNode }
    val mappedList2 = list2.map { it.typeNode }
    return areParametersPlatformClashing(mappedList1, mappedList2)
}

@JvmName("areParametersPlatformClashing_TypeNode")
fun areParametersPlatformClashing(list1: List<TypeNode>, list2: List<TypeNode>): Boolean {
    return list1.size == list2.size &&
        list1.zip(list2).all { areTypesPlatformClashing(it.first, it.second) }
}

@JvmName("areParametersEqual_NameAndType")
fun areParametersEqual(list1: List<NameAndType>, list2: List<NameAndType>): Boolean {
    val mappedList1 = list1.map { it.typeNode }
    val mappedList2 = list2.map { it.typeNode }
    return areParametersEqual(mappedList1, mappedList2)
}

@JvmName("areParametersEqual_TypeNode")
fun areParametersEqual(list1: List<TypeNode>, list2: List<TypeNode>): Boolean {
    return list1.size == list2.size &&
        list1.zip(list2).all { equalByTypes(it.first, it.second) }
}

private fun areTypesPlatformClashing(first: TypeNode, second: TypeNode): Boolean {
    val typeName1: TypeName = first.typeName
    val typeName2: TypeName = second.typeName

    val typeName1isClassLike = typeName1 is ClassName || typeName1 is ParameterizedTypeName
    val typeName2isClassLike = typeName2 is ClassName || typeName2 is ParameterizedTypeName

    return when {
        typeName1isClassLike && typeName2isClassLike -> {
            val rawClassName1 = rawClassName(typeName1)
            val rawClassName2 = rawClassName(typeName2)

            // raw classes is enough for "platform clashing" check
            rawClassName1 == rawClassName2
        }
        typeName1 is LambdaTypeName && typeName2 is LambdaTypeName -> {
            true //todo treating all functional types as conflicting for now
        }

        else -> false
    }
}

private fun equalByTypes(first: TypeNode, second: TypeNode): Boolean {
    return first.typeName == second.typeName
}

private fun rawClassName(typeName: TypeName): ClassName =
    (typeName as? ClassName)
        ?: (typeName as? ParameterizedTypeName)?.rawType
        ?: error("No raw class name for $typeName")
