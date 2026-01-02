package at.orchaldir.gm.app.html

import io.ktor.http.*

fun combine(param0: String, param1: String) = "$param0-$param1"
fun combine(param0: String, param1: String, param2: String) = "$param0-$param1-$param2"
fun combine(param: String, number: Int) = combine(param, number.toString())
fun combine(param0: String, param1: String, number: Int) = combine(param0, param1, number.toString())

inline fun <reified T : Enum<T>> parse(parameters: Parameters, param: String, default: T): T =
    parameters[param]?.let { java.lang.Enum.valueOf(T::class.java, it) } ?: default

inline fun <reified T : Enum<T>> parse(parameters: Parameters, param: String): T? =
    parameters[param]
        ?.let {
            if (it == "") {
                null
            } else {
                java.lang.Enum.valueOf(T::class.java, it)
            }
        }

inline fun <reified T : Enum<T>> parseSet(parameters: Parameters, param: String): Set<T> =
    parameters.getAll(param)?.map { java.lang.Enum.valueOf(T::class.java, it) }?.toSet() ?: emptySet()


// RarityMap



//

fun <T> parseElements(parameters: Parameters, param: String, parseId: (String) -> T) =
    parameters.getAll(param)
        ?.map { parseId(it) }
        ?.toSet()
        ?: emptySet()