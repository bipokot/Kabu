package io.kabu.frontend.ksp.processor.util.builder.validator

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.isProtected
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility
import io.kabu.frontend.ksp.processor.util.areNotSupported

class ContextClassFoundByAnnotatedConstructorValidator : ContextClassValidator() {

    override fun validate(clazz: KSClassDeclaration, role: String?) {
        @Suppress("NAME_SHADOWING") val role = role ?: "<unknown>"

        notSupportedClassKinds(
            clazz, role,
            ClassKind.ENUM_CLASS, // tested
            ClassKind.ENUM_ENTRY,
            ClassKind.OBJECT, // tested
            ClassKind.ANNOTATION_CLASS, // tested
        )
        notSupportedClassVisibilities(
            clazz, role,
            Visibility.LOCAL, // tested
            Visibility.PRIVATE, // tested
            Visibility.PROTECTED, // tested
        )
        notSupportedClassModifiers(
            clazz, role,
            Modifier.ABSTRACT, // tested
            Modifier.ACTUAL,
            Modifier.EXPECT,
            Modifier.INLINE, // tested
            Modifier.INNER, // tested
            Modifier.JAVA_NATIVE,
            Modifier.PRIVATE, // tested
            Modifier.PROTECTED, // tested
            Modifier.SEALED, // tested
            Modifier.VALUE, // tested
        )
        areNotSupported(clazz.isLocal(), clazz) { "Local classes as $role" } // tested
        areNotSupported(clazz.isPrivate(), clazz) { "Private classes as $role" } // tested
        areNotSupported(clazz.isProtected(), clazz) { "Protected classes as $role" } // tested
        areNotSupported(clazz.isCompanionObject, clazz) { "Companion objects as $role" } // tested
        areNotSupported(clazz.isActual, clazz) { "Classes with keyword 'actual' as $role" }
        areNotSupported(clazz.isExpect, clazz) { "Classes with keyword 'expect' as $role" }
//        areNotSupported(clazz.typeParameters.isNotEmpty(), clazz) { "Parameterized classes as $role" } // supported
        areNotSupported(clazz.isAbstract(), clazz) { "Abstract classes as $role" } // tested
    }
}
