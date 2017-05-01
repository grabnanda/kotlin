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

package org.jetbrains.kotlin.javac.wrappers.trees

import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaPackage
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class TreeBasedPackage(val name: String, val javac: JavacWrapper) : JavaPackage {

    override val fqName: FqName
        get() = FqName(name)

    override val subPackages: Collection<JavaPackage>
        get() = javac.findSubPackages(fqName)

    override fun getClasses(nameFilter: (Name) -> Boolean) = javac.findClassesFromPackage(fqName)
            .filter { nameFilter(it.fqName!!.shortName()) }

    override fun equals(other: Any?) = (other as? TreeBasedPackage)?.name == name

    override fun hashCode() = name.hashCode()

    override fun toString() = name

}