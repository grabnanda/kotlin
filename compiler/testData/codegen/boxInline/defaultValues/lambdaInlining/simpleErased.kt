// FILE: 1.kt
package test

@Suppress("NOT_YET_SUPPORTED_IN_INLINE")
inline fun inlineFun(lambda: () -> Any = { "OK" }): Any {
    return lambda()
}

// FILE: 2.kt

import test.*

fun box(): String {
    return inlineFun() as String
}