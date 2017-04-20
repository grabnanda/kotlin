/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.components

import com.sun.tools.javac.tree.JCTree
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.load.java.components.JavaPropertyInitializerEvaluator
import org.jetbrains.kotlin.load.java.structure.JavaField
import org.jetbrains.kotlin.resolve.constants.ConstantValueFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.wrappers.symbols.SymbolBasedField
import org.jetbrains.kotlin.wrappers.trees.TreeBasedClassifierTypeWithoutTypeArgument
import org.jetbrains.kotlin.wrappers.trees.TreeBasedField
import org.jetbrains.kotlin.wrappers.trees.TreeBasedPrimitiveType

class JavacBasedPropertyInitializerEvaluator : JavaPropertyInitializerEvaluator {

    override fun getInitializerConstant(field: JavaField, descriptor: PropertyDescriptor) = when (field) {
        is SymbolBasedField<*> -> field.constant(descriptor)
        is TreeBasedField<*> -> field.constant(descriptor)
        else -> null
    }

    override fun isNotNullCompileTimeConstant(field: JavaField) = when(field) {
        is SymbolBasedField<*> -> field.isCompileTimeConstant()
        is TreeBasedField<*> -> field.isCompileTimeConstant()
        else -> false
    }

    private fun SymbolBasedField<*>.constant(descriptor: PropertyDescriptor) = value?.let {
        ConstantValueFactory(descriptor.builtIns).createConstantValue(it)
    }

    private fun TreeBasedField<*>.constant(descriptor: PropertyDescriptor) = value?.let {
        if (isCompileTimeConstant() && it is JCTree.JCLiteral) {
            ConstantValueFactory(descriptor.builtIns).createConstantValue(it.value)
        } else null
    }

    private fun SymbolBasedField<*>.isCompileTimeConstant() = value?.let {
        val typeMirror = type.typeMirror

        (typeMirror.kind.isPrimitive || typeMirror.toString() == "java.lang.String")
    } ?: false

    private fun TreeBasedField<*>.isCompileTimeConstant() = value?.let {
        val type = this.type

        isFinal && ((type is TreeBasedPrimitiveType<*>) || (type is TreeBasedClassifierTypeWithoutTypeArgument<*> && type.canonicalText == "java.lang.String"))
    } ?: false

}